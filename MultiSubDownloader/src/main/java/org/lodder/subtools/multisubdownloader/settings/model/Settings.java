package org.lodder.subtools.multisubdownloader.settings.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lodder.subtools.sublibrary.control.VideoPatterns;
import org.lodder.subtools.sublibrary.settings.model.MappingSettings;

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
            serieSourceOpensubtitles, serieSourceLocal, serieSourceSubsMax;
    private boolean autoUpdateMapping;
    private SettingsProcessEpisodeSource processEpisodeSource;
    private MappingSettings mappingSettings;
    private Map<String, Integer> sortWeights;
    private boolean optionsMinAutomaticSelection;
    private int optionsMinAutomaticSelectionValue;
    private UpdateCheckPeriod updateCheckPeriod;
    private boolean optionsDefaultSelection;
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

    public boolean isOptionsAlwaysConfirm() {
        return optionsAlwaysConfirm;
    }

    public void setOptionsAlwaysConfirm(boolean optionsAlwaysConfirm) {
        this.optionsAlwaysConfirm = optionsAlwaysConfirm;
    }

    public boolean isOptionsShowOnlyFound() {
        return optionsShowOnlyFound;
    }

    public void setOptionsShowOnlyFound(boolean optionsShowOnlyFound) {
        this.optionsShowOnlyFound = optionsShowOnlyFound;
    }

    public void setLastOutputDir(File lastOutputDir) {
        this.lastOutputDir = lastOutputDir;
    }

    public File getLastOutputDir() {
        return lastOutputDir;
    }

    public void setExcludeList(List<SettingsExcludeItem> excludeList) {
        this.excludeList = excludeList;
    }

    public List<SettingsExcludeItem> getExcludeList() {
        return excludeList;
    }

    public void setOptionsStopOnSearchError(boolean optionsStopOnSearchError) {
        this.optionsStopOnSearchError = optionsStopOnSearchError;
    }

    public boolean isOptionsStopOnSearchError() {
        return optionsStopOnSearchError;
    }

    public void setEpisodeLibrarySettings(LibrarySettings episodeLibrarySettings) {
        this.episodeLibrarySettings = episodeLibrarySettings;
    }

    public LibrarySettings getEpisodeLibrarySettings() {
        return episodeLibrarySettings;
    }

    public void setMovieLibrarySettings(LibrarySettings movieLibrarySettings) {
        this.movieLibrarySettings = movieLibrarySettings;
    }

    public LibrarySettings getMovieLibrarySettings() {
        return movieLibrarySettings;
    }

    public String getGeneralProxyHost() {
        return generalProxyHost;
    }

    public void setGeneralProxyHost(String generalProxyHost) {
        this.generalProxyHost = generalProxyHost;
    }

    public int getGeneralProxyPort() {
        return generalProxyPort;
    }

    public void setGeneralProxyPort(int generalProxyPort) {
        this.generalProxyPort = generalProxyPort;
    }

    public boolean isGeneralProxyEnabled() {
        return generalProxyEnabled;
    }

    public void setGeneralProxyEnabled(boolean generalProxyEnabled) {
        this.generalProxyEnabled = generalProxyEnabled;
    }

    public void setDefaultIncomingFolders(List<File> defaultIncomingFolders) {
        this.defaultIncomingFolders = defaultIncomingFolders;
    }

    public List<File> getDefaultIncomingFolders() {
        return defaultIncomingFolders;
    }

    public void setOptionRecursive(boolean optionRecursive) {
        this.optionRecursive = optionRecursive;
    }

    public boolean isOptionRecursive() {
        return optionRecursive;
    }

    public ScreenSettings getScreenSettings() {
        return screenSettings;
    }

    public void setScreenSettings(ScreenSettings screenSettings) {
        this.screenSettings = screenSettings;
    }

    public void setLoginAddic7edEnabled(boolean loginAddic7edEnabled) {
        this.loginAddic7edEnabled = loginAddic7edEnabled;
    }

    public boolean isLoginAddic7edEnabled() {
        return this.loginAddic7edEnabled;
    }

    public void setLoginAddic7edUsername(String loginAddic7edUsername) {
        this.loginAddic7edUsername = loginAddic7edUsername;
    }

    public String getLoginAddic7edUsername() {
        return this.loginAddic7edUsername;
    }

    public void setLoginAddic7edPassword(String loginAddic7edPassword) {
        this.loginAddic7edPassword = loginAddic7edPassword;
    }

    public String getLoginAddic7edPassword() {
        return this.loginAddic7edPassword;
    }

    public void setLoginOpenSubtitlesEnabled(boolean loginOpenSubtitlesEnabled) {
        this.loginOpenSubtitlesEnabled = loginOpenSubtitlesEnabled;
    }

    public boolean isLoginOpenSubtitlesEnabled() {
        return this.loginOpenSubtitlesEnabled;
    }

    public void setLoginOpenSubtitlesUsername(String loginOpenSubtitlesUsername) {
        this.loginOpenSubtitlesUsername = loginOpenSubtitlesUsername;
    }

    public String getLoginOpenSubtitlesUsername() {
        return this.loginOpenSubtitlesUsername;
    }

    public void setLoginOpenSubtitlesPassword(String loginOpenSubtitlesPassword) {
        this.loginOpenSubtitlesPassword = loginOpenSubtitlesPassword;
    }

    public String getLoginOpenSubtitlesPassword() {
        return this.loginOpenSubtitlesPassword;
    }

    public List<File> getLocalSourcesFolders() {
        return localSourcesFolders;
    }

    public void setLocalSourcesFolders(List<File> localSourcesFolders) {
        this.localSourcesFolders = localSourcesFolders;
    }

    public boolean isSerieSourceAddic7ed() {
        return serieSourceAddic7ed;
    }

    public void setSerieSourceAddic7ed(boolean serieSourceAddic7ed) {
        this.serieSourceAddic7ed = serieSourceAddic7ed;
    }

    public boolean isSerieSourceTvSubtitles() {
        return serieSourceTvSubtitles;
    }

    public void setSerieSourceTvSubtitles(boolean serieSourceTvSubtitles) {
        this.serieSourceTvSubtitles = serieSourceTvSubtitles;
    }

    public boolean isSerieSourcePodnapisi() {
        return serieSourcePodnapisi;
    }

    public void setSerieSourcePodnapisi(boolean serieSourcePodnapisi) {
        this.serieSourcePodnapisi = serieSourcePodnapisi;
    }

    public boolean isSerieSourceOpensubtitles() {
        return serieSourceOpensubtitles;
    }

    public void setSerieSourceOpensubtitles(boolean serieSourceOpensubtitles) {
        this.serieSourceOpensubtitles = serieSourceOpensubtitles;
    }

    public boolean isSerieSourceLocal() {
        return serieSourceLocal;
    }

    public void setSerieSourceLocal(boolean serieSourceLocal) {
        this.serieSourceLocal = serieSourceLocal;
    }

    public boolean isAutoUpdateMapping() {
        return autoUpdateMapping;
    }

    public void setAutoUpdateMapping(boolean autoUpdateMapping) {
        this.autoUpdateMapping = autoUpdateMapping;
    }

    public boolean isOptionSubtitleExactMatch() {
        return optionSubtitleExactMatch;
    }

    public void setOptionSubtitleExactMatch(boolean optionSubtitleExactMatch) {
        this.optionSubtitleExactMatch = optionSubtitleExactMatch;
    }

    public boolean isOptionSubtitleKeywordMatch() {
        return optionSubtitleKeywordMatch;
    }

    public void setOptionSubtitleKeywordMatch(boolean optionSubtitleKeywordMatch) {
        this.optionSubtitleKeywordMatch = optionSubtitleKeywordMatch;
    }

    public SettingsProcessEpisodeSource getProcessEpisodeSource() {
        return processEpisodeSource;
    }

    public void setProcessEpisodeSource(SettingsProcessEpisodeSource processEpisodeSource) {
        this.processEpisodeSource = processEpisodeSource;
    }

    public MappingSettings getMappingSettings() {
        return mappingSettings;
    }

    public void setMappingSettings(MappingSettings mappingSettings) {
        this.mappingSettings = mappingSettings;
    }

    public boolean isOptionSubtitleExcludeHearingImpaired() {
        return optionSubtitleExcludeHearingImpaired;
    }

    public void setOptionSubtitleExcludeHearingImpaired(boolean optionSubtitleExcludeHearingImpaired) {
        this.optionSubtitleExcludeHearingImpaired = optionSubtitleExcludeHearingImpaired;
    }

    public boolean isSerieSourceSubsMax() {
        return serieSourceSubsMax;
    }

    public void setSerieSourceSubsMax(boolean serieSourceSubsMax) {
        this.serieSourceSubsMax = serieSourceSubsMax;
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

    public boolean isSerieSource(String subtitleProviderName) {
        // TODO: dynamically inject SubtitleProvider to settings
        return switch (subtitleProviderName) {
            case "Addic7ed" -> this.isSerieSourceAddic7ed();
            case "OpenSubtitles" -> this.isSerieSourceOpensubtitles();
            case "Podnapisi" -> this.isSerieSourcePodnapisi();
            case "SubsMax" -> this.isSerieSourceSubsMax();
            case "TvSubtitles" -> this.isSerieSourceTvSubtitles();
            case "Local" -> this.isSerieSourceLocal();
            default -> false;
        };
    }

    public boolean isOptionsMinAutomaticSelection() {
        return optionsMinAutomaticSelection;
    }

    public void setOptionsMinAutomaticSelection(boolean optionsMinAutomaticSelection) {
        this.optionsMinAutomaticSelection = optionsMinAutomaticSelection;
    }

    public int getOptionsMinAutomaticSelectionValue() {
        return optionsMinAutomaticSelectionValue;
    }

    public void setOptionsMinAutomaticSelectionValue(int optionsMinAutomaticSelectionValue) {
        this.optionsMinAutomaticSelectionValue = optionsMinAutomaticSelectionValue;
    }

    public UpdateCheckPeriod getUpdateCheckPeriod() {
        return updateCheckPeriod;
    }

    public void setUpdateCheckPeriod(UpdateCheckPeriod updateCheckPeriod) {
        this.updateCheckPeriod = updateCheckPeriod;
    }

    public boolean isOptionsDefaultSelection() {
        return optionsDefaultSelection;
    }

    public void setOptionsDefaultSelection(boolean optionsDefaultSelection) {
        this.optionsDefaultSelection = optionsDefaultSelection;
    }

    public List<String> getOptionsDefaultSelectionQualityList() {
        return optionsDefaultSelectionQualityList;
    }

    public void setOptionsDefaultSelectionQualityList(List<String> optionsDefaultSelectionQualityList) {
        this.optionsDefaultSelectionQualityList = optionsDefaultSelectionQualityList;
    }
}
