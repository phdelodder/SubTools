package org.lodder.subtools.multisubdownloader;

import static java.time.temporal.ChronoUnit.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.settings.model.UpdateCheckPeriod;
import org.lodder.subtools.multisubdownloader.util.PropertiesReader;
import org.lodder.subtools.sublibrary.ConfigProperties;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.Manager.ValueBuilderIsPresentIntf;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateAvailableGithub {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateAvailableGithub.class);

    private final static String DOMAIN = "https://github.com";
    private final static String REPO_URI = "/phdelodder/SubTools";
    private final static String REPO_URL = DOMAIN + REPO_URI;

    private final Manager manager;
    private final Settings settings;

    public boolean shouldCheckForNewUpdate(UpdateCheckPeriod updateCheckPeriod) {
        LocalDate lastUpdateCheck = getLastUpdateCheck();
        try {
            boolean shouldCheckForUpdate = switch (updateCheckPeriod) {
                case DAILY -> DAYS.between(lastUpdateCheck, LocalDate.now()) > 0;
                case WEEKLY -> DAYS.between(lastUpdateCheck, LocalDate.now()) > 6;
                case MONTHLY -> DAYS.between(lastUpdateCheck, LocalDate.now()) > 30;
                case MANUAL -> false;
            };
            return shouldCheckForUpdate;
        } catch (Exception e) {
            LOGGER.error("checkProgram", e);
            return false;
        }
    }

    public Optional<String> getLatestDownloadUrl() {
        return switch (settings.getUpdateType()) {
            case STABLE -> getUrlLatestNewStableGithubRelease();
            case NIGHTLY -> getUrlLatestNewNightlyGithubRelease();
        };
    }

    public boolean isNewVersionAvailable() {
        return switch (settings.getUpdateType()) {
            case STABLE -> getUrlLatestNewStableGithubRelease().isPresent();
            case NIGHTLY -> getUrlLatestNewNightlyGithubRelease().isPresent();
        };
    }

    private Optional<String> getUrlLatestNewStableGithubRelease() {
        return manager.valueBuilder()
                .cacheType(CacheType.MEMORY)
                .key("GitHub-update")
                .optionalSupplier(() -> {
                    try {
                        String currentVersion = getVersion();
                        Element element = manager.getPageContentBuilder().url(REPO_URL + "/releases")
                                .userAgent(null)
                                .cacheType(CacheType.NONE)
                                .getAsJsoupDocument()
                                .selectFirst("#repo-content-turbo-frame .box a[href='" + REPO_URI + "/releases/latest']");
                        Pattern versionPattern = Pattern.compile("[0-9]*\\.[0-9]\\.[0-9]");
                        String versionText = element.parent().selectFirst("a").text();
                        Matcher matcher = versionPattern.matcher(versionText);
                        matcher.find();
                        String version = matcher.group();
                        if (isFinalVersion(currentVersion) && compareVersions(version, currentVersion) <= 0) {
                            return Optional.empty();
                        }
                        String versionBlockUrl = REPO_URL + "/releases/expanded_assets/" + versionText;
                        Element artifactElement = manager.getPageContentBuilder().url(versionBlockUrl)
                                .userAgent(null)
                                .cacheType(CacheType.NONE)
                                .getAsJsoupDocument()
                                .selectFirst(".Box-row a[href$='.jar']");
                        String url = DOMAIN + artifactElement.attr("href");
                        updateLastUpdateCheck();
                        return Optional.of(url);
                    } catch (Exception e) {
                        LOGGER.error(Messages.getString("LoggingPanel.UpdateCheckFailed"));
                        return Optional.empty();
                    }
                }).getOptional();
    }

    private Optional<String> getUrlLatestNewNightlyGithubRelease() {
        return manager.valueBuilder()
                .cacheType(CacheType.MEMORY)
                .key("GitHub-update-nightly")
                .optionalSupplier(() -> {
                    try {
                        LocalDateTime buildTista = getBuildTista();

                        Element rowElement = manager.getPageContentBuilder().url(REPO_URL + "/actions?query=branch%3Amaster")
                                .userAgent(null)
                                .cacheType(CacheType.MEMORY)
                                .getAsJsoupDocument()
                                .selectFirst("#partial-actions-workflow-runs .Box-row");
                        LocalDateTime nightlyBuildTista =
                                zonedDateTimeStringToLocalDateTime(rowElement.selectFirst(".d-inline relative-time").attr("datetime"));
                        if (nightlyBuildTista.isBefore(buildTista)) {
                            return Optional.empty();
                        }
                        String url = "https://nightly.link" + rowElement.selectFirst(".Link--primary").attr("href");
                        String downloadUrl = manager.getPageContentBuilder().url(url).cacheType(CacheType.MEMORY).getAsJsoupDocument()
                                .selectFirst("table td a").attr("href");
                        updateLastUpdateCheck();
                        return Optional.of(downloadUrl);
                    } catch (Exception e) {
                        LOGGER.error(Messages.getString("LoggingPanel.UpdateCheckFailed"));
                        return Optional.empty();
                    }
                }).getOptional();
    }

    private LocalDateTime getBuildTista() {
        String timestamp = PropertiesReader.getProperty("build.timestamp");
        return zonedDateTimeStringToLocalDateTime(timestamp);
    }

    private String getVersion() {
        return ConfigProperties.getInstance().getProperty("version");
    }

    private boolean isFinalVersion(String version) {
        return !version.contains("-SNAPSHOT");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static ValueBuilderIsPresentIntf<LocalDate> getUpdateLastUpdateCheckBuilder(Manager manager) {
        return (ValueBuilderIsPresentIntf) manager.valueBuilder().cacheType(CacheType.DISK).key("LastUpdateCheck");
    }

    private void updateLastUpdateCheck() {
        getUpdateLastUpdateCheckBuilder(manager).value(LocalDate.now()).store();
    }

    private LocalDate getLastUpdateCheck() {
        return getUpdateLastUpdateCheckBuilder(manager).valueSupplier(() -> LocalDate.MIN).get();
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
