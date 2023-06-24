package org.lodder.subtools.multisubdownloader.settings;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.prefs.Preferences;
import java.util.stream.IntStream;

import org.apache.commons.lang3.function.TriFunction;
import org.lodder.subtools.multisubdownloader.gui.extra.MemoryFolderChooser;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.multisubdownloader.settings.model.PathOrRegex;
import org.lodder.subtools.multisubdownloader.settings.model.ScreenSettings;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsProcessEpisodeSource;
import org.lodder.subtools.multisubdownloader.settings.model.UpdateCheckPeriod;
import org.lodder.subtools.multisubdownloader.settings.model.UpdateType;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.control.VideoPatterns;
import org.lodder.subtools.sublibrary.util.FileUtils;
import org.lodder.subtools.sublibrary.util.TriConsumer;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

public enum SettingValue {

    // SETTINGS
    SETTINGS_VERSION(createSettingInt()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::getSettingsVersion)
            .valueSetter(Settings::setSettingsVersion)
            .defaultValue(0)),
    LAST_OUTPUT_DIR(createSettingPath()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(settings -> MemoryFolderChooser.getInstance().getMemory())
            .valueSetter(Settings::setLastOutputDir)
            .defaultValue(Path.of(""))),

    GENERAL_DEFAULT_INCOMING_FOLDER(createSettingPath()
            .rootElementFunction(SettingsControl::getSettings)
            .collectionGetter(Settings::getDefaultIncomingFolders)),
    LOCAL_SUBTITLES_SOURCES_FOLDERS(createSettingPath()
            .rootElementFunction(SettingsControl::getSettings)
            .collectionGetter(Settings::getLocalSourcesFolders)),
    EXCLUDE_ITEM(createSetting(PathOrRegex.class)
            .toStringMapper(PathOrRegex::getValue)
            .toObjectMapper(PathOrRegex::new)
            .rootElementFunction(SettingsControl::getSettings)
            .collectionGetter(Settings::getExcludeList)),
    DEFAULT_SELECTION_QUALITY(createSettingEnum(VideoPatterns.Source.class)
            .rootElementFunction(SettingsControl::getSettings)
            .collectionGetter(Settings::getOptionsDefaultSelectionQualityList)),
    DEFAULT_SELECTION_QUALITY_ENABLED(createSettingBoolean()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::isOptionsDefaultSelection)
            .valueSetter(Settings::setOptionsDefaultSelection)
            .defaultValue(false)),

    OPTIONS_LANGUAGE(createSettingEnum(Language.class)
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::getLanguage)
            .valueSetter(Settings::setLanguage)
            .defaultValue(Language.ENGLISH)),
    OPTIONS_ALWAYS_CONFIRM(createSettingBoolean()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::isOptionsAlwaysConfirm)
            .valueSetter(Settings::setOptionsAlwaysConfirm)
            .defaultValue(false)),
    OPTIONS_CONFIRM_MAPPING(createSettingBoolean()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::isOptionsConfirmProviderMapping)
            .valueSetter(Settings::setOptionsConfirmProviderMapping)
            .defaultValue(true)),
    OPTIONS_MIN_AUTOMATIC_SELECTION(createSettingBoolean()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::isOptionsMinAutomaticSelection)
            .valueSetter(Settings::setOptionsMinAutomaticSelection)
            .defaultValue(false)),
    OPTIONS_MIN_AUTOMATIC_SELECTION_VALUE(createSettingInt()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::getOptionsMinAutomaticSelectionValue)
            .valueSetter(Settings::setOptionsMinAutomaticSelectionValue)
            .defaultValue(0)),
    OPTION_SUBTITLE_EXACT_MATCH(createSettingBoolean()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::isOptionSubtitleExactMatch)
            .valueSetter(Settings::setOptionSubtitleExactMatch)
            .defaultValue(true)),
    OPTION_SUBTITLE_KEYWORD_MATCH(createSettingBoolean()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::isOptionSubtitleKeywordMatch)
            .valueSetter(Settings::setOptionSubtitleKeywordMatch)
            .defaultValue(true)),
    OPTION_SUBTITLE_EXCLUDE_HEARING_IMPAIRED(createSettingBoolean()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::isOptionSubtitleExcludeHearingImpaired)
            .valueSetter(Settings::setOptionSubtitleExcludeHearingImpaired)
            .defaultValue(true)),
    OPTIONS_SHOW_ONLY_FOUND(createSettingBoolean()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::isOptionsShowOnlyFound)
            .valueSetter(Settings::setOptionsShowOnlyFound)
            .defaultValue(true)),
    OPTIONS_STOP_ON_SEARCH_ERROR(createSettingBoolean()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::isOptionsStopOnSearchError)
            .valueSetter(Settings::setOptionsStopOnSearchError)
            .defaultValue(false)),
    OPTION_RECURSIVE(createSettingBoolean()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::isOptionRecursive)
            .valueSetter(Settings::setOptionRecursive)
            .defaultValue(false)),
    PROCESS_EPISODE_SOURCE(createSettingEnum(SettingsProcessEpisodeSource.class)
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::getProcessEpisodeSource)
            .valueSetter(Settings::setProcessEpisodeSource)
            .defaultValue(SettingsProcessEpisodeSource.TVDB)),
    UPDATE_CHECK_PERIOD(createSettingEnum(UpdateCheckPeriod.class)
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::getUpdateCheckPeriod)
            .valueSetter(Settings::setUpdateCheckPeriod)
            .defaultValue(UpdateCheckPeriod.WEEKLY)),
    USE_NIGHTLY(createSettingEnum(UpdateType.class)
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::getUpdateType)
            .valueSetter(Settings::setUpdateType)
            .defaultValue(UpdateType.STABLE)),
    SUBTITLE_LANGUAGE(createSettingEnum(Language.class)
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::getSubtitleLanguage)
            .valueSetter(Settings::setSubtitleLanguage)
            .defaultValue(Language.DUTCH)),

    // SCREEN SETTINGS
    SCREEN_HIDE_EPISODE(createSettingBoolean()
            .rootElementFunction(sCtr -> sCtr.getSettings().getScreenSettings())
            .valueGetter(ScreenSettings::isHideEpisode)
            .valueSetter(ScreenSettings::setHideEpisode)
            .defaultValue(true)),
    SCREEN_HIDE_FILENAME(createSettingBoolean()
            .rootElementFunction(sCtr -> sCtr.getSettings().getScreenSettings())
            .valueGetter(ScreenSettings::isHideFilename)
            .valueSetter(ScreenSettings::setHideFilename)
            .defaultValue(false)),
    SCREEN_HIDE_SEASON(createSettingBoolean()
            .rootElementFunction(sCtr -> sCtr.getSettings().getScreenSettings())
            .valueGetter(ScreenSettings::isHideSeason)
            .valueSetter(ScreenSettings::setHideSeason)
            .defaultValue(true)),
    SCREEN_HIDE_TITLE(createSettingBoolean()
            .rootElementFunction(sCtr -> sCtr.getSettings().getScreenSettings())
            .valueGetter(ScreenSettings::isHideTitle)
            .valueSetter(ScreenSettings::setHideTitle)
            .defaultValue(true)),
    SCREEN_HIDE_TYPE(createSettingBoolean()
            .rootElementFunction(sCtr -> sCtr.getSettings().getScreenSettings())
            .valueGetter(ScreenSettings::isHideType)
            .valueSetter(ScreenSettings::setHideType)
            .defaultValue(true)),
    SCREEN_HIDE_W_I_P(createSettingBoolean()
            .rootElementFunction(sCtr -> sCtr.getSettings().getScreenSettings())
            .valueGetter(ScreenSettings::isHideWIP)
            .valueSetter(ScreenSettings::setHideWIP)
            .defaultValue(true)),

    // PROXY SETTINGS
    GENERAL_PROXY_ENABLED(createSettingBoolean()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::isGeneralProxyEnabled)
            .valueSetter(Settings::setGeneralProxyEnabled)
            .defaultValue(false)),
    GENERAL_PROXY_HOST(createSettingString()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::getGeneralProxyHost)
            .valueSetter(Settings::setGeneralProxyHost)
            .defaultValue("")),
    GENERAL_PROXY_PORT(createSettingInt()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::getGeneralProxyPort)
            .valueSetter(Settings::setGeneralProxyPort)
            .defaultValue(80)),

    // LIBRARY SERIE
    EPISODE_LIBRARY_BACKUP_SUBTITLE_PATH(createSettingPath()
            .rootElementFunction(sCtr -> sCtr.getSettings().getEpisodeLibrarySettings())
            .valueGetter(LibrarySettings::getLibraryBackupSubtitlePath)
            .valueSetter(LibrarySettings::setLibraryBackupSubtitlePath)
            .defaultValue(null)),
    EPISODE_LIBRARY_BACKUP_SUBTITLE(createSettingBoolean()
            .rootElementFunction(sCtr -> sCtr.getSettings().getEpisodeLibrarySettings())
            .valueGetter(LibrarySettings::isLibraryBackupSubtitle)
            .valueSetter(LibrarySettings::setLibraryBackupSubtitle)
            .defaultValue(false)),
    EPISODE_LIBRARY_BACKUP_USE_WEBSITE_FILE_NAME(createSettingBoolean()
            .rootElementFunction(sCtr -> sCtr.getSettings().getEpisodeLibrarySettings())
            .valueGetter(LibrarySettings::isLibraryBackupUseWebsiteFileName)
            .valueSetter(LibrarySettings::setLibraryBackupUseWebsiteFileName)
            .defaultValue(false)),
    EPISODE_LIBRARY_ACTION(createSettingEnum(LibraryActionType.class)
            .rootElementFunction(sCtr -> sCtr.getSettings().getEpisodeLibrarySettings())
            .valueGetter(LibrarySettings::getLibraryAction)
            .valueSetter(LibrarySettings::setLibraryAction)
            .defaultValue(LibraryActionType.NOTHING)),
    EPISODE_LIBRARY_USE_T_V_D_B_NAMING(createSettingBoolean()
            .rootElementFunction(sCtr -> sCtr.getSettings().getEpisodeLibrarySettings())
            .valueGetter(LibrarySettings::isLibraryUseTVDBNaming)
            .valueSetter(LibrarySettings::setLibraryUseTVDBNaming)
            .defaultValue(false)),
    EPISODE_LIBRARY_OTHER_FILE_ACTION(createSettingEnum(LibraryOtherFileActionType.class)
            .rootElementFunction(sCtr -> sCtr.getSettings().getEpisodeLibrarySettings())
            .valueGetter(LibrarySettings::getLibraryOtherFileAction)
            .valueSetter(LibrarySettings::setLibraryOtherFileAction)
            .defaultValue(LibraryOtherFileActionType.NOTHING)),
    EPISODE_LIBRARY_FOLDER(createSettingPath()
            .rootElementFunction(sCtr -> sCtr.getSettings().getEpisodeLibrarySettings())
            .valueGetter(LibrarySettings::getLibraryFolder)
            .valueSetter(LibrarySettings::setLibraryFolder)
            .defaultValue(null)),
    EPISODE_LIBRARY_FOLDER_STRUCTURE(createSettingString()
            .rootElementFunction(sCtr -> sCtr.getSettings().getEpisodeLibrarySettings())
            .valueGetter(LibrarySettings::getLibraryFolderStructure)
            .valueSetter(LibrarySettings::setLibraryFolderStructure)
            .defaultValue("")),
    EPISODE_LIBRARY_REMOVE_EMPTY_FOLDERS(createSettingBoolean()
            .rootElementFunction(sCtr -> sCtr.getSettings().getEpisodeLibrarySettings())
            .valueGetter(LibrarySettings::isLibraryRemoveEmptyFolders)
            .valueSetter(LibrarySettings::setLibraryRemoveEmptyFolders)
            .defaultValue(false)),
    EPISODE_LIBRARY_FILENAME_STRUCTURE(createSettingString()
            .rootElementFunction(sCtr -> sCtr.getSettings().getEpisodeLibrarySettings())
            .valueGetter(LibrarySettings::getLibraryFilenameStructure)
            .valueSetter(LibrarySettings::setLibraryFilenameStructure)
            .defaultValue("")),
    EPISODE_LIBRARY_REPLACE_SPACE(createSettingBoolean()
            .rootElementFunction(sCtr -> sCtr.getSettings().getEpisodeLibrarySettings())
            .valueGetter(LibrarySettings::isLibraryFilenameReplaceSpace)
            .valueSetter(LibrarySettings::setLibraryFilenameReplaceSpace)
            .defaultValue(false)),
    EPISODE_LIBRARY_REPLACING_SIGN(createSettingCharacter()
            .rootElementFunction(sCtr -> sCtr.getSettings().getEpisodeLibrarySettings())
            .valueGetter(LibrarySettings::getLibraryFilenameReplacingSpaceChar)
            .valueSetter(LibrarySettings::setLibraryFilenameReplacingSpaceChar)
            .defaultValue('_')),
    EPISODE_LIBRARY_FOLDER_REPLACE_SPACE(createSettingBoolean()
            .rootElementFunction(sCtr -> sCtr.getSettings().getEpisodeLibrarySettings())
            .valueGetter(LibrarySettings::isLibraryFolderReplaceSpace)
            .valueSetter(LibrarySettings::setLibraryFolderReplaceSpace)
            .defaultValue(false)),
    EPISODE_LIBRARY_FOLDER_REPLACING_SIGN(createSettingCharacter()
            .rootElementFunction(sCtr -> sCtr.getSettings().getEpisodeLibrarySettings())
            .valueGetter(LibrarySettings::getLibraryFolderReplacingSpaceChar)
            .valueSetter(LibrarySettings::setLibraryFolderReplacingSpaceChar)
            .defaultValue('_')),
    EPISODE_LIBRARY_INCLUDE_LANGUAGE_CODE(createSettingBoolean()
            .rootElementFunction(sCtr -> sCtr.getSettings().getEpisodeLibrarySettings())
            .valueGetter(LibrarySettings::isLibraryIncludeLanguageCode)
            .valueSetter(LibrarySettings::setLibraryIncludeLanguageCode)
            .defaultValue(false)),
    EPISODE_LIBRARY_LANG_CODE_MAPPING(createSettingMap()
            .toStringMapperKey(Language::name)
            .toObjectMapperKey(Language::valueOf)
            .toStringMapperValue(Function.identity())
            .toObjectMapperValue(Function.identity())
            .rootElementFunction(sCtr -> sCtr.getSettings().getEpisodeLibrarySettings())
            .mapGetter(LibrarySettings::getLangCodeMap)),

    // LIBRARY MOVIE
    MOVIE_LIBRARY_BACKUP_SUBTITLE_PATH(createSettingPath()
            .rootElementFunction(sCtr -> sCtr.getSettings().getMovieLibrarySettings())
            .valueGetter(LibrarySettings::getLibraryBackupSubtitlePath)
            .valueSetter(LibrarySettings::setLibraryBackupSubtitlePath)
            .defaultValue(null)),
    MOVIE_LIBRARY_BACKUP_SUBTITLE(createSettingBoolean()
            .rootElementFunction(sCtr -> sCtr.getSettings().getMovieLibrarySettings())
            .valueGetter(LibrarySettings::isLibraryBackupSubtitle)
            .valueSetter(LibrarySettings::setLibraryBackupSubtitle)
            .defaultValue(false)),
    MOVIE_LIBRARY_BACKUP_USE_WEBSITE_FILE_NAME(createSettingBoolean()
            .rootElementFunction(sCtr -> sCtr.getSettings().getMovieLibrarySettings())
            .valueGetter(LibrarySettings::isLibraryBackupUseWebsiteFileName)
            .valueSetter(LibrarySettings::setLibraryBackupUseWebsiteFileName)
            .defaultValue(false)),
    MOVIE_LIBRARY_ACTION(createSettingEnum(LibraryActionType.class)
            .rootElementFunction(sCtr -> sCtr.getSettings().getMovieLibrarySettings())
            .valueGetter(LibrarySettings::getLibraryAction)
            .valueSetter(LibrarySettings::setLibraryAction)
            .defaultValue(LibraryActionType.NOTHING)),
    MOVIE_LIBRARY_USE_T_V_D_B_NAMING(createSettingBoolean()
            .rootElementFunction(sCtr -> sCtr.getSettings().getMovieLibrarySettings())
            .valueGetter(LibrarySettings::isLibraryUseTVDBNaming)
            .valueSetter(LibrarySettings::setLibraryUseTVDBNaming)
            .defaultValue(false)),
    MOVIE_LIBRARY_OTHER_FILE_ACTION(createSettingEnum(LibraryOtherFileActionType.class)
            .rootElementFunction(sCtr -> sCtr.getSettings().getMovieLibrarySettings())
            .valueGetter(LibrarySettings::getLibraryOtherFileAction)
            .valueSetter(LibrarySettings::setLibraryOtherFileAction)
            .defaultValue(LibraryOtherFileActionType.NOTHING)),
    MOVIE_LIBRARY_FOLDER(createSettingPath()
            .rootElementFunction(sCtr -> sCtr.getSettings().getMovieLibrarySettings())
            .valueGetter(LibrarySettings::getLibraryFolder)
            .valueSetter(LibrarySettings::setLibraryFolder)
            .defaultValue(null)),
    MOVIE_LIBRARY_FOLDER_STRUCTURE(createSettingString()
            .rootElementFunction(sCtr -> sCtr.getSettings().getMovieLibrarySettings())
            .valueGetter(LibrarySettings::getLibraryFolderStructure)
            .valueSetter(LibrarySettings::setLibraryFolderStructure)
            .defaultValue("")),
    MOVIE_LIBRARY_REMOVE_EMPTY_FOLDERS(createSettingBoolean()
            .rootElementFunction(sCtr -> sCtr.getSettings().getMovieLibrarySettings())
            .valueGetter(LibrarySettings::isLibraryRemoveEmptyFolders)
            .valueSetter(LibrarySettings::setLibraryRemoveEmptyFolders)
            .defaultValue(false)),
    MOVIE_LIBRARY_FILENAME_STRUCTURE(createSettingString()
            .rootElementFunction(sCtr -> sCtr.getSettings().getMovieLibrarySettings())
            .valueGetter(LibrarySettings::getLibraryFilenameStructure)
            .valueSetter(LibrarySettings::setLibraryFilenameStructure)
            .defaultValue("")),
    MOVIE_LIBRARY_REPLACE_SPACE(createSettingBoolean()
            .rootElementFunction(sCtr -> sCtr.getSettings().getMovieLibrarySettings())
            .valueGetter(LibrarySettings::isLibraryFilenameReplaceSpace)
            .valueSetter(LibrarySettings::setLibraryFilenameReplaceSpace)
            .defaultValue(false)),
    MOVIE_LIBRARY_REPLACING_SIGN(createSettingCharacter()
            .rootElementFunction(sCtr -> sCtr.getSettings().getMovieLibrarySettings())
            .valueGetter(LibrarySettings::getLibraryFilenameReplacingSpaceChar)
            .valueSetter(LibrarySettings::setLibraryFilenameReplacingSpaceChar)
            .defaultValue('_')),
    MOVIE_LIBRARY_FOLDER_REPLACE_SPACE(createSettingBoolean()
            .rootElementFunction(sCtr -> sCtr.getSettings().getMovieLibrarySettings())
            .valueGetter(LibrarySettings::isLibraryFolderReplaceSpace)
            .valueSetter(LibrarySettings::setLibraryFolderReplaceSpace)
            .defaultValue(false)),
    MOVIE_LIBRARY_FOLDER_REPLACING_SIGN(createSettingCharacter()
            .rootElementFunction(sCtr -> sCtr.getSettings().getMovieLibrarySettings())
            .valueGetter(LibrarySettings::getLibraryFolderReplacingSpaceChar)
            .valueSetter(LibrarySettings::setLibraryFolderReplacingSpaceChar)
            .defaultValue('_')),
    MOVIE_LIBRARY_INCLUDE_LANGUAGE_CODE(createSettingBoolean()
            .rootElementFunction(sCtr -> sCtr.getSettings().getMovieLibrarySettings())
            .valueGetter(LibrarySettings::isLibraryIncludeLanguageCode)
            .valueSetter(LibrarySettings::setLibraryIncludeLanguageCode)
            .defaultValue(false)),
    MOVIE_LIBRARY_LANG_CODE_MAPPING(createSettingMap()
            .toStringMapperKey(Language::name)
            .toObjectMapperKey(Language::valueOf)
            .toStringMapperValue(Function.identity())
            .toObjectMapperValue(Function.identity())
            .rootElementFunction(sCtr -> sCtr.getSettings().getMovieLibrarySettings())
            .mapGetter(LibrarySettings::getLangCodeMap)),

    // SERIE SOURCE SETTINGS
    LOGIN_ADDIC7ED_ENABLED(createSettingBoolean()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::isLoginAddic7edEnabled)
            .valueSetter(Settings::setLoginAddic7edEnabled)
            .defaultValue(false)),
    LOGIN_ADDIC7ED_USERNAME(createSettingString()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::getLoginAddic7edUsername)
            .valueSetter(Settings::setLoginAddic7edUsername)
            .defaultValue("")),
    LOGIN_ADDIC7ED_PASSWORD(createSettingString()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::getLoginAddic7edPassword)
            .valueSetter(Settings::setLoginAddic7edPassword)
            .defaultValue("")),
    LOGIN_OPEN_SUBTITLES_ENABLED(createSettingBoolean()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::isLoginOpenSubtitlesEnabled)
            .valueSetter(Settings::setLoginOpenSubtitlesEnabled)
            .defaultValue(false)),
    LOGIN_OPEN_SUBTITLES_USERNAME(createSettingString()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::getLoginOpenSubtitlesUsername)
            .valueSetter(Settings::setLoginOpenSubtitlesUsername)
            .defaultValue("")),
    LOGIN_OPEN_SUBTITLES_PASSWORD(createSettingString()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::getLoginOpenSubtitlesPassword)
            .valueSetter(Settings::setLoginOpenSubtitlesPassword)
            .defaultValue("")),
    SERIE_SOURCE_ADDIC7ED(createSettingBoolean()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::isSerieSourceAddic7ed)
            .valueSetter(Settings::setSerieSourceAddic7ed)
            .defaultValue(true)),
    SERIE_SOURCE_ADDIC7ED_PROXY(createSettingBoolean()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::isSerieSourceAddic7edProxy)
            .valueSetter(Settings::setSerieSourceAddic7edProxy)
            .defaultValue(true)),
    SERIE_SOURCE_LOCAL(createSettingBoolean()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::isSerieSourceLocal)
            .valueSetter(Settings::setSerieSourceLocal)
            .defaultValue(false)),
    SERIE_SOURCE_OPENSUBTITLES(createSettingBoolean()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::isSerieSourceOpensubtitles)
            .valueSetter(Settings::setSerieSourceOpensubtitles)
            .defaultValue(true)),
    SERIE_SOURCE_PODNAPISI(createSettingBoolean()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::isSerieSourcePodnapisi)
            .valueSetter(Settings::setSerieSourcePodnapisi)
            .defaultValue(true)),
    SERIE_SOURCE_TV_SUBTITLES(createSettingBoolean()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::isSerieSourceTvSubtitles)
            .valueSetter(Settings::setSerieSourceTvSubtitles)
            .defaultValue(true)),
    SERIE_SOURCE_SUBSCENE(createSettingBoolean()
            .rootElementFunction(SettingsControl::getSettings)
            .valueGetter(Settings::isSerieSourceSubscene)
            .valueSetter(Settings::setSerieSourceSubscene)
            .defaultValue(true));

    private final BiConsumer<SettingsControl, Preferences> storeValueFunction;
    private final BiConsumer<SettingsControl, Preferences> loadValueFunction;

    SettingValue(SettingBuildIntf settingBuild) {
        SettingIntf setting = settingBuild.build(getKey());
        this.storeValueFunction = setting.getStoreValueFunction();
        this.loadValueFunction = setting.getLoadValueFunction();
    }

    public String getKey() {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
    }

    public void store(SettingsControl settingsControl, Preferences preferences) {
        storeValueFunction.accept(settingsControl, preferences);
    }

    public void load(SettingsControl settingsControl, Preferences preferences) {
        loadValueFunction.accept(settingsControl, preferences);
    }

    public static void loadAll(SettingsControl settingsControl, Preferences preferences) {
        Arrays.stream(SettingValue.values()).forEach(sv -> sv.load(settingsControl, preferences));
    }

    @RequiredArgsConstructor
    @Setter
    @Accessors(chain = true)
    @Getter
    private static class Setting<T, R> implements SettingIntf {
        private final String key;
        private final Function<SettingsControl, R> rootElementFunction;
        private T defaultValue;
        private BiConsumer<SettingsControl, Preferences> storeValueFunction;
        private BiConsumer<SettingsControl, Preferences> loadValueFunction;
    }

    private interface SettingIntf {
        BiConsumer<SettingsControl, Preferences> getStoreValueFunction();

        BiConsumer<SettingsControl, Preferences> getLoadValueFunction();
    }

    private static SettingTypedRootElementFunctionIntf<String> createSettingString() {
        return createSetting(String.class)
                .toStringMapper(Function.identity())
                .toObjectMapper(Function.identity());
    }

    private static SettingTypedRootElementFunctionIntf<Character> createSettingCharacter() {
        return createSetting(Character.class)
                .toStringMapper(String::valueOf)
                .toObjectMapper(s -> s.charAt(0));
    }

    private static SettingTypedRootElementFunctionIntf<Integer> createSettingInt() {
        return createSetting(Integer.class)
                .preferencesSetter(Preferences::putInt)
                .preferencesGetter(Preferences::getInt);
    }

    private static SettingTypedRootElementFunctionIntf<Boolean> createSettingBoolean() {
        return createSetting(Boolean.class)
                .preferencesSetter(Preferences::putBoolean)
                .preferencesGetter(Preferences::getBoolean);
    }

    private static SettingTypedRootElementFunctionIntf<Path> createSettingPath() {
        return createSetting(Path.class)
                .toStringMapper(FileUtils::toAbsolutePathAsString)
                .toObjectMapper(Path::of);
    }

    private static <T extends Enum<T>> SettingTypedRootElementFunctionIntf<T> createSettingEnum(Class<T> type) {
        return createSetting(type)
                .toStringMapper(Enum::name)
                .toObjectMapper(s -> Enum.valueOf(type, s));
    }

    private static <T> SettingTypedToStringMapperIntf<T> createSetting(Class<T> type) {
        return new SettingTyped<>(type);
    }

    private interface SettingTypedToStringMapperIntf<T> {
        SettingTypedToObjectMapperIntf<T> toStringMapper(Function<T, String> toStringMapper);

        SettingTypedPreferenceGetterIntf<T> preferencesSetter(TriConsumer<Preferences, String, T> preferencesSetter);
    }

    private interface SettingTypedToObjectMapperIntf<T> {
        SettingTypedRootElementFunctionIntf<T> toObjectMapper(Function<String, T> toObjectMapper);
    }

    private interface SettingTypedPreferenceGetterIntf<T> {
        SettingTypedRootElementFunctionIntf<T> preferencesGetter(TriFunction<Preferences, String, T, T> preferencesGetter);
    }

    private interface SettingTypedRootElementFunctionIntf<T> {
        <R> SettingTypedValueGetterIntf<T, R> rootElementFunction(Function<SettingsControl, R> rootElementFunction);
    }

    private interface SettingTypedValueGetterIntf<T, R> {
        SettingTypedValueSetterIntf<T, R> valueGetter(Function<R, T> valueGetter);

        SettingBuildIntf collectionGetter(Function<R, Collection<T>> valueGetter);

    }

    private interface SettingTypedValueSetterIntf<T, R> {
        SettingTypedDefaultValueIntf<T, R> valueSetter(BiConsumer<R, T> valueSetter);
    }

    private interface SettingTypedDefaultValueIntf<T, R> {
        SettingBuildIntf defaultValue(T defaultValue);
    }

    private interface SettingBuildIntf {
        SettingIntf build(String key);
    }

    private enum SettingType {
        SINGLE_VALUE, COLLECTION;
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    private static class SettingTyped<T, R> extends SettingCommon<T, R, SettingTyped<T, R>>
            implements
            SettingTypedToStringMapperIntf<T>,
            SettingTypedToObjectMapperIntf<T>,
            SettingTypedPreferenceGetterIntf<T>,
            SettingTypedRootElementFunctionIntf<T>,
            SettingTypedValueGetterIntf<T, R>,
            SettingTypedValueSetterIntf<T, R>,
            SettingTypedDefaultValueIntf<T, R>,
            SettingBuildIntf {

        private SettingType settingType = SettingType.SINGLE_VALUE;

        // single value
        private Function<R, T> valueGetter;
        private BiConsumer<R, T> valueSetter;

        // collection
        Consumer<R> listCleaner;
        BiConsumer<R, T> valueAdder;
        BiConsumer<R, Consumer<T>> valueConsumer;

        private Function<T, String> toStringMapper;
        private Function<String, T> toObjectMapper;

        private TriConsumer<Preferences, String, T> preferencesSetter;
        private TriFunction<Preferences, String, T, T> preferencesGetter;

        SettingTyped(Class<T> type) {
            //
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R2> SettingTyped<T, R2> rootElementFunction(Function<SettingsControl, R2> rootElementFunction) {
            super.setRootElementFunction(rootElementFunction);
            return (SettingTyped<T, R2>) this;
        }

        @Override
        public SettingTyped<T, R> defaultValue(T defaultValue) {
            super.defaultValue(defaultValue);
            return this;
        }

        @Override
        public SettingTyped<T, R> toStringMapper(Function<T, String> toStringMapper) {
            this.toStringMapper = toStringMapper;
            this.preferencesSetter = (preferences, key, value) -> preferences.put(key, toStringMapper.apply(value));
            return this;
        }

        @Override
        public SettingTyped<T, R> toObjectMapper(Function<String, T> toObjectMapper) {
            this.toObjectMapper = toObjectMapper;
            this.preferencesGetter =
                    (preferences, key, defaultValue) -> {
                        String value = preferences.get(key, null);
                        return value != null ? toObjectMapper.apply(value) : getDefaultValue();
                    };
            return this;
        }

        @Override
        public SettingTyped<T, R> collectionGetter(Function<R, Collection<T>> collectionGetter) {
            settingType = SettingType.COLLECTION;
            listCleaner = object -> collectionGetter.apply(object).clear();
            valueAdder = (object, v) -> collectionGetter.apply(object).add(v);
            valueConsumer = (object, consumer) -> collectionGetter.apply(object).forEach(consumer);
            return this;
        }

        @Override
        public SettingIntf build(String key) {
            switch (settingType) {
                case SINGLE_VALUE -> {
                    super.storeValueFunction((settingsControl, preferences) -> {
                        T value = valueGetter.apply(getRootElement(settingsControl));
                        if (!Objects.equal(value, getDefaultValue()) && !(value instanceof String text && "".equals(text))) {
                            preferencesSetter.accept(preferences, key, value);
                        }
                    });

                    super.loadValueFunction((settingsControl, preferences) -> valueSetter.accept(getRootElement(settingsControl),
                            preferencesGetter.apply(preferences, key, getDefaultValue())));
                }
                case COLLECTION -> {
                    super.storeValueFunction((settingsControl, preferences) -> {
                        AtomicInteger i = new AtomicInteger(-1);
                        valueConsumer.accept(getRootElement(settingsControl),
                                value -> preferences.put(key + i.incrementAndGet(), toStringMapper.apply(value)));
                        if (i.get() > -1) {
                            preferences.putInt(key + "Size", i.get() + 1);
                        }
                    });
                    super.loadValueFunction((settingsControl, preferences) -> {
                        int numberOfItems = preferences.getInt(key + "Size", 0);
                        R rootElement = getRootElement(settingsControl);
                        listCleaner.accept(rootElement);
                        IntStream.range(0, numberOfItems).forEach(i -> {
                            valueAdder.accept(rootElement, toObjectMapper.apply(preferences.get(key + i, "")));
                        });

                    });
                }
                default -> throw new IllegalArgumentException("Unexpected value: " + settingType);
            }
            return this;
        }
    }

    private static SettingMapTypedToStringMapperKeyIntf createSettingMap() {
        return new SettingMapTyped<>();
    }

    private interface SettingMapTypedToStringMapperKeyIntf {
        <K> SettingMapTypedToObjectMapperKeyIntf<K> toStringMapperKey(Function<K, String> toStringMapperKey);

    }

    private interface SettingMapTypedToObjectMapperKeyIntf<K> {
        SettingMapTypedToStringMapperValueIntf<K> toObjectMapperKey(Function<String, K> toObjectMapperKey);
    }

    private interface SettingMapTypedToStringMapperValueIntf<K> {
        <V> SettingMapTypedToObjectMapperValueIntf<K, V> toStringMapperValue(Function<V, String> toStringMapperValue);

    }

    private interface SettingMapTypedToObjectMapperValueIntf<K, V> {
        SettingMapTypedRootElementFunctionIntf<K, V> toObjectMapperValue(Function<String, V> toObjectMapperValue);
    }

    private interface SettingMapTypedRootElementFunctionIntf<K, V> {
        <R> SettingMapTypedMapGetterIntf<K, V, R> rootElementFunction(Function<SettingsControl, R> rootElementFunction);
    }

    private interface SettingMapTypedMapGetterIntf<K, V, R> {
        SettingMapTyped<K, V, R> mapGetter(Function<R, Map<K, V>> valueGetter);
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    private static class SettingMapTyped<K, V, R>
            implements SettingIntf,
            SettingMapTypedToStringMapperKeyIntf,
            SettingMapTypedToObjectMapperKeyIntf<K>,
            SettingMapTypedToStringMapperValueIntf<K>,
            SettingMapTypedToObjectMapperValueIntf<K, V>,
            SettingMapTypedRootElementFunctionIntf<K, V>,
            SettingMapTypedMapGetterIntf<K, V, R>,
            SettingBuildIntf {

        private Function<K, String> toStringMapperKey;
        private Function<String, K> toObjectMapperKey;
        private Function<V, String> toStringMapperValue;
        private Function<String, V> toObjectMapperValue;
        private BiConsumer<SettingsControl, Preferences> storeValueFunction;
        private BiConsumer<SettingsControl, Preferences> loadValueFunction;
        private Function<SettingsControl, R> rootElementFunction;
        private TriConsumer<R, K, V> valueAdder;
        private BiConsumer<R, BiConsumer<K, V>> valueConsumer;

        @SuppressWarnings("unchecked")
        @Override
        public <K2> SettingMapTyped<K2, V, R> toStringMapperKey(Function<K2, String> toStringMapperKey) {
            this.toStringMapperKey = (Function<K, String>) toStringMapperKey;
            return (SettingMapTyped<K2, V, R>) this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <V2> SettingMapTyped<K, V2, R> toStringMapperValue(Function<V2, String> toStringMapperValue) {
            this.toStringMapperValue = (Function<V, String>) toStringMapperValue;
            return (SettingMapTyped<K, V2, R>) this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R2> SettingMapTyped<K, V, R2> rootElementFunction(Function<SettingsControl, R2> rootElementFunction) {
            this.rootElementFunction = (Function<SettingsControl, R>) rootElementFunction;
            return (SettingMapTyped<K, V, R2>) this;
        }

        @Override
        public SettingMapTyped<K, V, R> mapGetter(Function<R, Map<K, V>> mapGetter) {
            valueAdder = (object, k, v) -> mapGetter.apply(object).put(k, v);
            valueConsumer = (object, consumer) -> mapGetter.apply(object).forEach(consumer);
            return this;
        }

        @Override
        public BiConsumer<SettingsControl, Preferences> getStoreValueFunction() {
            return storeValueFunction;
        }

        @Override
        public BiConsumer<SettingsControl, Preferences> getLoadValueFunction() {
            return loadValueFunction;
        }

        @Override
        public SettingIntf build(String key) {
            this.storeValueFunction = (settingsControl, preferences) -> {
                AtomicInteger i = new AtomicInteger(-1);
                valueConsumer.accept(getRootElement(settingsControl),
                        (k, v) -> {
                            int idx = i.incrementAndGet();
                            preferences.put(getKeyString(key, idx), toStringMapperKey.apply(k));
                            preferences.put(getValueString(key, idx), toStringMapperValue.apply(v));
                        });
                if (i.get() > -1) {
                    preferences.putInt(key + "Size", i.get() + 1);
                }
            };
            this.loadValueFunction = (settingsControl, preferences) -> {
                int numberOfItems = preferences.getInt(key + "Size", 0);
                IntStream.range(0, numberOfItems).forEach(idx -> {
                    valueAdder.accept(getRootElement(settingsControl),
                            toObjectMapperKey.apply(preferences.get(getKeyString(key, idx), "")),
                            toObjectMapperValue.apply(preferences.get(getValueString(key, idx), "")));
                });

            };
            return this;
        }

        private String getKeyString(String key, int idx) {
            return key + "-key" + idx;
        }

        private String getValueString(String key, int idx) {
            return key + "-value" + idx;
        }

        private R getRootElement(SettingsControl settingsControl) {
            return rootElementFunction.apply(settingsControl);
        }

    }

    @Getter
    private abstract static class SettingCommon<T, ROOT, TYPE> implements SettingIntf {
        private Function<SettingsControl, ROOT> rootElementFunction;
        private T defaultValue;
        private BiConsumer<SettingsControl, Preferences> storeValueFunction;
        private BiConsumer<SettingsControl, Preferences> loadValueFunction;

        TYPE defaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return getThis();
        }

        @SuppressWarnings("unchecked")
        public <R> void setRootElementFunction(Function<SettingsControl, R> rootElementFunction) {
            this.rootElementFunction = (Function<SettingsControl, ROOT>) rootElementFunction;
        }

        TYPE storeValueFunction(BiConsumer<SettingsControl, Preferences> storeValueFunction) {
            this.storeValueFunction = storeValueFunction;
            return getThis();
        }

        TYPE loadValueFunction(BiConsumer<SettingsControl, Preferences> loadValueFunction) {
            this.loadValueFunction = loadValueFunction;
            return getThis();
        }

        ROOT getRootElement(SettingsControl settingsControl) {
            return rootElementFunction.apply(settingsControl);
        }

        @SuppressWarnings("unchecked")
        private TYPE getThis() {
            return (TYPE) this;
        }
    }
}
