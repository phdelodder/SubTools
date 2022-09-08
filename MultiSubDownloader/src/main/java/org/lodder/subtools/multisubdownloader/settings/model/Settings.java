package org.lodder.subtools.multisubdownloader.settings.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lodder.subtools.sublibrary.control.VideoPatterns;
import org.lodder.subtools.sublibrary.model.Subtitle.SubtitleSource;
import org.lodder.subtools.sublibrary.settings.model.MappingSettings;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Settings {

    private File lastOutputDir;
    private boolean optionsAlwaysConfirm, optionSubtitleExactMatch, optionSubtitleKeywordMatch,
            optionSubtitleExcludeHearingImpaired;
    private boolean optionsShowOnlyFound, optionsStopOnSearchError;
    private List<SettingsExcludeItem> excludeList;
    private LibrarySettings movieLibrarySettings;
    private LibrarySettings episodeLibrarySettings;
    private String generalProxyHost;
    private int generalProxyPort;
    private boolean generalProxyEnabled;
    private List<File> defaultIncomingFolders;
    private List<File> localSourcesFolders;
    private boolean optionRecursive;
    private ScreenSettings screenSettings;
    private boolean loginAddic7edEnabled;
    private String loginAddic7edUsername;
    private String loginAddic7edPassword;
    private boolean loginOpenSubtitlesEnabled;
    private String loginOpenSubtitlesUsername;
    private String loginOpenSubtitlesPassword;
    private boolean serieSourceAddic7ed, serieSourceTvSubtitles, serieSourcePodnapisi,
            serieSourceOpensubtitles, serieSourceLocal, serieSourceSubsMax, serieSourceSubscene;
    private boolean autoUpdateMapping;
    private SettingsProcessEpisodeSource processEpisodeSource;
    private MappingSettings mappingSettings;
    private Map<String, Integer> sortWeights;
    @Getter
    @Setter
    private boolean optionsMinAutomaticSelection;
    @Getter
    @Setter
    private int optionsMinAutomaticSelectionValue;
    @Getter
    @Setter
    private UpdateCheckPeriod updateCheckPeriod;
    @Getter
    @Setter
    private boolean optionsDefaultSelection;
    @Getter
    @Setter
    private List<String> optionsDefaultSelectionQualityList;

    public Settings() {
        super();
        setDefaultIncomingFolders(new ArrayList<File>());
        lastOutputDir = null;
        optionsAlwaysConfirm = false;
        optionsShowOnlyFound = false;
        optionsStopOnSearchError = false;
        setExcludeList(new ArrayList<SettingsExcludeItem>());
        movieLibrarySettings = new LibrarySettings();
        episodeLibrarySettings = new LibrarySettings();
        generalProxyHost = "";
        generalProxyPort = 80;
        generalProxyEnabled = false;
        optionRecursive = false;
        screenSettings = new ScreenSettings();
        localSourcesFolders = new ArrayList<>();
        serieSourceAddic7ed = true;
        serieSourceTvSubtitles = true;
        serieSourcePodnapisi = true;
        serieSourceOpensubtitles = true;
        serieSourceLocal = true;
        autoUpdateMapping = false;
        setOptionSubtitleExactMatch(true);
        setOptionSubtitleKeywordMatch(true);
        setProcessEpisodeSource(SettingsProcessEpisodeSource.TVDB);
        setMappingSettings(new MappingSettings());
        optionsMinAutomaticSelection = false;
        optionsMinAutomaticSelectionValue = 0;
        setOptionsDefaultSelection(false);
        setOptionsDefaultSelectionQualityList(new ArrayList<String>());
    }

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
            case SUBSMAX -> this.isSerieSourceSubsMax();
            case TVSUBTITLES -> this.isSerieSourceTvSubtitles();
            case LOCAL -> this.isSerieSourceLocal();
            case SUBSCENE -> this.isSerieSourceSubscene();
        };
    }
}
