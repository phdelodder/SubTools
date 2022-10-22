package org.lodder.subtools.multisubdownloader;

import static java.time.temporal.ChronoUnit.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.multisubdownloader.settings.model.UpdateCheckPeriod;
import org.lodder.subtools.sublibrary.ConfigProperties;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateAvailableGithub {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateAvailableGithub.class);

    private final Manager manager;
    private final SettingsControl settingsControl;

    public boolean shouldCheckForNewUpdate(UpdateCheckPeriod updateCheckPeriod) {
        LocalDate latestUpdateCheck = settingsControl.getState().getLatestUpdateCheck();
        settingsControl.getState().setLatestUpdateCheck(LocalDate.now());
        try {
            return switch (updateCheckPeriod) {
                case DAILY -> DAYS.between(latestUpdateCheck, LocalDate.now()) > 0;
                case WEEKLY -> DAYS.between(latestUpdateCheck, LocalDate.now()) > 6;
                case MONTHLY -> DAYS.between(latestUpdateCheck, LocalDate.now()) > 30;
                case MANUAL -> false;
                default -> false;
            };
        } catch (Exception e) {
            LOGGER.error("checkProgram", e);
            settingsControl.getState().setLatestUpdateCheck(latestUpdateCheck);
            return false;
        }
    }

    public Optional<String> getLatestDownloadUrl() {
        return getLatestGithubRelease().map(GitHubRelease::getUrl);
    }

    public boolean isNewVersionAvailable() {
        String currentVersion = ConfigProperties.getInstance().getProperty("version").replace("-SNAPSHOT", "");
        return getLatestGithubRelease().map(githubRelease -> compareVersions(githubRelease.getVersion(), currentVersion) > 0).orElse(false);
    }

    private Optional<GitHubRelease> getLatestGithubRelease() {
        return manager.valueBuilder()
                .cacheType(CacheType.MEMORY)
                .key("GitHub-update")
                .optionalSupplier(() -> {
                    try {
                        Element element = manager.getPageContentBuilder().url("https://github.com/phdelodder/SubTools/releases")
                                .userAgent(null)
                                .cacheType(CacheType.NONE)
                                .getAsJsoupDocument()
                                .selectFirst("#repo-content-turbo-frame .box a[href='/phdelodder/SubTools/releases/latest']");
                        Pattern versionPattern = Pattern.compile("[0-9]*\\.[0-9]\\.[0-9]");
                        String versionText = element.parent().selectFirst("a").text();
                        Matcher matcher = versionPattern.matcher(versionText);
                        matcher.find();
                        String version = matcher.group();
                        String versionBlockUrl = "https://github.com/phdelodder/SubTools/releases/expanded_assets/" + versionText;
                        String url = "https://github.com" + manager.getPageContentBuilder().url(versionBlockUrl)
                                .userAgent(null)
                                .cacheType(CacheType.NONE)
                                .getAsJsoupDocument()
                                .selectFirst(".Box-row a[href$='.jar']").attr("href");
                        return Optional.of(new GitHubRelease(version, url));
                    } catch (Exception e) {
                        LOGGER.error(Messages.getString("LoggingPanel.UpdateCheckFailed"));
                        return Optional.empty();
                    }
                }).getOptional();
    }

    @RequiredArgsConstructor
    @Getter
    private class GitHubRelease implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String version;
        private final String url;
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
