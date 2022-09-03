package org.lodder.subtools.multisubdownloader.settings;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.extra.MemoryFolderChooser;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsExcludeItem;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsExcludeType;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsProcessEpisodeSource;
import org.lodder.subtools.multisubdownloader.settings.model.UpdateCheckPeriod;
import org.lodder.subtools.sublibrary.settings.MappingSettingsControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsControl {

  private Preferences preferences;
  private Settings settings;
  private MappingSettingsControl mappingSettingsCtrl;
  private static final String BACKING_STORE_AVAIL = "BackingStoreAvail";
  private static final Logger LOGGER = LoggerFactory.getLogger(SettingsControl.class);

  public SettingsControl() {
    if (!backingStoreAvailable())
      LOGGER.error("Unable to store preferences, used debug for reason");
    preferences = Preferences.userRoot().node("MultiSubDownloader");
    settings = new Settings();
    mappingSettingsCtrl = new MappingSettingsControl(preferences);
    load();
  }

  private static boolean backingStoreAvailable() {
    Preferences prefs = Preferences.userRoot().node("MultiSubDownloader");
    try {
      boolean oldValue = prefs.getBoolean(BACKING_STORE_AVAIL, false);
      prefs.putBoolean(BACKING_STORE_AVAIL, !oldValue);
      prefs.flush();
    } catch (BackingStoreException e) {
      LOGGER.error("BackingStore is not available, settings could not be loaded using defaults", e);
      return false;
    }
    return true;
  }

  public void store() {
    try {
      // clean up
      preferences.clear();
      // EpisodeLibrary
      storeLibrarySerie();
      // MovieLibrary
      storeLibraryMovie();
      // Settings

      storeDefaultIncomingFolder();

      mappingSettingsCtrl.setMappingSettings(settings.getMappingSettings());
      mappingSettingsCtrl.store();

      storeExcludeSettings();

      storeProxySettings();

      preferences.putBoolean("OptionsAlwaysConfirm", settings.isOptionsAlwaysConfirm());
      preferences.putBoolean("OptionsMinAutomaticSelection",
          settings.isOptionsMinAutomaticSelection());
      preferences.putInt("OptionsMinAutomaticSelectionValue",
          settings.getOptionsMinAutomaticSelectionValue());
      preferences.putBoolean("OptionSubtitleExactMatch", settings.isOptionSubtitleExactMatch());
      preferences.putBoolean("OptionSubtitleKeywordMatch", settings.isOptionSubtitleKeywordMatch());
      preferences.putBoolean("OptionSubtitleExcludeHearingImpaired",
          settings.isOptionSubtitleExcludeHearingImpaired());
      preferences.putBoolean("OptionsShowOnlyFound", settings.isOptionsShowOnlyFound());
      preferences.putBoolean("OptionsStopOnSearchError", settings.isOptionsStopOnSearchError());
      preferences.putBoolean("OptionRecursive", settings.isOptionRecursive());
      preferences.putBoolean("AutoUpdateMapping", settings.isAutoUpdateMapping());
      preferences.put("ProcessEpisodeSource", settings.getProcessEpisodeSource().toString());
      preferences.put("UpdateCheckPeriod", settings.getUpdateCheckPeriod().toString());

      if (MemoryFolderChooser.getInstance().getMemory() != null)
        preferences.put("LastOutputDir", MemoryFolderChooser.getInstance().getMemory()
            .getAbsolutePath());

      storeScreenSettings();

      storeSerieSourcesSettings();

      storeLocalSourcesFolders();

      storeDefaultSelectionSettings();

    } catch (BackingStoreException e) {
      LOGGER.error(e.getMessage(), e);
    }

  }

  private void storeScreenSettings() {
    preferences.putBoolean("ScreenHideEpisode", settings.getScreenSettings().isHideEpisode());
    preferences.putBoolean("ScreenHideFilename", settings.getScreenSettings().isHideFilename());
    preferences.putBoolean("ScreenHideSeason", settings.getScreenSettings().isHideSeason());
    preferences.putBoolean("ScreenHideTitle", settings.getScreenSettings().isHideTitle());
    preferences.putBoolean("ScreenHideType", settings.getScreenSettings().isHideType());
    preferences.putBoolean("ScreenHideWIP", settings.getScreenSettings().isHideWIP());
  }

  private void storeDefaultIncomingFolder() {
    int last;
    last = 0;
    for (int i = 0; i < settings.getDefaultIncomingFolders().size(); i++) {
      LOGGER.trace("SettingsControl, storeDefaultIncomingFolder(), storing {}", settings.getDefaultIncomingFolders().get(i).getAbsolutePath());
      preferences.put("GeneralDefaultIncomingFolder" + i,
          settings.getDefaultIncomingFolders().get(i).getAbsolutePath());
      last++;
    }
    LOGGER.trace("SettingsControl, storeDefaultIncomingFolder(), # stored [{}] DefaultIncomingFolders", last);
    preferences.putInt("lastDefaultIncomingFolder", last);
  }

  private void storeLocalSourcesFolders() {
    int last;
    last = 0;
    for (int i = 0; i < settings.getLocalSourcesFolders().size(); i++) {
      LOGGER.trace("SettingsControl, storeLocalSourcesFolders(), storing {}", settings.getLocalSourcesFolders().get(i).getAbsolutePath());
      preferences.put("LocalSubtitlesSourcesFolders" + i, settings.getLocalSourcesFolders().get(i)
          .getAbsolutePath());
      last++;
    }
    LOGGER.trace("SettingsControl, storeLocalSourcesFolders(), # stored [{}] LocalSourcesFolders", last);
    preferences.putInt("lastLocalSubtitlesSourcesFolder", last);
  }

  private void storeProxySettings() {
    preferences.putBoolean("generalProxyEnabled", settings.isGeneralProxyEnabled());
    preferences.put("generalProxyHost", settings.getGeneralProxyHost());
    preferences.putInt("generalProxyPort", settings.getGeneralProxyPort());
    updateProxySettings();
  }

  private void storeLibrarySerie() {
    preferences.put("EpisodeLibraryBackupSubtitlePath", settings.getEpisodeLibrarySettings()
        .getLibraryBackupSubtitlePath().getAbsolutePath());
    preferences.putBoolean("EpisodeLibraryBackupSubtitle", settings.getEpisodeLibrarySettings()
        .isLibraryBackupSubtitle());
    preferences.putBoolean("EpisodeLibraryBackupUseWebsiteFileName", settings
        .getEpisodeLibrarySettings().isLibraryBackupUseWebsiteFileName());

    preferences.put("EpisodeLibraryAction", settings.getEpisodeLibrarySettings().getLibraryAction()
        .toString());
    preferences.putBoolean("EpisodeLibraryUseTVDBNaming", settings.getEpisodeLibrarySettings()
        .isLibraryUseTVDBNaming());
    preferences.putBoolean("EpisodeLibraryReplaceChars", settings.getEpisodeLibrarySettings()
        .isLibraryReplaceChars());
    preferences.put("EpisodeLibraryOtherFileAction", settings.getEpisodeLibrarySettings()
        .getLibraryOtherFileAction().toString());

    if (settings.getEpisodeLibrarySettings().getLibraryFolder() != null)
      preferences.put("EpisodeLibraryFolder", settings.getEpisodeLibrarySettings()
          .getLibraryFolder().getAbsolutePath());
    preferences.put("EpisodeLibraryStructure", settings.getEpisodeLibrarySettings()
        .getLibraryFolderStructure());
    preferences.putBoolean("EpisodeLibraryRemoveEmptyFolders", settings.getEpisodeLibrarySettings()
        .isLibraryRemoveEmptyFolders());

    preferences.put("EpisodeLibraryFilename", settings.getEpisodeLibrarySettings()
        .getLibraryFilenameStructure());
    preferences.putBoolean("EpisodeLibraryReplaceSpace", settings.getEpisodeLibrarySettings()
        .isLibraryFilenameReplaceSpace());
    preferences.put("EpisodeLibraryReplacingSign", settings.getEpisodeLibrarySettings()
        .getLibraryFilenameReplacingSpaceSign());
    preferences.putBoolean("EpisodeLibraryFolderReplaceSpace", settings.getEpisodeLibrarySettings()
        .isLibraryFolderReplaceSpace());
    preferences.put("EpisodeLibraryFolderReplacingSign", settings.getEpisodeLibrarySettings()
        .getLibraryFolderReplacingSpaceSign());
    preferences.putBoolean("EpisodeLibraryIncludeLanguageCode", settings
        .getEpisodeLibrarySettings().isLibraryIncludeLanguageCode());
    preferences.put("EpisodeLibraryDefaultNlText", settings.getEpisodeLibrarySettings()
        .getDefaultNlText());
    preferences.put("EpisodeLibraryDefaultEnText", settings.getEpisodeLibrarySettings()
        .getDefaultEnText());
  }

  private void storeLibraryMovie() {
    preferences.put("MovieLibraryBackupSubtitlePath", settings.getMovieLibrarySettings()
        .getLibraryBackupSubtitlePath().getAbsolutePath());
    preferences.putBoolean("MovieLibraryBackupSubtitle", settings.getMovieLibrarySettings()
        .isLibraryBackupSubtitle());
    preferences.putBoolean("MovieLibraryBackupUseWebsiteFileName", settings
        .getMovieLibrarySettings().isLibraryBackupUseWebsiteFileName());

    preferences.put("MovieLibraryAction", settings.getMovieLibrarySettings().getLibraryAction()
        .toString());
    preferences.putBoolean("MovieLibraryUseTVDBNaming", settings.getMovieLibrarySettings()
        .isLibraryUseTVDBNaming());
    preferences.putBoolean("MovieLibraryReplaceChars", settings.getMovieLibrarySettings()
        .isLibraryReplaceChars());
    preferences.put("MovieLibraryOtherFileAction", settings.getMovieLibrarySettings()
        .getLibraryOtherFileAction().toString());

    if (settings.getMovieLibrarySettings().getLibraryFolder() != null)
      preferences.put("MovieLibraryFolder", settings.getMovieLibrarySettings().getLibraryFolder()
          .getAbsolutePath());
    preferences.put("MovieLibraryStructure", settings.getMovieLibrarySettings()
        .getLibraryFolderStructure());
    preferences.putBoolean("MovieLibraryRemoveEmptyFolders", settings.getMovieLibrarySettings()
        .isLibraryRemoveEmptyFolders());

    preferences.put("MovieLibraryFilename", settings.getMovieLibrarySettings()
        .getLibraryFilenameStructure());
    preferences.putBoolean("MovieLibraryReplaceSpace", settings.getMovieLibrarySettings()
        .isLibraryFilenameReplaceSpace());
    preferences.put("MovieLibraryReplacingSign", settings.getMovieLibrarySettings()
        .getLibraryFilenameReplacingSpaceSign());
    preferences.putBoolean("MovieLibraryFolderReplaceSpace", settings.getMovieLibrarySettings()
        .isLibraryFolderReplaceSpace());
    preferences.put("MovieLibraryFolderReplacingSign", settings.getMovieLibrarySettings()
        .getLibraryFolderReplacingSpaceSign());
    preferences.putBoolean("MovieLibraryIncludeLanguageCode", settings.getMovieLibrarySettings()
        .isLibraryIncludeLanguageCode());
    preferences.put("MovieLibraryDefaultNlText", settings.getMovieLibrarySettings().getDefaultNlText());
    preferences.put("MovieLibraryDefaultEnText", settings.getMovieLibrarySettings().getDefaultEnText());
  }

  private void storeExcludeSettings() {
    int last;
    last = 0;
    for (int i = 0; i < settings.getExcludeList().size(); i++) {
      preferences.put("ExcludeDescription" + i, settings.getExcludeList().get(i).getDescription());
      preferences.put("ExcludeType" + i, settings.getExcludeList().get(i).getType().toString());
      last++;
    }
    preferences.putInt("lastItemExclude", last);
  }

  private void storeSerieSourcesSettings() {
    preferences.putBoolean("loginAddic7edEnabled", settings.isLoginAddic7edEnabled());
    preferences.put("loginAddic7edUsername", settings.getLoginAddic7edUsername());
    preferences.put("loginAddic7edPassword", settings.getLoginAddic7edPassword());
    preferences.putBoolean("loginOpenSubtitlesEnabled", settings.isLoginOpenSubtitlesEnabled());
    preferences.put("loginOpenSubtitlesUsername", settings.getLoginOpenSubtitlesUsername());
    preferences.put("loginOpenSubtitlesPassword", settings.getLoginOpenSubtitlesPassword());
    preferences.putBoolean("serieSourceAddic7ed", settings.isSerieSourceAddic7ed());
    preferences.putBoolean("serieSourceLocal", settings.isSerieSourceLocal());
    preferences.putBoolean("serieSourceOpensubtitles", settings.isSerieSourceOpensubtitles());
    preferences.putBoolean("serieSourcePodnapisi", settings.isSerieSourcePodnapisi());
    preferences.putBoolean("serieSourceTvSubtitles", settings.isSerieSourceTvSubtitles());
    preferences.putBoolean("serieSourceSubsMax", settings.isSerieSourceSubsMax());
  }

  public void load() {
    // settings
    settings.setLastOutputDir(new File(preferences.get("LastOutputDir", "")));
    settings.setOptionsAlwaysConfirm(preferences.getBoolean("OptionsAlwaysConfirm", false));
    settings.setOptionsMinAutomaticSelection(preferences.getBoolean("OptionsMinAutomaticSelection",
        false));
    settings.setOptionsMinAutomaticSelectionValue(preferences.getInt(
        "OptionsMinAutomaticSelectionValue", 0));
    settings.setOptionSubtitleExactMatch(preferences.getBoolean("OptionSubtitleExactMatch", true));
    settings.setOptionSubtitleKeywordMatch(preferences.getBoolean("OptionSubtitleKeywordMatch",
        true));
    settings.setOptionSubtitleExcludeHearingImpaired(preferences.getBoolean(
        "OptionSubtitleExcludeHearingImpaired", false));
    settings.setOptionsShowOnlyFound(preferences.getBoolean("OptionsShowOnlyFound", false));
    settings.setOptionsStopOnSearchError(preferences.getBoolean("OptionsStopOnSearchError", false));
    settings.setOptionRecursive(preferences.getBoolean("OptionRecursive", false));
    settings.setAutoUpdateMapping(preferences.getBoolean("AutoUpdateMapping", false));
    settings.setProcessEpisodeSource(SettingsProcessEpisodeSource.valueOf(preferences.get(
        "ProcessEpisodeSource", SettingsProcessEpisodeSource.TVDB.toString())));
    settings.setUpdateCheckPeriod(UpdateCheckPeriod.valueOf(preferences.get("UpdateCheckPeriod",
        UpdateCheckPeriod.WEEKLY.toString())));
    // GeneralDefaultIncomingFolders
    loadGeneralDefaultIncomingFolders();
    // Serie
    loadLibrarySerie();
    // movie
    loadLibraryMovie();
    // mapping
    mappingSettingsCtrl.load();
    settings.setMappingSettings(mappingSettingsCtrl.getMappingSettings());
    // exclude settings
    loadExcludeSettings();
    // proxy settings
    loadProxySettings();
    loadScreenSettings();
    loadSerieSourcesSettings();
    loadLocalSourcesFolders();
    loadDefaultSelectionSettings();
  }

  private void loadLocalSourcesFolders() {
    int last;

    last = preferences.getInt("lastLocalSubtitlesSourcesFolder", 0);

    for (int i = 0; i < last; i++) {
      settings.getLocalSourcesFolders().add(
          new File(preferences.get("LocalSubtitlesSourcesFolders" + i, "")));
    }
  }

  private void loadGeneralDefaultIncomingFolders() {
    if (preferences.get("GeneralDefaultIncomingFolder", "").equals("")) {
      int last;

      last = preferences.getInt("lastDefaultIncomingFolder", 0);

      for (int i = 0; i < last; i++) {
        settings.getDefaultIncomingFolders().add(
            new File(preferences.get("GeneralDefaultIncomingFolder" + i, "")));
      }
    } else {
      // compatibility
      settings.getDefaultIncomingFolders().add(
          new File(preferences.get("GeneralDefaultIncomingFolder", "")));
    }
  }

  private void loadProxySettings() {
    settings.setGeneralProxyEnabled(preferences.getBoolean("generalProxyEnabled", false));
    settings.setGeneralProxyHost(preferences.get("generalProxyHost", ""));
    settings.setGeneralProxyPort(preferences.getInt("generalProxyPort", 80));
    updateProxySettings();
  }

  private void loadExcludeSettings() {
    int last;

    last = preferences.getInt("lastItemExclude", 0);
    for (int i = 0; i < last; i++) {
      String description = preferences.get("ExcludeDescription" + i, "");
      String type = preferences.get("ExcludeType" + i, "");
      SettingsExcludeItem sei;
      try {
        sei = new SettingsExcludeItem(description, SettingsExcludeType.valueOf(type));
      } catch (Exception e) {
        sei = new SettingsExcludeItem(description, SettingsExcludeType.FOLDER);
      }

      settings.getExcludeList().add(sei);
    }
    LOGGER.trace("loadExcludeSettings: ExcludeList size {}", settings.getExcludeList().size());
  }

  private void loadLibraryMovie() {
    LibrarySettings moLibSet = new LibrarySettings();

    moLibSet.setLibraryBackupSubtitle(preferences.getBoolean("MovieLibraryBackupSubtitle", false));
    moLibSet.setLibraryBackupSubtitlePath(new File(preferences.get(
        "MovieLibraryBackupSubtitlePath", "")));
    moLibSet.setLibraryBackupUseWebsiteFileName(preferences.getBoolean(
        "MovieLibraryBackupUseWebsiteFileName", false));

    moLibSet.setLibraryAction(LibraryActionType.fromString(preferences
        .get("MovieLibraryAction", "")));
    moLibSet.setLibraryUseTVDBNaming(preferences.getBoolean("MovieLibraryUseTVDBNaming", false));
    moLibSet.setLibraryReplaceChars(preferences.getBoolean("MovieLibraryReplaceChars", false));
    moLibSet.setLibraryOtherFileAction(LibraryOtherFileActionType.fromString(preferences.get(
        "MovieLibraryOtherFileAction", "NOTHING")));

    moLibSet.setLibraryFolder(new File(preferences.get("MovieLibraryFolder", "")));
    moLibSet.setLibraryFolderStructure(checkForOldStructure(preferences.get(
        "MovieLibraryStructure", "")));
    moLibSet.setLibraryRemoveEmptyFolders(preferences.getBoolean("MovieLibraryRemoveEmptyFolders",
        false));

    moLibSet.setLibraryFilenameStructure(checkForOldStructure(preferences.get(
        "MovieLibraryFilename", "")));
    moLibSet.setLibraryFilenameReplaceSpace(preferences.getBoolean("MovieLibraryReplaceSpace",
        false));
    moLibSet.setLibraryFilenameReplacingSpaceSign(preferences.get("MovieLibraryReplacingSign", ""));
    moLibSet.setLibraryFolderReplaceSpace(preferences.getBoolean("MovieLibraryFolderReplaceSpace",
        false));
    moLibSet.setLibraryFolderReplacingSpaceSign(preferences.get("MovieLibraryFolderReplacingSign",
        ""));
    moLibSet.setLibraryIncludeLanguageCode(preferences.getBoolean(
        "MovieLibraryIncludeLanguageCode", false));
    moLibSet.setDefaultEnText(preferences.get("MovieLibraryDefaultEnText", ""));
    moLibSet.setDefaultNlText(preferences.get("MovieLibraryDefaultNlText", ""));
    settings.setMovieLibrarySettings(moLibSet);
  }

  private void loadLibrarySerie() {
    LibrarySettings epLibSet = new LibrarySettings();

    epLibSet
        .setLibraryBackupSubtitle(preferences.getBoolean("EpisodeLibraryBackupSubtitle", false));
    epLibSet.setLibraryBackupSubtitlePath(new File(preferences.get(
        "EpisodeLibraryBackupSubtitlePath", "")));
    epLibSet.setLibraryBackupUseWebsiteFileName(preferences.getBoolean(
        "EpisodeLibraryBackupUseWebsiteFileName", false));

    epLibSet.setLibraryAction(LibraryActionType.fromString(preferences.get("EpisodeLibraryAction",
        "")));
    epLibSet.setLibraryUseTVDBNaming(preferences.getBoolean("EpisodeLibraryUseTVDBNaming", false));
    epLibSet.setLibraryReplaceChars(preferences.getBoolean("EpisodeLibraryReplaceChars", false));
    epLibSet.setLibraryOtherFileAction(LibraryOtherFileActionType.fromString(preferences.get(
        "EpisodeLibraryOtherFileAction", "NOTHING")));

    epLibSet.setLibraryFolder(new File(preferences.get("EpisodeLibraryFolder", "")));
    epLibSet.setLibraryFolderStructure(checkForOldStructure(preferences.get(
        "EpisodeLibraryStructure", "")));
    epLibSet.setLibraryRemoveEmptyFolders(preferences.getBoolean(
        "EpisodeLibraryRemoveEmptyFolders", false));

    epLibSet.setLibraryFilenameStructure(checkForOldStructure(preferences.get(
        "EpisodeLibraryFilename", "")));
    epLibSet.setLibraryFilenameReplaceSpace(preferences.getBoolean("EpisodeLibraryReplaceSpace",
        false));
    epLibSet.setLibraryFilenameReplacingSpaceSign(preferences
        .get("EpisodeLibraryReplacingSign", ""));
    epLibSet.setLibraryFolderReplaceSpace(preferences.getBoolean(
        "EpisodeLibraryFolderReplaceSpace", false));
    epLibSet.setLibraryFolderReplacingSpaceSign(preferences.get(
        "EpisodeLibraryFolderReplacingSign", ""));
    epLibSet.setLibraryIncludeLanguageCode(preferences.getBoolean(
        "EpisodeLibraryIncludeLanguageCode", false));
    epLibSet.setDefaultEnText(preferences.get("EpisodeLibraryDefaultEnText", ""));
    epLibSet.setDefaultNlText(preferences.get("EpisodeLibraryDefaultNlText", ""));
    settings.setEpisodeLibrarySettings(epLibSet);
  }

  private void loadSerieSourcesSettings() {
    settings.setLoginAddic7edEnabled(preferences.getBoolean("loginAddic7edEnabled", false));
    settings.setLoginAddic7edUsername(preferences.get("loginAddic7edUsername", ""));
    settings.setLoginAddic7edPassword(preferences.get("loginAddic7edPassword", ""));
    settings.setLoginOpenSubtitlesEnabled(preferences.getBoolean("loginOpenSubtitlesEnabled", false));
    settings.setLoginOpenSubtitlesUsername(preferences.get("loginOpenSubtitlesUsername", ""));
    settings.setLoginOpenSubtitlesPassword(preferences.get("loginOpenSubtitlesPassword", ""));
    settings.setSerieSourceAddic7ed(preferences.getBoolean("serieSourceAddic7ed", true));
    settings.setSerieSourceLocal(preferences.getBoolean("serieSourceLocal", true));
    settings.setSerieSourceOpensubtitles(preferences.getBoolean("serieSourceOpensubtitles", true));
    settings.setSerieSourcePodnapisi(preferences.getBoolean("serieSourcePodnapisi", true));
    settings.setSerieSourceTvSubtitles(preferences.getBoolean("serieSourceTvSubtitles", true));
    settings.setSerieSourceSubsMax(preferences.getBoolean("serieSourceSubsMax", true));
  }

  private String checkForOldStructure(String oldStructure) {
    switch (oldStructure) {
      case "Show\\Season":
        return "%SHOW NAME%%SEPARATOR%Season %S%";
      case "Show\\Series":
        return "%SHOW NAME%%SEPARATOR%Series %S%";
      case "\\":
        return "%SEPARATOR%";
      case "Show S00E00.extension":
        return "%SHOW NAME% S%SS%E%EE%";
      case "Show S00E00 Title.extension":
        return "%SHOW NAME% S%SS%E%EE% %TITLE%";
      case "Show 00X00 Title.extension":
        return "%SHOW NAME% %SS%X%EE% %TITLE%";
      case "Show - S00E00.extension":
        return "%SHOW NAME% - S%SS%E%EE%";
      case "Show S00E00 Title Quality.extension":
        return "%SHOW NAME% S%SS%E%EE% %TITLE% %QUALITY%";
      case "Movie (Year)":
        return "%MOVIE TITLE% (%YEAR%)";
      case "Year\\Movie":
        return "%YEAR%%SEPARATOR%%MOVIE TITLE%";
    }
    return oldStructure;
  }

  public void exportPreferences(File file) {

    store();
    try (FileOutputStream fos = new FileOutputStream(file)) {
      preferences.exportSubtree(fos);
    } catch (Exception e) {
      LOGGER.error("exportPreferences", e);
    }
  }

  public void importPreferences(File file) {
    try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
      preferences.clear();
      Preferences.importPreferences(is);
      load();
    } catch (Exception e) {
      LOGGER.error("importPreferences", e);
    }
  }

  public Settings getSettings() {
    return settings;
  }

  public void setSettings(Settings settings) {
    this.settings = settings;
    store();
  }

  public void updateProxySettings() {
    if (settings.isGeneralProxyEnabled()) {
      System.getProperties().put("proxySet", "true");
      System.getProperties().put("proxyHost", settings.getGeneralProxyHost());
      System.getProperties().put("proxyPort", settings.getGeneralProxyPort());
    } else {
      System.getProperties().put("proxySet", "false");
    }
  }

  public void loadScreenSettings() {
    settings.getScreenSettings().setHideEpisode(preferences.getBoolean("ScreenHideEpisode", true));
    settings.getScreenSettings().setHideFilename(
        preferences.getBoolean("ScreenHideFilename", false));
    settings.getScreenSettings().setHideSeason(preferences.getBoolean("ScreenHideSeason", true));
    settings.getScreenSettings().setHideTitle(preferences.getBoolean("ScreenHideTitle", true));
    settings.getScreenSettings().setHideType(preferences.getBoolean("ScreenHideType", true));
    settings.getScreenSettings().setHideWIP(preferences.getBoolean("ScreenHideWIP", true));
  }

  /**
   * @throws Throwable
   * 
   */
  public void updateMappingFromOnline() throws Throwable {
    LOGGER.info(Messages.getString("SettingsControl.UpdateMapping"));
    mappingSettingsCtrl.updateMappingFromOnline();
    settings.setMappingSettings(mappingSettingsCtrl.getMappingSettings());
  }

  private void storeDefaultSelectionSettings() {
    int last;
    last = 0;
    for (int i = 0; i < settings.getOptionsDefaultSelectionQualityList().size(); i++) {
      preferences.put("DefaultSelectionQuality" + i, settings
          .getOptionsDefaultSelectionQualityList().get(i));
      last++;
    }
    preferences.putInt("lastItemDefaultSelectionQuality", last);
    
    preferences.putBoolean("DefaultSelectionQualityEnabled", settings.isOptionsDefaultSelection());
  }

  private void loadDefaultSelectionSettings() {
    int last;

    last = preferences.getInt("lastItemDefaultSelectionQuality", 0);

    for (int i = 0; i < last; i++) {
      settings.getOptionsDefaultSelectionQualityList().add(preferences.get("DefaultSelectionQuality" + i, ""));
    }
    
    settings.setOptionsDefaultSelection(preferences.getBoolean("DefaultSelectionQualityEnabled", false));
  }

}
