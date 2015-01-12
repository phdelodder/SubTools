package org.lodder.subtools.multisubdownloader.settings;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import org.lodder.subtools.multisubdownloader.gui.extra.MemoryFolderChooser;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.multisubdownloader.settings.model.SearchSubtitlePriority;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsExcludeItem;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsExcludeType;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsProcessEpisodeSource;
import org.lodder.subtools.sublibrary.logging.Level;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Subtitle.SubtitleSource;
import org.lodder.subtools.sublibrary.settings.MappingSettingsControl;

public class SettingsControl {

  private Preferences preferences;
  private Settings settings;
  private MappingSettingsControl mappingSettingsCtrl;
  private static final String BACKING_STORE_AVAIL = "BackingStoreAvail";

  public SettingsControl() {
    if (!backingStoreAvailable())
      Logger.instance.error("Unable to store preferences, used debug for reason");
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
      Logger.instance.debug(Logger.stack2String(e));
      return false;
    }
    return true;
  }

  public void store() {
    Logger.instance.log("SettingsControl, store()", Level.TRACE);
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

      storeQualityRuleSettings();

      storeProxySettings();

      preferences.putBoolean("OptionsAlwaysConfirm", settings.isOptionsAlwaysConfirm());
      preferences.putBoolean("OptionSubtitleExactMatch", settings.isOptionSubtitleExactMatch());
      preferences.putBoolean("OptionSubtitleKeywordMatch", settings.isOptionSubtitleKeywordMatch());
      preferences.putBoolean("OptionSubtitleExcludeHearingImpaired",
          settings.isOptionSubtitleExcludeHearingImpaired());
      preferences.putBoolean("OptionsShowOnlyFound", settings.isOptionsShowOnlyFound());
      preferences.putBoolean("OptionsStopOnSearchError", settings.isOptionsStopOnSearchError());
      preferences.putBoolean("OptionsAutomaticDownloadSelection",
          settings.isOptionsAutomaticDownloadSelection());
      preferences.putBoolean("OptionsNoRuleMatchTakeFirst",
          settings.isOptionsNoRuleMatchTakeFirst());
      preferences.putBoolean("OptionRecursive", settings.isOptionRecursive());
      preferences.putBoolean("AutoUpdateMapping", settings.isAutoUpdateMapping());
      preferences.put("ProcessEpisodeSource", settings.getProcessEpisodeSource().toString());

      if (MemoryFolderChooser.getInstance().getMemory() != null)
        preferences.put("LastOutputDir", MemoryFolderChooser.getInstance().getMemory()
            .getAbsolutePath());

      storeScreenSettings();

      storeSerieSourcesSettings();

      storeLocalSourcesFolders();

    } catch (BackingStoreException e) {
      Logger.instance.log(Logger.stack2String(e));
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
    Logger.instance.log("SettingsControl, storeDefaultIncomingFolder()", Level.TRACE);
    int last;
    last = 0;
    for (int i = 0; i < settings.getDefaultIncomingFolders().size(); i++) {
      Logger.instance.log("SettingsControl, storeDefaultIncomingFolder(), storing:"
          + settings.getDefaultIncomingFolders().get(i).getAbsolutePath(), Level.TRACE);
      preferences.put("GeneralDefaultIncomingFolder" + i,
          settings.getDefaultIncomingFolders().get(i).getAbsolutePath());
      last++;
    }
    Logger.instance.log("SettingsControl, storeDefaultIncomingFolder(), stored:" + last
        + " DefaultIncomingFolders", Level.TRACE);
    preferences.putInt("lastDefaultIncomingFolder", last);
  }

  private void storeLocalSourcesFolders() {
    Logger.instance.log("SettingsControl, storeLocalSourcesFolders()", Level.TRACE);
    int last;
    last = 0;
    for (int i = 0; i < settings.getLocalSourcesFolders().size(); i++) {
      Logger.instance.log("SettingsControl, storeLocalSourcesFolders(), storing:"
          + settings.getLocalSourcesFolders().get(i).getAbsolutePath(), Level.TRACE);
      preferences.put("LocalSubtitlesSourcesFolders" + i, settings.getLocalSourcesFolders().get(i)
          .getAbsolutePath());
      last++;
    }
    Logger.instance.log("SettingsControl, storeLocalSourcesFolders(), stored:" + last
        + " LocalSourcesFolders", Level.TRACE);
    preferences.putInt("lastLocalSubtitlesSourcesFolder", last);
  }

  private void storeProxySettings() {
    Logger.instance.log("SettingsControl, storeProxySettings()", Level.TRACE);
    preferences.putBoolean("generalProxyEnabled", settings.isGeneralProxyEnabled());
    preferences.put("generalProxyHost", settings.getGeneralProxyHost());
    preferences.putInt("generalProxyPort", settings.getGeneralProxyPort());
    updateProxySettings();
  }

  private void storeLibrarySerie() {
    Logger.instance.log("SettingsControl, storeLibrarySerie()", Level.TRACE);

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
    Logger.instance.log("SettingsControl, storeLibraryMovie()", Level.TRACE);

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
    preferences.put("MovieDefaultNlText", settings.getMovieLibrarySettings().getDefaultNlText());
    preferences.put("MovieDefaultEnText", settings.getMovieLibrarySettings().getDefaultEnText());
  }

  private void storeExcludeSettings() {
    Logger.instance.log("SettingsControl, storeExcludeSettings()", Level.TRACE);
    int last;
    last = 0;
    for (int i = 0; i < settings.getExcludeList().size(); i++) {
      preferences.put("ExcludeDescription" + i, settings.getExcludeList().get(i).getDescription());
      preferences.put("ExcludeType" + i, settings.getExcludeList().get(i).getType().toString());
      last++;
    }
    preferences.putInt("lastItemExclude", last);
  }

  private void storeQualityRuleSettings() {
    Logger.instance.log("SettingsControl, storeQualityRuleSettings()", Level.TRACE);
    int last;
    last = 0;
    for (int i = 0; i < settings.getQualityRuleList().size(); i++) {
      preferences.put("Quality" + i, settings.getQualityRuleList().get(i));
      last++;
    }
    preferences.putInt("lastItemQuality", last);
  }

  private void storeSerieSourcesSettings() {
    Logger.instance.log("SettingsControl, storeAddic7edLoginSettings()", Level.TRACE);
    preferences.putBoolean("loginAddic7edEnabled", settings.isLoginAddic7edEnabled());
    preferences.put("loginAddic7edUsername", settings.getLoginAddic7edUsername());
    preferences.put("loginAddic7edPassword", settings.getLoginAddic7edPassword());
    preferences.putBoolean("serieSourceAddic7ed", settings.isSerieSourceAddic7ed());
    preferences.putBoolean("serieSourceLocal", settings.isSerieSourceLocal());
    preferences.putBoolean("serieSourceOpensubtitles", settings.isSerieSourceOpensubtitles());
    preferences.putBoolean("serieSourcePodnapisi", settings.isSerieSourcePodnapisi());
    preferences.putBoolean("serieSourceTvSubtitles", settings.isSerieSourceTvSubtitles());
    preferences.putBoolean("serieSourcePrivateRepo", settings.isSerieSourcePrivateRepo());
    preferences.putBoolean("serieSourceSubsMax", settings.isSerieSourceSubsMax());

    for (SearchSubtitlePriority prio : settings.getListSearchSubtitlePriority()) {
      switch (prio.getSubtitleSource()) {
        case ADDIC7ED:
          preferences.putInt("serieSourceAddic7edPrio", prio.getPriority());
          break;
        case LOCAL:
          preferences.putInt("serieSourceLocalPrio", prio.getPriority());
          break;
        case OPENSUBTITLES:
          preferences.putInt("serieSourceOpensubtitlesPrio", prio.getPriority());
          break;
        case PODNAPISI:
          preferences.putInt("serieSourcePodnapisiPrio", prio.getPriority());
          break;
        case PRIVATEREPO:
          preferences.putInt("serieSourcePrivateRepoPrio", prio.getPriority());
          break;
        case TVSUBTITLES:
          preferences.putInt("serieSourceTvSubtitlesPrio", prio.getPriority());
          break;
        case SUBSMAX:
          preferences.putInt("serieSourceSubsMaxPrio", prio.getPriority());
          break;
        default:
          break;
      }
    }
  }

  public void load() {
    Logger.instance.log("SettingsControl, load()", Level.TRACE);
    // settings
    settings.setLastOutputDir(new File(preferences.get("LastOutputDir", "")));
    settings.setOptionsAlwaysConfirm(preferences.getBoolean("OptionsAlwaysConfirm", false));
    settings.setOptionSubtitleExactMatch(preferences.getBoolean("OptionSubtitleExactMatch", true));
    settings.setOptionSubtitleKeywordMatch(preferences.getBoolean("OptionSubtitleKeywordMatch",
        true));
    settings.setOptionSubtitleExcludeHearingImpaired(preferences.getBoolean(
        "OptionSubtitleExcludeHearingImpaired", false));
    settings.setOptionsShowOnlyFound(preferences.getBoolean("OptionsShowOnlyFound", false));
    settings.setOptionsStopOnSearchError(preferences.getBoolean("OptionsStopOnSearchError", false));
    settings.setOptionsAutomaticDownloadSelection(preferences.getBoolean(
        "OptionsAutomaticDownloadSelection", false));
    settings.setOptionsNoRuleMatchMatchTakeFirst(preferences.getBoolean(
        "OptionsNoRuleMatchTakeFirst", false));
    settings.setOptionRecursive(preferences.getBoolean("OptionRecursive", false));
    settings.setAutoUpdateMapping(preferences.getBoolean("AutoUpdateMapping", false));
    settings.setProcessEpisodeSource(SettingsProcessEpisodeSource.valueOf(preferences.get(
        "ProcessEpisodeSource", SettingsProcessEpisodeSource.TVDB.toString())));
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
    // quality rules
    loadQualityRuleSettings();
    // proxy settings
    loadProxySettings();
    loadScreenSettings();
    loadSerieSourcesSettings();
    loadLocalSourcesFolders();
  }

  private void loadLocalSourcesFolders() {
    Logger.instance.log("SettingsControl, loadLocalSourcesFolders()", Level.TRACE);
    int last;

    last = preferences.getInt("lastLocalSubtitlesSourcesFolder", 0);

    for (int i = 0; i < last; i++) {
      settings.getLocalSourcesFolders().add(
          new File(preferences.get("LocalSubtitlesSourcesFolders" + i, "")));
    }
  }

  private void loadGeneralDefaultIncomingFolders() {
    Logger.instance.log("SettingsControl, loadGeneralDefaultIncomingFolders()", Level.TRACE);
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
    Logger.instance.log("SettingsControl, loadProxySettings()", Level.TRACE);
    settings.setGeneralProxyEnabled(preferences.getBoolean("generalProxyEnabled", false));
    settings.setGeneralProxyHost(preferences.get("generalProxyHost", ""));
    settings.setGeneralProxyPort(preferences.getInt("generalProxyPort", 80));
    updateProxySettings();
  }

  private void loadQualityRuleSettings() {
    Logger.instance.log("SettingsControl, loadQualityRuleSettings()", Level.TRACE);
    int last;

    last = preferences.getInt("lastItemQuality", 0);

    for (int i = 0; i < last; i++) {
      settings.getQualityRuleList().add(preferences.get("Quality" + i, ""));
    }
  }

  private void loadExcludeSettings() {
    Logger.instance.log("SettingsControl, loadExcludeSettings()", Level.TRACE);
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
    Logger.instance.trace("Settings", "loadExcludeSettings", "ExcludeList Size"
        + settings.getExcludeList().size());
  }

  private void loadLibraryMovie() {
    Logger.instance.log("SettingsControl, loadLibraryMovie()", Level.TRACE);
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
    Logger.instance.log("SettingsControl, loadLibrarySerie()", Level.TRACE);
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
    Logger.instance.log("SettingsControl, loadAddic7edLoginSettings()", Level.TRACE);
    settings.setLoginAddic7edEnabled(preferences.getBoolean("loginAddic7edEnabled", false));
    settings.setLoginAddic7edUsername(preferences.get("loginAddic7edUsername", ""));
    settings.setLoginAddic7edPassword(preferences.get("loginAddic7edPassword", ""));
    settings.setSerieSourceAddic7ed(preferences.getBoolean("serieSourceAddic7ed", true));
    settings.setSerieSourceLocal(preferences.getBoolean("serieSourceLocal", true));
    settings.setSerieSourceOpensubtitles(preferences.getBoolean("serieSourceOpensubtitles", true));
    settings.setSerieSourcePodnapisi(preferences.getBoolean("serieSourcePodnapisi", true));
    settings.setSerieSourceTvSubtitles(preferences.getBoolean("serieSourceTvSubtitles", true));
    settings.setSerieSourcePrivateRepo(preferences.getBoolean("serieSourcePrivateRepo", true));
    settings.setSerieSourceSubsMax(preferences.getBoolean("serieSourceSubsMax", true));

    SearchSubtitlePriority prioAddic7ed =
        new SearchSubtitlePriority(SubtitleSource.ADDIC7ED, preferences.getInt(
            "serieSourceAddic7edPrio", 2));
    SearchSubtitlePriority prioLocal =
        new SearchSubtitlePriority(SubtitleSource.LOCAL, (Integer) preferences.getInt(
            "serieSourceLocalPrio", 2));
    SearchSubtitlePriority prioOpensubtitles =
        new SearchSubtitlePriority(SubtitleSource.OPENSUBTITLES, (Integer) preferences.getInt(
            "serieSourceOpensubtitlesPrio", 2));
    SearchSubtitlePriority prioPodnapisi =
        new SearchSubtitlePriority(SubtitleSource.PODNAPISI, (Integer) preferences.getInt(
            "serieSourcePodnapisiPrio", 2));
    SearchSubtitlePriority prioPrivateRepo =
        new SearchSubtitlePriority(SubtitleSource.PRIVATEREPO, (Integer) preferences.getInt(
            "serieSourcePrivateRepoPrio", 1));
    SearchSubtitlePriority prioTvSubtitles =
        new SearchSubtitlePriority(SubtitleSource.TVSUBTITLES, (Integer) preferences.getInt(
            "serieSourceTvSubtitlesPrio", 2));
    SearchSubtitlePriority prioSubsMax =
        new SearchSubtitlePriority(SubtitleSource.SUBSMAX, (Integer) preferences.getInt(
            "serieSourceSubsMaxPrio", 2));
    List<SearchSubtitlePriority> lPrio = new ArrayList<SearchSubtitlePriority>();

    lPrio.add(prioAddic7ed);
    lPrio.add(prioLocal);
    lPrio.add(prioOpensubtitles);
    lPrio.add(prioPodnapisi);
    lPrio.add(prioPrivateRepo);
    lPrio.add(prioTvSubtitles);
    lPrio.add(prioSubsMax);

    java.util.Collections.sort(lPrio, new Comparator<SearchSubtitlePriority>() {
      public int compare(SearchSubtitlePriority t1, SearchSubtitlePriority t2) {
        return t1.getPriority() - t2.getPriority();
      }
    });

    settings.setListSearchSubtitlePriority(lPrio);
  }

  private String checkForOldStructure(String oldStructure) {
    if (oldStructure.equals("Show\\Season")) {
      return "%SHOW NAME%%SEPARATOR%Season %S%";
    } else if (oldStructure.equals("Show\\Series")) {
      return "%SHOW NAME%%SEPARATOR%Series %S%";
    } else if (oldStructure.equals("\\")) {
      return "%SEPARATOR%";
    } else if (oldStructure.equals("Show S00E00.extension")) {
      return "%SHOW NAME% S%SS%E%EE%";
    } else if (oldStructure.equals("Show S00E00 Title.extension")) {
      return "%SHOW NAME% S%SS%E%EE% %TITLE%";
    } else if (oldStructure.equals("Show 00X00 Title.extension")) {
      return "%SHOW NAME% %SS%X%EE% %TITLE%";
    } else if (oldStructure.equals("Show - S00E00.extension")) {
      return "%SHOW NAME% - S%SS%E%EE%";
    } else if (oldStructure.equals("Show S00E00 Title Quality.extension")) {
      return "%SHOW NAME% S%SS%E%EE% %TITLE% %QUALITY%";
    } else if (oldStructure.equals("Movie (Year)")) {
      return "%MOVIE TITLE% (%YEAR%)";
    } else if (oldStructure.equals("Year\\Movie")) {
      return "%YEAR%%SEPARATOR%%MOVIE TITLE%";
    }
    return oldStructure;
  }

  public void exportPreferences(File file) throws IOException, BackingStoreException {
    Logger.instance.log("SettingsControl, exportPreferences(File file)", Level.TRACE);
    store();
    FileOutputStream fos = new FileOutputStream(file);
    preferences.exportSubtree(fos);
  }

  public void importPreferences(File file) throws IOException, InvalidPreferencesFormatException,
      BackingStoreException {
    Logger.instance.log("SettingsControl, importPreferences(File file)", Level.TRACE);
    InputStream is = new BufferedInputStream(new FileInputStream(file));
    preferences.clear();
    Preferences.importPreferences(is);
    load();
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
    mappingSettingsCtrl.updateMappingFromOnline();
    settings.setMappingSettings(mappingSettingsCtrl.getMappingSettings());
  }

}
