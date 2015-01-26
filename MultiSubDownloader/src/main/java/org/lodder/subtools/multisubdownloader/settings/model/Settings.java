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
  private boolean optionsAlwaysConfirm, optionSubtitleExcludeHearingImpaired;
  private boolean optionsShowOnlyFound, optionsStopOnSearchError;
  private boolean optionsNoRuleMatchTakeFirst, optionsAutomaticDownloadSelection,
      optionsAutomaticDownloadSelectionQuality, optionsAutomaticDownloadSelectionReleaseGroup;
  private List<SettingsExcludeItem> excludeList;
  private List<String> qualityRuleList;
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
  private boolean serieSourceAddic7ed, serieSourceTvSubtitles, serieSourcePodnapisi,
      serieSourceOpensubtitles, serieSourceLocal, serieSourcePrivateRepo, serieSourceSubsMax;
  private boolean autoUpdateMapping;
  private SettingsProcessEpisodeSource processEpisodeSource;
  private MappingSettings mappingSettings;
  private List<SearchSubtitlePriority> listSearchSubtitlePriority;
  private Map<String, Integer> sortWeights;

  public Settings() {
    super();
    setDefaultIncomingFolders(new ArrayList<File>());
    lastOutputDir = null;
    optionsAlwaysConfirm = false;
    optionsShowOnlyFound = false;
    optionsStopOnSearchError = false;
    optionsNoRuleMatchTakeFirst = false;
    optionsAutomaticDownloadSelection = false;
    qualityRuleList = new ArrayList<String>();
    setExcludeList(new ArrayList<SettingsExcludeItem>());
    movieLibrarySettings = new LibrarySettings();
    episodeLibrarySettings = new LibrarySettings();
    generalProxyHost = "";
    generalProxyPort = 80;
    generalProxyEnabled = false;
    optionRecursive = false;
    screenSettings = new ScreenSettings();
    localSourcesFolders = new ArrayList<File>();
    serieSourceAddic7ed = true;
    serieSourceTvSubtitles = true;
    serieSourcePodnapisi = true;
    serieSourceOpensubtitles = true;
    serieSourceLocal = true;
    serieSourcePrivateRepo = true;
    autoUpdateMapping = false;
    setProcessEpisodeSource(SettingsProcessEpisodeSource.TVDB);
    setMappingSettings(new MappingSettings());
    listSearchSubtitlePriority = new ArrayList<SearchSubtitlePriority>();
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

  public void setQualityRuleList(List<String> qualityRuleList) {
    this.qualityRuleList = qualityRuleList;
  }

  public List<String> getQualityRuleList() {
    return qualityRuleList;
  }

  public void setOptionsNoRuleMatchMatchTakeFirst(boolean optionsNoRuleMatchMatchTakeFirst) {
    this.optionsNoRuleMatchTakeFirst = optionsNoRuleMatchMatchTakeFirst;
  }

  public boolean isOptionsNoRuleMatchTakeFirst() {
    return optionsNoRuleMatchTakeFirst;
  }

  public void setOptionsAutomaticDownloadSelection(boolean optionsAutomaticDownloadSelection) {
    this.optionsAutomaticDownloadSelection = optionsAutomaticDownloadSelection;
  }

  public boolean isOptionsAutomaticDownloadSelection() {
    return optionsAutomaticDownloadSelection;
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

  /**
   * @return the localSourcesFolders
   */
  public List<File> getLocalSourcesFolders() {
    return localSourcesFolders;
  }

  /**
   * @param localSourcesFolders the localSourcesFolders to set
   */
  public void setLocalSourcesFolders(List<File> localSourcesFolders) {
    this.localSourcesFolders = localSourcesFolders;
  }

  /**
   * @return the serieSourceAddic7ed
   */
  public boolean isSerieSourceAddic7ed() {
    return serieSourceAddic7ed;
  }

  /**
   * @param serieSourceAddic7ed the serieSourceAddic7ed to set
   */
  public void setSerieSourceAddic7ed(boolean serieSourceAddic7ed) {
    this.serieSourceAddic7ed = serieSourceAddic7ed;
  }

  /**
   * @return the serieSourceTvSubtitles
   */
  public boolean isSerieSourceTvSubtitles() {
    return serieSourceTvSubtitles;
  }

  /**
   * @param serieSourceTvSubtitles the serieSourceTvSubtitles to set
   */
  public void setSerieSourceTvSubtitles(boolean serieSourceTvSubtitles) {
    this.serieSourceTvSubtitles = serieSourceTvSubtitles;
  }

  /**
   * @return the serieSourcePodnapisi
   */
  public boolean isSerieSourcePodnapisi() {
    return serieSourcePodnapisi;
  }

  /**
   * @param serieSourcePodnapisi the serieSourcePodnapisi to set
   */
  public void setSerieSourcePodnapisi(boolean serieSourcePodnapisi) {
    this.serieSourcePodnapisi = serieSourcePodnapisi;
  }

  /**
   * @return the serieSourceOpensubtitles
   */
  public boolean isSerieSourceOpensubtitles() {
    return serieSourceOpensubtitles;
  }

  /**
   * @param serieSourceOpensubtitles the serieSourceOpensubtitles to set
   */
  public void setSerieSourceOpensubtitles(boolean serieSourceOpensubtitles) {
    this.serieSourceOpensubtitles = serieSourceOpensubtitles;
  }

  /**
   * @return the serieSourceLocal
   */
  public boolean isSerieSourceLocal() {
    return serieSourceLocal;
  }

  /**
   * @param serieSourceLocal the serieSourceLocal to set
   */
  public void setSerieSourceLocal(boolean serieSourceLocal) {
    this.serieSourceLocal = serieSourceLocal;
  }

  /**
   * @return the autoUpdateMapping
   */
  public boolean isAutoUpdateMapping() {
    return autoUpdateMapping;
  }

  /**
   * @param autoUpdateMapping the autoUpdateMapping to set
   */
  public void setAutoUpdateMapping(boolean autoUpdateMapping) {
    this.autoUpdateMapping = autoUpdateMapping;
  }

  /**
   * @return the processEpisodeSource
   */
  public SettingsProcessEpisodeSource getProcessEpisodeSource() {
    return processEpisodeSource;
  }

  /**
   * @param processEpisodeSource the processEpisodeSource to set
   */
  public void setProcessEpisodeSource(SettingsProcessEpisodeSource processEpisodeSource) {
    this.processEpisodeSource = processEpisodeSource;
  }

  /**
   * @return the serieSourcePrivateRepo
   */
  public boolean isSerieSourcePrivateRepo() {
    return serieSourcePrivateRepo;
  }

  /**
   * @param serieSourcePrivateRepo the serieSourcePrivateRepo to set
   */
  public void setSerieSourcePrivateRepo(boolean serieSourcePrivateRepo) {
    this.serieSourcePrivateRepo = serieSourcePrivateRepo;
  }

  /**
   * @return the mappingSettings
   */
  public MappingSettings getMappingSettings() {
    return mappingSettings;
  }

  /**
   * @param mappingSettings the mappingSettings to set
   */
  public void setMappingSettings(MappingSettings mappingSettings) {
    this.mappingSettings = mappingSettings;
  }

  /**
   * @return the optionSubtitleExcludeHearingImpaired
   */
  public boolean isOptionSubtitleExcludeHearingImpaired() {
    return optionSubtitleExcludeHearingImpaired;
  }

  /**
   * @param optionSubtitleExcludeHearingImpaired the optionSubtitleExcludeHearingImpaired to set
   */
  public void setOptionSubtitleExcludeHearingImpaired(boolean optionSubtitleExcludeHearingImpaired) {
    this.optionSubtitleExcludeHearingImpaired = optionSubtitleExcludeHearingImpaired;
  }

  /**
   * @return the listSearchSubtitlePriority
   */
  public List<SearchSubtitlePriority> getListSearchSubtitlePriority() {
    return listSearchSubtitlePriority;
  }

  /**
   * @param listSearchSubtitlePriority the listSearchSubtitlePriority to set
   */
  public void setListSearchSubtitlePriority(List<SearchSubtitlePriority> listSearchSubtitlePriority) {
    this.listSearchSubtitlePriority = listSearchSubtitlePriority;
  }

  public boolean isSerieSourceSubsMax() {
    return serieSourceSubsMax;
  }

  public void setSerieSourceSubsMax(boolean serieSourceSubsMax) {
    this.serieSourceSubsMax = serieSourceSubsMax;
  }

  public boolean isOptionsAutomaticDownloadSelectionQuality() {
    return optionsAutomaticDownloadSelectionQuality;
  }

  public void setOptionsAutomaticDownloadSelectionQuality(
      boolean optionsAutomaticDownloadSelectionQuality) {
    this.optionsAutomaticDownloadSelectionQuality = optionsAutomaticDownloadSelectionQuality;
  }

  public boolean isOptionsAutomaticDownloadSelectionReleaseGroup() {
    return optionsAutomaticDownloadSelectionReleaseGroup;
  }

  public void setOptionsAutomaticDownloadSelectionReleaseGroup(
      boolean optionsAutomaticDownloadSelectionReleaseGroup) {
    this.optionsAutomaticDownloadSelectionReleaseGroup =
        optionsAutomaticDownloadSelectionReleaseGroup;
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
    sortWeights.put("%GROUP%",5);
    for (String keyword : VideoPatterns.QUALITYKEYWORDS) {
      sortWeights.put(keyword, 1);
    }
    return sortWeights;
  }
}
