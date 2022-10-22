package org.lodder.subtools.multisubdownloader.settings;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.prefs.Preferences;
import java.util.stream.IntStream;

import org.apache.commons.text.CaseUtils;
import org.lodder.subtools.multisubdownloader.gui.extra.MemoryFolderChooser;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.multisubdownloader.settings.model.ScreenSettings;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsExcludeItem;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsExcludeType;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsProcessEpisodeSource;
import org.lodder.subtools.multisubdownloader.settings.model.State;
import org.lodder.subtools.multisubdownloader.settings.model.UpdateCheckPeriod;
import org.lodder.subtools.sublibrary.Language;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

public enum SettingValue {

    // SETTINGS
    SETTINGS_VERSION(0, SettingsControl::getSettings, Settings::getSettingsVersion, Settings::setSettingsVersion),
    LAST_OUTPUT_DIR(new File(""), File::getAbsolutePath, File::new, SettingsControl::getSettings, settings -> MemoryFolderChooser.getInstance().getMemory(), Settings::setLastOutputDir),

    GENERAL_DEFAULT_INCOMING_FOLDER(File::getAbsolutePath, File::new, SettingsControl::getSettings, Settings::getDefaultIncomingFolders),
    LOCAL_SUBTITLES_SOURCES_FOLDERS(File::getAbsolutePath, File::new, SettingsControl::getSettings, Settings::getLocalSourcesFolders),
    EXCLUDE_ITEM(v -> v.getType().toString() + "//" + v.getDescription(),
            v -> {
                String[] split = v.split("//", 2);
                String description = split[1];
                SettingsExcludeType type;
                try {
                    type = SettingsExcludeType.valueOf(split[0]);
                } catch (IllegalArgumentException e) {
                    type = SettingsExcludeType.FOLDER;
                }
                return new SettingsExcludeItem(description, type);
            },
            SettingsControl::getSettings, Settings::getExcludeList),
    DEFAULT_SELECTION_QUALITY(SettingsControl::getSettings, Settings::getOptionsDefaultSelectionQualityList),
    DEFAULT_SELECTION_QUALITY_ENABLED(false, SettingsControl::getSettings, Settings::isOptionsDefaultSelection, Settings::setOptionsDefaultSelection),

    OPTIONS_ALWAYS_CONFIRM(true, SettingsControl::getSettings, Settings::isOptionsAlwaysConfirm, Settings::setOptionsAlwaysConfirm),
    OPTIONS_CONFIRM_MAPPING(false, SettingsControl::getSettings, Settings::isOptionsConfirmProviderMapping, Settings::setOptionsConfirmProviderMapping),
    OPTIONS_MIN_AUTOMATIC_SELECTION(false, SettingsControl::getSettings, Settings::isOptionsMinAutomaticSelection, Settings::setOptionsMinAutomaticSelection),
    OPTIONS_MIN_AUTOMATIC_SELECTION_VALUE(0, SettingsControl::getSettings, Settings::getOptionsMinAutomaticSelectionValue, Settings::setOptionsMinAutomaticSelectionValue),
    OPTION_SUBTITLE_EXACT_MATCH(true, SettingsControl::getSettings, Settings::isOptionSubtitleExactMatch, Settings::setOptionSubtitleExactMatch),
    OPTION_SUBTITLE_KEYWORD_MATCH(true, SettingsControl::getSettings, Settings::isOptionSubtitleKeywordMatch, Settings::setOptionSubtitleKeywordMatch),
    OPTION_SUBTITLE_EXCLUDE_HEARING_IMPAIRED(true, SettingsControl::getSettings, Settings::isOptionSubtitleExcludeHearingImpaired, Settings::setOptionSubtitleExcludeHearingImpaired),
    OPTIONS_SHOW_ONLY_FOUND(true, SettingsControl::getSettings, Settings::isOptionsShowOnlyFound, Settings::setOptionsShowOnlyFound),
    OPTIONS_STOP_ON_SEARCH_ERROR(false, SettingsControl::getSettings, Settings::isOptionsStopOnSearchError, Settings::setOptionsStopOnSearchError),
    OPTION_RECURSIVE(false, SettingsControl::getSettings, Settings::isOptionRecursive, Settings::setOptionRecursive),
    PROCESS_EPISODE_SOURCE(SettingsProcessEpisodeSource.TVDB, SettingsProcessEpisodeSource::toString, SettingsProcessEpisodeSource::valueOf, SettingsControl::getSettings, Settings::getProcessEpisodeSource, Settings::setProcessEpisodeSource),
    UPDATE_CHECK_PERIOD(UpdateCheckPeriod.WEEKLY, UpdateCheckPeriod::toString, UpdateCheckPeriod::valueOf, SettingsControl::getSettings, Settings::getUpdateCheckPeriod, Settings::setUpdateCheckPeriod),
    SUBTITLE_LANGUAGE(Language.DUTCH, Language::name, Language::fromValue, SettingsControl::getSettings, Settings::getSubtitleLanguage, Settings::setSubtitleLanguage),

    // SCREEN SETTINGS
    SCREEN_HIDE_EPISODE(true, sCtr -> sCtr.getSettings().getScreenSettings(), ScreenSettings::isHideEpisode, ScreenSettings::setHideEpisode),
    SCREEN_HIDE_FILENAME(false, sCtr -> sCtr.getSettings().getScreenSettings(), ScreenSettings::isHideFilename, ScreenSettings::setHideFilename),
    SCREEN_HIDE_SEASON(true, sCtr -> sCtr.getSettings().getScreenSettings(), ScreenSettings::isHideSeason, ScreenSettings::setHideSeason),
    SCREEN_HIDE_TITLE(true, sCtr -> sCtr.getSettings().getScreenSettings(), ScreenSettings::isHideTitle, ScreenSettings::setHideTitle),
    SCREEN_HIDE_TYPE(true, sCtr -> sCtr.getSettings().getScreenSettings(), ScreenSettings::isHideType, ScreenSettings::setHideType),
    SCREEN_HIDE_W_I_P(true, sCtr -> sCtr.getSettings().getScreenSettings(), ScreenSettings::isHideWIP, ScreenSettings::setHideWIP),

    // PROXY SETTINGS
    GENERAL_PROXY_ENABLED(false, SettingsControl::getSettings, Settings::isGeneralProxyEnabled, Settings::setGeneralProxyEnabled),
    GENERAL_PROXY_HOST("", SettingsControl::getSettings, Settings::getGeneralProxyHost, Settings::setGeneralProxyHost),
    GENERAL_PROXY_PORT(80, SettingsControl::getSettings, Settings::getGeneralProxyPort, Settings::setGeneralProxyPort),

    // LIBRARY SERIE
    EPISODE_LIBRARY_BACKUP_SUBTITLE_PATH(new File(""), File::getAbsolutePath, File::new, sCtr -> sCtr.getSettings().getEpisodeLibrarySettings(), LibrarySettings::getLibraryBackupSubtitlePath, LibrarySettings::setLibraryBackupSubtitlePath),
    EPISODE_LIBRARY_BACKUP_SUBTITLE(false, sCtr -> sCtr.getSettings().getEpisodeLibrarySettings(), LibrarySettings::isLibraryBackupSubtitle, LibrarySettings::setLibraryBackupSubtitle),
    EPISODE_LIBRARY_BACKUP_USE_WEBSITE_FILE_NAME(false, sCtr -> sCtr.getSettings().getEpisodeLibrarySettings(), LibrarySettings::isLibraryBackupUseWebsiteFileName, LibrarySettings::setLibraryBackupUseWebsiteFileName),
    EPISODE_LIBRARY_ACTION(LibraryActionType.NOTHING, LibraryActionType::name, LibraryActionType::valueOf, sCtr -> sCtr.getSettings().getEpisodeLibrarySettings(), LibrarySettings::getLibraryAction, LibrarySettings::setLibraryAction),
    EPISODE_LIBRARY_USE_T_V_D_B_NAMING(false, sCtr -> sCtr.getSettings().getEpisodeLibrarySettings(), LibrarySettings::isLibraryUseTVDBNaming, LibrarySettings::setLibraryUseTVDBNaming),
    EPISODE_LIBRARY_REPLACE_CHARS(false, sCtr -> sCtr.getSettings().getEpisodeLibrarySettings(), LibrarySettings::isLibraryReplaceChars, LibrarySettings::setLibraryReplaceChars),
    EPISODE_LIBRARY_OTHER_FILE_ACTION(LibraryOtherFileActionType.NOTHING, LibraryOtherFileActionType::name, LibraryOtherFileActionType::valueOf, sCtr -> sCtr.getSettings().getEpisodeLibrarySettings(), LibrarySettings::getLibraryOtherFileAction, LibrarySettings::setLibraryOtherFileAction),
    EPISODE_LIBRARY_FOLDER(new File(""), File::getAbsolutePath, File::new, sCtr -> sCtr.getSettings().getEpisodeLibrarySettings(), LibrarySettings::getLibraryFolder, LibrarySettings::setLibraryFolder),
    EPISODE_LIBRARY_STRUCTURE("", sCtr -> sCtr.getSettings().getEpisodeLibrarySettings(), LibrarySettings::getLibraryFolderStructure, LibrarySettings::setLibraryFolderStructure),
    EPISODE_LIBRARY_REMOVE_EMPTY_FOLDERS(false, sCtr -> sCtr.getSettings().getEpisodeLibrarySettings(), LibrarySettings::isLibraryRemoveEmptyFolders, LibrarySettings::setLibraryRemoveEmptyFolders),
    EPISODE_LIBRARY_FILENAME("", sCtr -> sCtr.getSettings().getEpisodeLibrarySettings(), LibrarySettings::getLibraryFilenameStructure, LibrarySettings::setLibraryFilenameStructure),
    EPISODE_LIBRARY_REPLACE_SPACE(false, sCtr -> sCtr.getSettings().getEpisodeLibrarySettings(), LibrarySettings::isLibraryFilenameReplaceSpace, LibrarySettings::setLibraryFilenameReplaceSpace),
    EPISODE_LIBRARY_REPLACING_SIGN("", sCtr -> sCtr.getSettings().getEpisodeLibrarySettings(), LibrarySettings::getLibraryFilenameReplacingSpaceSign, LibrarySettings::setLibraryFilenameReplacingSpaceSign),
    EPISODE_LIBRARY_FOLDER_REPLACE_SPACE(false, sCtr -> sCtr.getSettings().getEpisodeLibrarySettings(), LibrarySettings::isLibraryFolderReplaceSpace, LibrarySettings::setLibraryFolderReplaceSpace),
    EPISODE_LIBRARY_FOLDER_REPLACING_SIGN("", sCtr -> sCtr.getSettings().getEpisodeLibrarySettings(), LibrarySettings::getLibraryFolderReplacingSpaceSign, LibrarySettings::setLibraryFolderReplacingSpaceSign),
    EPISODE_LIBRARY_INCLUDE_LANGUAGE_CODE(false, sCtr -> sCtr.getSettings().getEpisodeLibrarySettings(), LibrarySettings::isLibraryIncludeLanguageCode, LibrarySettings::setLibraryIncludeLanguageCode),
    EPISODE_LIBRARY_DEFAULT_NL_TEXT("", sCtr -> sCtr.getSettings().getEpisodeLibrarySettings(), LibrarySettings::getDefaultNlText, LibrarySettings::setDefaultNlText),
    EPISODE_LIBRARY_DEFAULT_EN_TEXT("", sCtr -> sCtr.getSettings().getEpisodeLibrarySettings(), LibrarySettings::getDefaultEnText, LibrarySettings::setDefaultEnText),

    // LIBRARY MOVIE
    MOVIE_LIBRARY_BACKUP_SUBTITLE_PATH(new File(""), File::getAbsolutePath, File::new, sCtr -> sCtr.getSettings().getMovieLibrarySettings(), LibrarySettings::getLibraryBackupSubtitlePath, LibrarySettings::setLibraryBackupSubtitlePath),
    MOVIE_LIBRARY_BACKUP_SUBTITLE(false, sCtr -> sCtr.getSettings().getMovieLibrarySettings(), LibrarySettings::isLibraryBackupSubtitle, LibrarySettings::setLibraryBackupSubtitle),
    MOVIE_LIBRARY_BACKUP_USE_WEBSITE_FILE_NAME(false, sCtr -> sCtr.getSettings().getMovieLibrarySettings(), LibrarySettings::isLibraryBackupUseWebsiteFileName, LibrarySettings::setLibraryBackupUseWebsiteFileName),
    MOVIE_LIBRARY_ACTION(LibraryActionType.NOTHING, LibraryActionType::name, LibraryActionType::valueOf, sCtr -> sCtr.getSettings().getMovieLibrarySettings(), LibrarySettings::getLibraryAction, LibrarySettings::setLibraryAction),
    MOVIE_LIBRARY_USE_T_V_D_B_NAMING(false, sCtr -> sCtr.getSettings().getMovieLibrarySettings(), LibrarySettings::isLibraryUseTVDBNaming, LibrarySettings::setLibraryUseTVDBNaming),
    MOVIE_LIBRARY_REPLACE_CHARS(false, sCtr -> sCtr.getSettings().getMovieLibrarySettings(), LibrarySettings::isLibraryReplaceChars, LibrarySettings::setLibraryReplaceChars),
    MOVIE_LIBRARY_OTHER_FILE_ACTION(LibraryOtherFileActionType.NOTHING, LibraryOtherFileActionType::name, LibraryOtherFileActionType::valueOf, sCtr -> sCtr.getSettings().getMovieLibrarySettings(), LibrarySettings::getLibraryOtherFileAction, LibrarySettings::setLibraryOtherFileAction),
    MOVIE_LIBRARY_FOLDER(new File(""), File::getAbsolutePath, File::new, sCtr -> sCtr.getSettings().getMovieLibrarySettings(), LibrarySettings::getLibraryFolder, LibrarySettings::setLibraryFolder),
    MOVIE_LIBRARY_STRUCTURE("", sCtr -> sCtr.getSettings().getMovieLibrarySettings(), LibrarySettings::getLibraryFolderStructure, LibrarySettings::setLibraryFolderStructure),
    MOVIE_LIBRARY_REMOVE_EMPTY_FOLDERS(false, sCtr -> sCtr.getSettings().getMovieLibrarySettings(), LibrarySettings::isLibraryRemoveEmptyFolders, LibrarySettings::setLibraryRemoveEmptyFolders),
    MOVIE_LIBRARY_FILENAME("", sCtr -> sCtr.getSettings().getMovieLibrarySettings(), LibrarySettings::getLibraryFilenameStructure, LibrarySettings::setLibraryFilenameStructure),
    MOVIE_LIBRARY_REPLACE_SPACE(false, sCtr -> sCtr.getSettings().getMovieLibrarySettings(), LibrarySettings::isLibraryFilenameReplaceSpace, LibrarySettings::setLibraryFilenameReplaceSpace),
    MOVIE_LIBRARY_REPLACING_SIGN("", sCtr -> sCtr.getSettings().getMovieLibrarySettings(), LibrarySettings::getLibraryFilenameReplacingSpaceSign, LibrarySettings::setLibraryFilenameReplacingSpaceSign),
    MOVIE_LIBRARY_FOLDER_REPLACE_SPACE(false, sCtr -> sCtr.getSettings().getMovieLibrarySettings(), LibrarySettings::isLibraryFolderReplaceSpace, LibrarySettings::setLibraryFolderReplaceSpace),
    MOVIE_LIBRARY_FOLDER_REPLACING_SIGN("", sCtr -> sCtr.getSettings().getMovieLibrarySettings(), LibrarySettings::getLibraryFolderReplacingSpaceSign, LibrarySettings::setLibraryFolderReplacingSpaceSign),
    MOVIE_LIBRARY_INCLUDE_LANGUAGE_CODE(false, sCtr -> sCtr.getSettings().getMovieLibrarySettings(), LibrarySettings::isLibraryIncludeLanguageCode, LibrarySettings::setLibraryIncludeLanguageCode),
    MOVIE_LIBRARY_DEFAULT_NL_TEXT("", sCtr -> sCtr.getSettings().getMovieLibrarySettings(), LibrarySettings::getDefaultNlText, LibrarySettings::setDefaultNlText),
    MOVIE_LIBRARY_DEFAULT_EN_TEXT("", sCtr -> sCtr.getSettings().getMovieLibrarySettings(), LibrarySettings::getDefaultEnText, LibrarySettings::setDefaultEnText),

    // SERIE SOURCE SETTINGS
    LOGIN_ADDIC7ED_ENABLED(false, SettingsControl::getSettings, Settings::isLoginAddic7edEnabled, Settings::setLoginAddic7edEnabled),
    LOGIN_ADDIC7ED_USERNAME("", SettingsControl::getSettings, Settings::getLoginAddic7edUsername, Settings::setLoginAddic7edUsername),
    LOGIN_ADDIC7ED_PASSWORD("", SettingsControl::getSettings, Settings::getLoginAddic7edPassword, Settings::setLoginAddic7edPassword),
    LOGIN_OPEN_SUBTITLES_ENABLED(false, SettingsControl::getSettings, Settings::isLoginOpenSubtitlesEnabled, Settings::setLoginOpenSubtitlesEnabled),
    LOGIN_OPEN_SUBTITLES_USERNAME("", SettingsControl::getSettings, Settings::getLoginOpenSubtitlesUsername, Settings::setLoginOpenSubtitlesUsername),
    LOGIN_OPEN_SUBTITLES_PASSWORD("", SettingsControl::getSettings, Settings::getLoginOpenSubtitlesPassword, Settings::setLoginOpenSubtitlesPassword),
    SERIE_SOURCE_ADDIC7ED(true, SettingsControl::getSettings, Settings::isSerieSourceAddic7ed, Settings::setSerieSourceAddic7ed),
    SERIE_SOURCE_ADDIC7ED_PROXY(true, SettingsControl::getSettings, Settings::isSerieSourceAddic7edProxy, Settings::setSerieSourceAddic7edProxy),
    SERIE_SOURCE_LOCAL(false, SettingsControl::getSettings, Settings::isSerieSourceLocal, Settings::setSerieSourceLocal),
    SERIE_SOURCE_OPENSUBTITLES(true, SettingsControl::getSettings, Settings::isSerieSourceOpensubtitles, Settings::setSerieSourceOpensubtitles),
    SERIE_SOURCE_PODNAPISI(true, SettingsControl::getSettings, Settings::isSerieSourcePodnapisi, Settings::setSerieSourcePodnapisi),
    SERIE_SOURCE_TV_SUBTITLES(true, SettingsControl::getSettings, Settings::isSerieSourceTvSubtitles, Settings::setSerieSourceTvSubtitles),
    SERIE_SOURCE_SUBSCENE(true, SettingsControl::getSettings, Settings::isSerieSourceSubscene, Settings::setSerieSourceSubscene),

    // STATE
    LATEST_UPDATE_CHECK(LocalDate.MIN, LocalDate::toString, LocalDate::parse, SettingsControl::getState, State::getLatestUpdateCheck, State::setLatestUpdateCheck);

    private final BiConsumer<SettingsControl, Preferences> storeValueFunction;
    private final BiConsumer<SettingsControl, Preferences> loadValueFunction;

    <T> SettingValue(boolean defaultValue, Function<SettingsControl, T> rootElementFuntion, Function<T, Boolean> valueGetter,
            BiConsumer<T, Boolean> valueSetter) {
        String key = getKey();
        this.storeValueFunction = (settingsControl, preferences) -> {
            Boolean value = valueGetter.apply(rootElementFuntion.apply(settingsControl));
            if (value != defaultValue) {
                preferences.putBoolean(key, value);
            }
        };
        this.loadValueFunction = (settingsControl, preferences) -> valueSetter.accept(rootElementFuntion.apply(settingsControl),
                preferences.getBoolean(key, defaultValue));
    }

    SettingValue(int defaultValue, Function<SettingsControl, Integer> valueGetter, BiConsumer<SettingsControl, Integer> valueSetter) {
        this(defaultValue, Function.identity(), valueGetter, valueSetter);
    }

    <T> SettingValue(int defaultValue, Function<SettingsControl, T> rootElementFuntion, Function<T, Integer> valueGetter,
            BiConsumer<T, Integer> valueSetter) {
        String key = getKey();
        this.storeValueFunction = (settingsControl, preferences) -> {
            Integer value = valueGetter.apply(rootElementFuntion.apply(settingsControl));
            if (value != defaultValue) {
                preferences.putInt(key, value);
            }
        };
        this.loadValueFunction = (settingsControl, preferences) -> valueSetter.accept(rootElementFuntion.apply(settingsControl),
                preferences.getInt(key, defaultValue));
    }

    SettingValue(String defaultValue, Function<SettingsControl, String> valueGetter, BiConsumer<SettingsControl, String> valueSetter) {
        this(defaultValue, Function.identity(), valueGetter, valueSetter);
    }

    <T> SettingValue(String defaultValue, Function<SettingsControl, T> rootElementFuntion, Function<T, String> valueGetter,
            BiConsumer<T, String> valueSetter) {
        this(defaultValue, Function.identity(), Function.identity(), rootElementFuntion, valueGetter, valueSetter);
    }

    <T, V> SettingValue(V defaultValue, Function<V, String> toStringMapper, Function<String, V> toObjectMapper,
            Function<SettingsControl, T> rootElementFuntion, Function<T, V> valueGetter,
            BiConsumer<T, V> valueSetter) {
        String key = getKey();
        this.storeValueFunction = (settingsControl, preferences) -> {
            V value = valueGetter.apply(rootElementFuntion.apply(settingsControl));
            if (value != defaultValue) {
                preferences.put(key, toStringMapper.apply(value));
            }
        };
        this.loadValueFunction = (settingsControl, preferences) -> valueSetter.accept(rootElementFuntion.apply(settingsControl),
                toObjectMapper.apply(preferences.get(key, toStringMapper.apply(defaultValue))));
    }

    <T> SettingValue(Function<SettingsControl, T> rootElementFuntion, Function<T, Collection<String>> collectionGetter) {
        this(Function.identity(), Function.identity(), rootElementFuntion, collectionGetter);
    }

    <T, V> SettingValue(Function<V, String> toStringMapper, Function<String, V> toObjectMapper,
            Function<SettingsControl, T> rootElementFuntion, Function<T, Collection<V>> collectionGetter) {
        this(toStringMapper, toObjectMapper, rootElementFuntion, (object, v) -> collectionGetter.apply(object).add(v),
                (object, consumer) -> collectionGetter.apply(object).forEach(consumer::accept));
    }

    <T, V> SettingValue(Function<V, String> toStringMapper, Function<String, V> toObjectMapper,
            Function<SettingsControl, T> rootElementFuntion, BiConsumer<T, V> valueAdder, BiConsumer<T, Consumer<V>> valueConsumer) {
        String key = getKey();
        this.storeValueFunction = (settingsControl, preferences) -> {
            T object = rootElementFuntion.apply(settingsControl);
            IntWrapper i = IntWrapper.of(-1);
            valueConsumer.accept(object, value -> preferences.put(key + i.increment(), toStringMapper.apply(value)));
            if (i.getValue() > -1) {
                preferences.putInt(key + "Size", i.getValue() + 1);
            }
        };
        this.loadValueFunction = (settingsControl, preferences) -> {
            int numberOfItems = preferences.getInt(key + "Size", 0);
            T object = rootElementFuntion.apply(settingsControl);
            IntStream.range(0, numberOfItems).forEach(i -> {
                valueAdder.accept(object, toObjectMapper.apply(preferences.get(key + i, "")));
            });

        };
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class IntWrapper {
        private int i;

        public static IntWrapper of(int i) {
            return new IntWrapper(i);
        }

        public int increment() {
            return ++i;
        }

        public int getValue() {
            return i;
        }
    }

    public String getKey() {
        return CaseUtils.toCamelCase(name(), true, '_');
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
}
