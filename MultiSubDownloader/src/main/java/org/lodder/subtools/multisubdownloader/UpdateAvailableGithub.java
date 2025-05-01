package org.lodder.subtools.multisubdownloader;

import static java.time.temporal.ChronoUnit.*;
import static org.lodder.subtools.sublibrary.PageContentParams.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.settings.model.UpdateCheckPeriod;
import org.lodder.subtools.multisubdownloader.util.PropertiesReader;
import org.lodder.subtools.multisubdownloader.util.PropertiesReader.PomProperty;
import org.lodder.subtools.sublibrary.ConfigProperties;
import org.lodder.subtools.sublibrary.ConfigProperties.Property;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.Manager.CacheKey;
import org.lodder.subtools.sublibrary.Manager.Value;
import org.lodder.subtools.sublibrary.PageContentParams;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class UpdateAvailableGithub {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateAvailableGithub.class);

    private static final String DOMAIN = "https://github.com";
    private static final String REPO_URI = "/phdelodder/SubTools";
    private static final String REPO_URL = DOMAIN + REPO_URI;

    private final Manager manager;
    private final Settings settings;

    public boolean shouldCheckForNewUpdate(UpdateCheckPeriod updateCheckPeriod) {
        LocalDate lastUpdateCheck = getLastUpdateCheck();
        try {
            return switch (updateCheckPeriod) {
                case DAILY -> DAYS.between(lastUpdateCheck, LocalDate.now()) > 0;
                case WEEKLY -> DAYS.between(lastUpdateCheck, LocalDate.now()) > 6;
                case MONTHLY -> DAYS.between(lastUpdateCheck, LocalDate.now()) > 30;
                case MANUAL -> false;
            };
        } catch (Exception e) {
            LOGGER.error("checkProgram", e);
            return false;
        }
    }

    public Optional<String> getLatestDownloadUrl() {
        return switch (settings.updateType) {
            case STABLE -> getUrlLatestNewStableGithubRelease();
            case NIGHTLY -> getUrlLatestNewNightlyGithubRelease();
        };
    }

    public boolean isNewVersionAvailable() {
        return switch (settings.updateType) {
            case STABLE -> getUrlLatestNewStableGithubRelease().isPresent();
            case NIGHTLY -> getUrlLatestNewNightlyGithubRelease().isPresent();
        };
    }

    private Optional<String> getUrlLatestNewStableGithubRelease() {
        return manager.getCache(CacheType.MEMORY, "GitHub-update")
            .getOptional(() -> {
                try {
                    String currentVersion = getVersion();
                    Element element =
                        manager.getAsJsoupDocument(PageContentParams.params(
                                url:"$REPO_URL/releases",
                                cacheType:CacheType.NONE,
                                userAgent:null))
                        .selectFirstByCss("#repo-content-turbo-frame .box a[href='$REPO_URI/releases/latest']");
                    Pattern versionPattern = Pattern.compile("\\d*\\.\\d\\.\\d");
                    String versionText = element.parent().selectFirstByTag("a").text();
                    Matcher matcher = versionPattern.matcher(versionText);
                    matcher.find();
                    String version = matcher.group();
                    if (isFinalVersion(currentVersion) && compareVersions(version, currentVersion) <= 0) {
                        return Optional.empty();
                    }
                    String versionBlockUrl = REPO_URL + "/releases/expanded_assets/" + versionText;
                    Element artifactElement = manager.getAsJsoupDocument(
                            PageContentParams.params(url:versionBlockUrl, userAgent:null))
                        .selectFirstByCss(".Box-row a[href$='.jar']");
                    String url = DOMAIN + artifactElement.attr("href");
                    updateLastUpdateCheck();
                    return Optional.of(url);
                } catch (Exception e) {
                    if (LOGGER.isTraceEnabled) {
                        LOGGER.trace(Messages.getText("LoggingPanel.UpdateCheckFailed"), e);
                    } else {
                        LOGGER.error(Messages.getText("LoggingPanel.UpdateCheckFailed"));
                    }
                    return Optional.empty();
                }
            });
    }

    private Optional<String> getUrlLatestNewNightlyGithubRelease() {
        return manager.getCache(CacheType.MEMORY, "GitHub-update-nightly")
            .getOptional(() -> {
                try {
                    LocalDateTime buildTista = getBuildTista();

                    Element rowElement =
                        manager.getAsJsoupDocument(PageContentParams.params(
                                url:"$REPO_URL/actions?query=branch%3Amaster",
                                cacheType:CacheType.MEMORY,
                                userAgent:null))
                            .selectFirstByCss("#partial-actions-workflow-runs .Box-row");
                    LocalDateTime nightlyBuildTista = zonedDateTimeStringToLocalDateTime(
                        rowElement.selectFirstByCss(".d-inline relative-time").attr("datetime"));
                    if (nightlyBuildTista.isBefore(buildTista)) {
                        return Optional.empty();
                    }
                    String url =
                        "https://nightly.link" + rowElement.selectFirstByCss(".Link--primary").attr("href");
                    String downloadUrl = manager.getAsJsoupDocument(params(url, CacheType.MEMORY))
                        .selectFirstByCss("table td a")
                        .attr("href");
                    updateLastUpdateCheck();
                    return Optional.of(downloadUrl);
                } catch (Exception e) {
                    if (LOGGER.isTraceEnabled) {
                        LOGGER.trace(Messages.getText("LoggingPanel.UpdateCheckFailed"), e);
                    } else {
                        LOGGER.error(Messages.getText("LoggingPanel.UpdateCheckFailed"));
                    }
                    return Optional.empty();
                }
            });
    }

    private LocalDateTime getBuildTista() {
        String timestamp = PropertiesReader.getProperty(PomProperty.BUILD_TIMESTAMP);
        return zonedDateTimeStringToLocalDateTime(timestamp);
    }

    private String getVersion() {
        return ConfigProperties.getProperty(Property.VERSION);
    }

    private boolean isFinalVersion(String version) {
        return !version.contains("-SNAPSHOT");
    }

    private CacheKey getUpdateLastUpdateCheckCache() {
        return manager.getCache(CacheType.DISK, "LastUpdateCheck");
    }

    private void updateLastUpdateCheck() {
        getUpdateLastUpdateCheckCache().store(Value.of(LocalDate.now()));
    }

    private LocalDate getLastUpdateCheck() {
        return getUpdateLastUpdateCheckCache().get(() -> LocalDate.MIN);
    }

    private LocalDateTime zonedDateTimeStringToLocalDateTime(String dateString) {
        Instant instant = Instant.parse(dateString);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("UTC"));
        return zonedDateTime.toLocalDateTime();
    }

    private int compareVersions(String str1, String str2) {
        String[] vals1 = str1.split("\\.");
        String[] vals2 = str2.split("\\.");
        int i = 0;
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++;
        }

        if (i < vals1.length && i < vals2.length) {
            int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return Integer.signum(diff);
        }

        return Integer.signum(vals1.length - vals2.length);
    }
}
