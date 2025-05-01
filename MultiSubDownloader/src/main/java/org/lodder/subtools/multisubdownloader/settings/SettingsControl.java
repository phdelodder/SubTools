package org.lodder.subtools.multisubdownloader.settings;

import static manifold.ext.props.rt.api.PropOption.*;
import static org.lodder.subtools.multisubdownloader.settings.SettingValue.*;
import static org.lodder.subtools.sublibrary.cache.CacheType.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;
import java.util.stream.IntStream;

import lombok.experimental.ExtensionMethod;
import manifold.ext.props.rt.api.get;
import manifold.ext.props.rt.api.set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.lodder.subtools.multisubdownloader.gui.dialog.MappingEpisodeNameDialog.MappingType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.settings.model.State;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.OpenSubtitlesApi;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.Manager.Value;
import org.lodder.subtools.sublibrary.control.VideoPatterns;
import org.lodder.subtools.sublibrary.control.VideoPatterns.Source;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;
import org.lodder.subtools.sublibrary.util.function.TriConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtensionMethod({Files.class})
public class SettingsControl {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsControl.class);
    private static final String BACKING_STORE_AVAIL = "BackingStoreAvail";
    public static final String DATABASE_VERSION_KEY = "DATABSE_VERSION";

    private final Manager manager;
    private final Preferences preferences;
    @get @set(Private) Settings settings;
    @get @set(Private) State state;

    public SettingsControl(Manager manager) {
        if (!backingStoreAvailable()) {
            LOGGER.error("Unable to store preferences, used debug for reason");
        }
        this.manager = manager;
        this.preferences = Preferences.userRoot().node("MultiSubDownloader");
        this.settings = new Settings();
        this.state = new State();
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
            SettingValue.values().forEach(sv -> sv.store(this, preferences));
            updateProxySettings();
        } catch (BackingStoreException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void load() {
        migrateSettings();
        migrateDatabase();
        SettingValue.loadAll(this, preferences);
        updateProxySettings();
    }

    public void exportPreferences(Path file) throws IOException, BackingStoreException {
        store();
        try (OutputStream os = file.newOutputStream()) {
            preferences.exportSubtree(os);
        }
    }

    public void importPreferences(Path file)
        throws IOException, BackingStoreException, InvalidPreferencesFormatException {
        try (InputStream is = new BufferedInputStream(file.newInputStream())) {
            preferences.clear();
            Preferences.importPreferences(is);
            load();
        }
    }

    public void updateProxySettings() {
        if (settings.generalProxyEnabled) {
            System.getProperties().put("proxySet", "true");
            System.getProperties().put("proxyHost", settings.generalProxyHost);
            System.getProperties().put("proxyPort", settings.generalProxyPort);
        } else {
            System.getProperties().put("proxySet", "false");
        }
    }

    /**
     * Migrate settings layout for backward incompatibility changes.
     */
    private void migrateSettings() {
        SettingValue.loadAll(this, preferences);
        int version = settings.settingsVersion;
        if (version == 0) {
            migrateSettingsV0ToV1();
            settings = new Settings();
            state = new State();
            SettingValue.loadAll(this, preferences);
        }
        if (version <= 1) {
            migrateSettingsV1ToV2();
        }
        if (version <= 2) {
            migrateSettingsV2ToV3();
        }
        if (version <= 3) {
            migrateSettingsV3ToV4();
        }
        if (version <= 4) {
            migrateSettingsV4ToV5();
        }
        if (version <= 5) {
            migrateSettingsV5ToV6();
        }
        if (version <= 6) {
            migrateSettingsV6ToV7();
        }
        if (version <= 7) {
            migrateSettingsV7ToV8();
        }
    }

    public void migrateSettingsV0ToV1() {
        preferences.putInt("GeneralDefaultIncomingFolderSize", preferences.getInt("lastDefaultIncomingFolder", 0));
        preferences.putInt("LocalSubtitlesSourcesFoldersSize",
            preferences.getInt("lastLocalSubtitlesSourcesFolder", 0));
        preferences.putInt("GeneralDefaultIncomingFolderSize", preferences.getInt("lastDefaultIncomingFolder", 0));
        preferences.putInt("DefaultSelectionQualitySize", preferences.getInt("lastItemDefaultSelectionQuality", 0));
        preferences.putInt("DefaultSelectionQualitySize", preferences.getInt("lastItemDefaultSelectionQuality", 0));
        preferences.putInt("DictionarySize", preferences.getInt("lastItemDictionary", 0));
        preferences.putInt("MappingVersion", preferences.getInt("mappingVersion", 0));

        // removed since version 6

        // int lastItemExclude = preferences.getInt("lastItemExclude", 0);
        // if (lastItemExclude > 0) {
        // List<SettingsExcludeItem> excludeList = settings.excludeList;
        // IntStream.range(0, preferences.getInt("lastItemExclude", 0)).forEach(i -> {
        // String description = preferences.get("ExcludeDescription" + i, "");
        // PathMatchType type;
        // try {
        // type = PathMatchType.valueOf(preferences.get("ExcludeType" + i, ""));
        // } catch (IllegalArgumentException e) {
        // type = PathMatchType.FOLDER;
        // }
        // excludeList.add(new SettingsExcludeItem(description, type));
        // });
        // EXCLUDE_ITEM.store(this, preferences);
        // }

        EPISODE_LIBRARY_FOLDER_STRUCTURE.load(this, preferences);
        settings.episodeLibrarySettings.folderStructure =
            migrateLibraryStructureV0(settings.episodeLibrarySettings.folderStructure);
        EPISODE_LIBRARY_FOLDER_STRUCTURE.store(this, preferences);

        EPISODE_LIBRARY_FILENAME_STRUCTURE.load(this, preferences);
        settings.episodeLibrarySettings.filenameStructure =
            migrateLibraryStructureV0(settings.episodeLibrarySettings.filenameStructure);
        EPISODE_LIBRARY_FILENAME_STRUCTURE.store(this, preferences);

        MOVIE_LIBRARY_FOLDER_STRUCTURE.load(this, preferences);
        settings.episodeLibrarySettings.folderStructure =
            migrateLibraryStructureV0(settings.episodeLibrarySettings.folderStructure);
        MOVIE_LIBRARY_FOLDER_STRUCTURE.store(this, preferences);

        MOVIE_LIBRARY_FILENAME_STRUCTURE.load(this, preferences);
        settings.episodeLibrarySettings.filenameStructure =
            migrateLibraryStructureV0(settings.episodeLibrarySettings.filenameStructure);
        MOVIE_LIBRARY_FILENAME_STRUCTURE.store(this, preferences);

        try {
            preferences.keys().forEach(key -> {
                String value = preferences.get(key, "");
                preferences.remove(key);
                preferences.put(StringUtils.capitalize(key), value);
            });
        } catch (BackingStoreException e) {
            LOGGER.error("Error during migration of settings, ignoring...");
        }

        settings.settingsVersion = 1;
        SETTINGS_VERSION.store(this, preferences);
    }

    @SuppressWarnings("deprecation")
    public void migrateSettingsV1ToV2() {
        settings.episodeLibrarySettings.otherFileAction =
            LibraryOtherFileActionType.fromString(preferences.get(EPISODE_LIBRARY_OTHER_FILE_ACTION.getKey(), ""));
        EPISODE_LIBRARY_OTHER_FILE_ACTION.store(this, preferences);

        settings.movieLibrarySettings.otherFileAction =
            LibraryOtherFileActionType.fromString(preferences.get(MOVIE_LIBRARY_OTHER_FILE_ACTION.getKey(), ""));
        MOVIE_LIBRARY_OTHER_FILE_ACTION.store(this, preferences);

        settings.episodeLibrarySettings.action =
            LibraryActionType.fromString(preferences.get(EPISODE_LIBRARY_ACTION.getKey(), ""));
        EPISODE_LIBRARY_ACTION.store(this, preferences);

        settings.movieLibrarySettings.action =
            LibraryActionType.fromString(preferences.get(MOVIE_LIBRARY_ACTION.getKey(), ""));
        MOVIE_LIBRARY_ACTION.store(this, preferences);

        settings.settingsVersion = 2;
        SETTINGS_VERSION.store(this, preferences);
    }

    public void migrateSettingsV2ToV3() {
        // removed since version 6

        // int numberOfItems = preferences.getInt("DictionarySize", 0);
        // IntStream.range(0, numberOfItems).forEach(i -> {
        // String v = preferences.get("Dictionary" + i, "");
        // String[] items = v.split("\\\\\\\\");
        // int tvdbId = Integer.parseInt(items[1]);
        // SerieMapping tvdbMapping = new SerieMapping(items[0], tvdbId, String.valueOf(tvdbId));
        // manager.valueBuilder()
        // .cacheType(CacheType.DISK)
        // .key("TVDB-SerieId-%s-%s".formatted(tvdbMapping.getName(), null))
        // .value(tvdbMapping)
        // .store();
        // preferences.remove("Dictionary" + i);
        // });
        // preferences.remove("DictionarySize");

        settings.settingsVersion = 3;
        SETTINGS_VERSION.store(this, preferences);
    }

    public void migrateSettingsV3ToV4() {
        // removed since version 6

        // int numberOfItems = preferences.getInt("ExcludeItemSize", 0);
        // Pattern pattern = Pattern.compile("(.*?)\\[*==\\](.*?)");
        // IntStream.range(0, numberOfItems).forEach(i -> {
        // String v = preferences.get("ExcludeItem" + i, "");
        // Matcher matcher = pattern.matcher(v);
        // matcher.matches();
        // String newValue = matcher.group(2) + "//" + matcher.group(1);
        // preferences.put("ExcludeItem" + i, newValue);
        // });
        // EXCLUDE_ITEM.store(this, preferences);
        settings.settingsVersion = 4;
        SETTINGS_VERSION.store(this, preferences);
    }

    public void migrateSettingsV4ToV5() {
        MappingType.ADDIC7ED_PROXY.selectionForKeyPrefixList
            .forEach(selectionForKeyPrefix -> MappingType.MAPPING_SUPPLIER.apply(manager, selectionForKeyPrefix)
                .forEach(serieMappingPair -> manager.getCache(DISK, serieMappingPair.getKey()).remove()));
        settings.settingsVersion = 5;
        SETTINGS_VERSION.store(this, preferences);
    }

    public void migrateSettingsV5ToV6() {
        IntStream.range(0, preferences.getInt("ExcludeItemSize", 0))
            .forEach(i -> preferences.put("ExcludeItem" + i,
                preferences.get("ExcludeItem" + i, "").split("//", 2)[1]));
        EXCLUDE_ITEM.store(this, preferences);

        // Conversion from String to enum + remove duplicates
        int defaultSelectionQualitySize = preferences.getInt("DefaultSelectionQualitySize", 0);
        if (defaultSelectionQualitySize > 0) {
            List<Source> defaultSelectionQualitySizes = IntStream.range(0, defaultSelectionQualitySize)
                .mapToObj(i -> VideoPatterns.Source.fromValue(preferences.get("DefaultSelectionQuality" + i, "")))
                .distinct()
                .toList();
            IntStream.range(0, defaultSelectionQualitySizes.size())
                .forEach(i -> preferences.put("DefaultSelectionQuality" + i,
                    defaultSelectionQualitySizes.get(i).name()));
            if (defaultSelectionQualitySize != defaultSelectionQualitySizes.size()) {
                preferences.putInt("DefaultSelectionQualitySize", defaultSelectionQualitySizes.size());
                IntStream.range(defaultSelectionQualitySize, defaultSelectionQualitySizes.size())
                    .forEach(i -> preferences.remove("DefaultSelectionQuality" + i));
            }
        }
        DEFAULT_SELECTION_QUALITY.store(this, preferences);

        settings.settingsVersion = 6;
        SETTINGS_VERSION.store(this, preferences);
    }

    public void migrateSettingsV6ToV7() {
        TriConsumer<String, Language, LibrarySettings> consumer = (label, language, librarySettings) -> {
            String value = preferences.get(label, null);
            preferences.remove(label);
            if (StringUtils.isNotBlank(value)) {
                librarySettings.langCodeMap.put(language, value.trim());
            }
        };
        consumer.accept("EpisodeLibraryDefaultNlText", Language.DUTCH, settings.episodeLibrarySettings);
        consumer.accept("EpisodeLibraryDefaultEnText", Language.ENGLISH, settings.episodeLibrarySettings);
        consumer.accept("MovieLibraryDefaultNlText", Language.DUTCH, settings.movieLibrarySettings);
        consumer.accept("MovieLibraryDefaultENText", Language.ENGLISH, settings.movieLibrarySettings);

        EPISODE_LIBRARY_LANG_CODE_MAPPING.store(this, preferences);
        MOVIE_LIBRARY_LANG_CODE_MAPPING.store(this, preferences);

        if (settings.episodeLibrarySettings.hasAnyLibraryAction(LibraryActionType.RENAME,
            LibraryActionType.MOVEANDRENAME)) {
            if (StringUtils.isBlank(settings.episodeLibrarySettings.filenameStructure)) {
                settings.movieLibrarySettings.filenameStructure = "%SHOW NAME%%SEPARATOR%%Season %S%";
                MOVIE_LIBRARY_FILENAME_STRUCTURE.store(this, preferences);
                EPISODE_LIBRARY_FILENAME_STRUCTURE.store(this, preferences);
            }
            if (StringUtils.isBlank(settings.episodeLibrarySettings.folderStructure)) {
                settings.movieLibrarySettings.folderStructure = "%SHOW NAME%.S%SS%E%EE%.%TITLE%";
                EPISODE_LIBRARY_FOLDER_STRUCTURE.store(this, preferences);
            }
        }

        if (settings.movieLibrarySettings.hasAnyLibraryAction(LibraryActionType.RENAME,
            LibraryActionType.MOVEANDRENAME)) {
            if (StringUtils.isBlank(settings.movieLibrarySettings.filenameStructure)) {
                settings.movieLibrarySettings.filenameStructure = "%MOVIE TITLE% (%YEAR%)";
                MOVIE_LIBRARY_FILENAME_STRUCTURE.store(this, preferences);
            }
        }

        settings.settingsVersion = 7;
        SETTINGS_VERSION.store(this, preferences);
    }

    public void migrateSettingsV7ToV8() {
        if (settings.loginOpenSubtitlesEnabled &&
            !OpenSubtitlesApi.isValidCredentials(settings.loginOpenSubtitlesUsername,
                settings.loginOpenSubtitlesPassword)) {
            settings.loginOpenSubtitlesEnabled = false;
            LOGIN_OPEN_SUBTITLES_ENABLED.store(this, preferences);
        }
        settings.settingsVersion = 8;
        SETTINGS_VERSION.store(this, preferences);
    }

    private static String migrateLibraryStructureV0(String oldStructure) {
        return switch (oldStructure) {
            case "Show\\Season" -> "%SHOW NAME%%SEPARATOR%%Season %S%";
            case "Show\\Series" -> "%SHOW NAME%%SEPARATOR%%Series %S%";
            case "\\" -> "%SEPARATOR%";
            case "Show S00E00.extension" -> "%SHOW NAME% S%SS%E%EE%";
            case "Show S00E00 Title.extension" -> "%SHOW NAME% S%SS%E%EE% %TITLE%";
            case "Show 00X00 Title.extension" -> "%SHOW NAME% %SS%X%EE% %TITLE%";
            case "Show - S00E00.extension" -> "%SHOW NAME% - S%SS%E%EE%";
            case "Show S00E00 Title Quality.extension" -> "%SHOW NAME% S%SS%E%EE% %TITLE% %QUALITY%";
            case "Movie (Year)" -> "%MOVIE TITLE% (%YEAR%)";
            case "Year\\Movie" -> "%YEAR%%SEPARATOR%%MOVIE TITLE%";
            default -> oldStructure;
        };
    }

    private void migrateDatabase() {
        int version = manager.getCache(DISK, DATABASE_VERSION_KEY).get(() -> 0);
        if (version == 0) {
            migrateDatabaseV0ToV1();
        }
        if (version <= 1) {
            migrateDatabaseV1ToV2();
        }
    }

    private void migrateDatabaseV0ToV1() {
        manager.getCache(DISK, k -> k.startsWith("TVDB-SerieMapping-")).remove();
        manager.getCache(DISK, k -> k.startsWith("TVDB-SerieId-")).remove();
        manager.getCache(DISK, DATABASE_VERSION_KEY).store(Value.of(1));
    }

    private void migrateDatabaseV1ToV2() {
        List<Pair<String, SerieMapping>> editedEntries =
            manager.getCache(DISK, k -> k.startsWith("SUBSCENE-serieName-"))
                .getEntries(SerieMapping.class)
                .stream().map(pair -> {
                    int lastIndexOfDash = pair.getKey().lastIndexOf("-");
                    int season;
                    try {
                        season = Integer.parseInt(pair.getKey().substring(lastIndexOfDash + 1));
                    } catch (NumberFormatException e) {
                        season = -1;
                    }
                    String name = pair.getValue().name;
                    String providerId = pair.getValue().providerId;
                    String providerName = pair.getValue().providerName;
                    SerieMapping serieMapping = new SerieMapping(name, providerId, providerName, season);
                    return Pair.of(pair.getKey(), serieMapping);
                }).toList();
        editedEntries.forEach(entry -> {
            manager.getCache(DISK, entry.key).remove();
            manager.getCache(DISK, entry.key).store(Value.of(entry.getValue()));
        });
        manager.getCache(DISK, DATABASE_VERSION_KEY).store(Value.of(2));
    }
}
