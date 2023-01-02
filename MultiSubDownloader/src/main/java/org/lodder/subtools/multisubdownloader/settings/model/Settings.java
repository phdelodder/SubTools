package org.lodder.subtools.multisubdownloader.settings.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.control.VideoPatterns;
import org.lodder.subtools.sublibrary.data.UserInteractionSettingsIntf;
import org.lodder.subtools.sublibrary.model.SubtitleSource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Settings implements UserInteractionSettingsIntf {

    private File lastOutputDir;
    private boolean optionsAlwaysConfirm;
    private boolean optionSubtitleExactMatch = true;
    private boolean optionSubtitleKeywordMatch = true;
    private boolean optionSubtitleExcludeHearingImpaired;
    private boolean optionsShowOnlyFound, optionsStopOnSearchError;
    private List<SettingsExcludeItem> excludeList = new ArrayList<>();
    private LibrarySettings movieLibrarySettings = new LibrarySettings();
    private LibrarySettings episodeLibrarySettings = new LibrarySettings();
    private String generalProxyHost = "";
    private int generalProxyPort = 80;
    private boolean generalProxyEnabled;
    private List<File> defaultIncomingFolders = new ArrayList<>();
    private List<File> localSourcesFolders = new ArrayList<>();
    private boolean optionRecursive;
    private ScreenSettings screenSettings = new ScreenSettings();
    private boolean loginAddic7edEnabled;
    private String loginAddic7edUsername;
    private String loginAddic7edPassword;
    private boolean loginOpenSubtitlesEnabled;
    private String loginOpenSubtitlesUsername;
    private String loginOpenSubtitlesPassword;
    private boolean serieSourceAddic7ed = true;
    private boolean serieSourceAddic7edProxy = true;
    private boolean serieSourceTvSubtitles = true;
    private boolean serieSourcePodnapisi = true;
    private boolean serieSourceOpensubtitles = true;
    private boolean serieSourceLocal = true;
    private boolean serieSourceSubscene = true;
    private SettingsProcessEpisodeSource processEpisodeSource = SettingsProcessEpisodeSource.TVDB;
    private Map<String, Integer> sortWeights;
    private Language subtitleLanguage;
    private boolean optionsMinAutomaticSelection;
    private int optionsMinAutomaticSelectionValue;
    private UpdateCheckPeriod updateCheckPeriod;
    private UpdateType updateType;
    private boolean optionsDefaultSelection;
    private List<String> optionsDefaultSelectionQualityList = new ArrayList<>();
    private int settingsVersion;
    private boolean optionsConfirmProviderMapping;
    private Language language;

    public List<File> getDefaultFolders() {
        return getDefaultIncomingFolders();
    }

    public boolean hasDefaultFolders() {
        return getDefaultIncomingFolders().size() > 0;
    }

    public Map<String, Integer> getSortWeights() {
        // TODO: user should be able to edit/add these through a panel
        sortWeights = new HashMap<>();
        sortWeights.put("%GROUP%", 5);
        VideoPatterns videoPatterns = new VideoPatterns();
        videoPatterns.getQualityKeywords().forEach(keyword -> sortWeights.put(keyword, 2));
        /* overwrite keywords that should have low weight */
        // keywords that tend to have a lot of different sources:
        sortWeights.put("ts", 1);
        sortWeights.put("dvdscreener", 1);
        sortWeights.put("r5", 1);
        sortWeights.put("cam", 1);
        // encoding says little about the release:
        sortWeights.put("xvid", 1);
        sortWeights.put("divx", 1);
        sortWeights.put("x264", 1);
        sortWeights.put("x265", 1);
        // keywords that might get matched too easily
        // sortWeights.remove("dl");

        return sortWeights;
    }

    public boolean isSerieSource(SubtitleSource sbtitleSource) {
        // TODO: dynamically inject SubtitleProvider to settings
        return switch (sbtitleSource) {
            case ADDIC7ED -> this.isSerieSourceAddic7ed();
            case OPENSUBTITLES -> this.isSerieSourceOpensubtitles();
            case PODNAPISI -> this.isSerieSourcePodnapisi();
            case TVSUBTITLES -> this.isSerieSourceTvSubtitles();
            case LOCAL -> this.isSerieSourceLocal();
            case SUBSCENE -> this.isSerieSourceSubscene();
        };
    }
}
