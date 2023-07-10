package org.lodder.subtools.multisubdownloader.settings;

import static org.lodder.subtools.multisubdownloader.settings.SettingValue.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;
import java.util.stream.IntStream;

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
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.control.VideoPatterns;
import org.lodder.subtools.sublibrary.control.VideoPatterns.Source;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;
import org.lodder.subtools.sublibrary.util.TriConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ Files.class })
public class SettingsControl {

    private final Manager manager;
    private final Preferences preferences;
    @Getter
    private Settings settings;
    @Getter
    private State state;
    private static final String BACKING_STORE_AVAIL = "BackingStoreAvail";
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsControl.class);

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
            Arrays.stream(SettingValue.values()).forEach(sv -> sv.store(this, preferences));
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

    public void importPreferences(Path file) throws IOException, BackingStoreException, InvalidPreferencesFormatException {
        try (InputStream is = new BufferedInputStream(file.newInputStream())) {
            preferences.clear();
            Preferences.importPreferences(is);
            load();
        }
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

    /**
     * Migrate settings layout for backward incompatibility changes.
     */
    private void migrateSettings() {
        SettingValue.loadAll(this, preferences);
        int version = settings.getSettingsVersion();
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
        preferences.putInt("LocalSubtitlesSourcesFoldersSize", preferences.getInt("lastLocalSubtitlesSourcesFolder", 0));
        preferences.putInt("GeneralDefaultIncomingFolderSize", preferences.getInt("lastDefaultIncomingFolder", 0));
        preferences.putInt("DefaultSelectionQualitySize", preferences.getInt("lastItemDefaultSelectionQuality", 0));
        preferences.putInt("DefaultSelectionQualitySize", preferences.getInt("lastItemDefaultSelectionQuality", 0));
        preferences.putInt("DictionarySize", preferences.getInt("lastItemDictionary", 0));
        preferences.putInt("MappingVersion", preferences.getInt("mappingVersion", 0));

        // removed since version 6

        // int lastItemExclude = preferences.getInt("lastItemExclude", 0);
        // if (lastItemExclude > 0) {
        // List<SettingsExcludeItem> excludeList = settings.getExcludeList();
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
        settings.getEpisodeLibrarySettings()
                .setLibraryFolderStructure(migrateLibraryStructureV0(settings.getEpisodeLibrarySettings().getLibraryFolderStructure()));
        EPISODE_LIBRARY_FOLDER_STRUCTURE.store(this, preferences);

        EPISODE_LIBRARY_FILENAME_STRUCTURE.load(this, preferences);
        settings.getEpisodeLibrarySettings()
                .setLibraryFilenameStructure(migrateLibraryStructureV0(settings.getEpisodeLibrarySettings().getLibraryFilenameStructure()));
        EPISODE_LIBRARY_FILENAME_STRUCTURE.store(this, preferences);

        MOVIE_LIBRARY_FOLDER_STRUCTURE.load(this, preferences);
        settings.getEpisodeLibrarySettings()
                .setLibraryFolderStructure(migrateLibraryStructureV0(settings.getEpisodeLibrarySettings().getLibraryFolderStructure()));
        MOVIE_LIBRARY_FOLDER_STRUCTURE.store(this, preferences);

        MOVIE_LIBRARY_FILENAME_STRUCTURE.load(this, preferences);
        settings.getEpisodeLibrarySettings()
                .setLibraryFilenameStructure(migrateLibraryStructureV0(settings.getEpisodeLibrarySettings().getLibraryFilenameStructure()));
        MOVIE_LIBRARY_FILENAME_STRUCTURE.store(this, preferences);

        try {
            Arrays.stream(preferences.keys()).forEach(key -> {
                String value = preferences.get(key, "");
                preferences.remove(key);
                preferences.put(StringUtils.capitalize(key), value);
            });
        } catch (BackingStoreException e) {
            LOGGER.error("Error during migration of settings, ignoring...");
        }

        settings.setSettingsVersion(1);
        SETTINGS_VERSION.store(this, preferences);
    }

    @SuppressWarnings("deprecation")
    public void migrateSettingsV1ToV2() {
        settings.getEpisodeLibrarySettings()
                .setLibraryOtherFileAction(LibraryOtherFileActionType.fromString(preferences.get(EPISODE_LIBRARY_OTHER_FILE_ACTION.getKey(), "")));
        EPISODE_LIBRARY_OTHER_FILE_ACTION.store(this, preferences);

        settings.getMovieLibrarySettings()
                .setLibraryOtherFileAction(LibraryOtherFileActionType.fromString(preferences.get(MOVIE_LIBRARY_OTHER_FILE_ACTION.getKey(), "")));
        MOVIE_LIBRARY_OTHER_FILE_ACTION.store(this, preferences);

        settings.getEpisodeLibrarySettings()
                .setLibraryAction(LibraryActionType.fromString(preferences.get(EPISODE_LIBRARY_ACTION.getKey(), "")));
        EPISODE_LIBRARY_ACTION.store(this, preferences);

        settings.getMovieLibrarySettings()
                .setLibraryAction(LibraryActionType.fromString(preferences.get(MOVIE_LIBRARY_ACTION.getKey(), "")));
        MOVIE_LIBRARY_ACTION.store(this, preferences);

        settings.setSettingsVersion(2);
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

        settings.setSettingsVersion(3);
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
        settings.setSettingsVersion(4);
        SETTINGS_VERSION.store(this, preferences);
    }

    public void migrateSettingsV4ToV5() {
        Arrays.stream(MappingType.ADDIC7ED_PROXY.getSelectionForKeyPrefixList())
                .forEach(selectionForKeyPrefix -> MappingType.MAPPING_SUPPLIER.apply(manager, selectionForKeyPrefix)
                        .forEach(serieMappingPair -> {
                            manager.valueBuilder()
                                    .cacheType(CacheType.DISK)
                                    .key(serieMappingPair.getKey())
                                    .remove();
                        }));
        settings.setSettingsVersion(5);
        SETTINGS_VERSION.store(this, preferences);
    }

    public void migrateSettingsV5ToV6() {
        IntStream.range(0, preferences.getInt("ExcludeItemSize", 0))
                .forEach(i -> preferences.put("ExcludeItem" + i, preferences.get("ExcludeItem" + i, "").split("//", 2)[1]));
        EXCLUDE_ITEM.store(this, preferences);

        // Conversion from String to enum + remove duplicates
        int defaultSelectionQualitySize = preferences.getInt("DefaultSelectionQualitySize", 0);
        if (defaultSelectionQualitySize > 0) {
            List<Source> defaultSelectionQualitySizes = IntStream.range(0, defaultSelectionQualitySize)
                    .mapToObj(i -> VideoPatterns.Source.fromValue(preferences.get("DefaultSelectionQuality" + i, ""))).distinct().toList();
            IntStream.range(0, defaultSelectionQualitySizes.size())
                    .forEach(i -> preferences.put("DefaultSelectionQuality" + i, defaultSelectionQualitySizes.get(i).name()));
            if (defaultSelectionQualitySize != defaultSelectionQualitySizes.size()) {
                preferences.putInt("DefaultSelectionQualitySize", defaultSelectionQualitySizes.size());
                IntStream.range(defaultSelectionQualitySize, defaultSelectionQualitySizes.size())
                        .forEach(i -> preferences.remove("DefaultSelectionQuality" + i));
            }
        }
        DEFAULT_SELECTION_QUALITY.store(this, preferences);

        settings.setSettingsVersion(6);
        SETTINGS_VERSION.store(this, preferences);
    }

    public void migrateSettingsV6ToV7() {
        TriConsumer<String, Language, LibrarySettings> consumer = (label, language, librarySettings) -> {
            String value = preferences.get(label, null);
            preferences.remove(label);
            if (StringUtils.isNotBlank(value)) {
                librarySettings.getLangCodeMap().put(language, value.trim());
            }
        };
        consumer.accept("EpisodeLibraryDefaultNlText", Language.DUTCH, settings.getEpisodeLibrarySettings());
        consumer.accept("EpisodeLibraryDefaultEnText", Language.ENGLISH, settings.getEpisodeLibrarySettings());
        consumer.accept("MovieLibraryDefaultNlText", Language.DUTCH, settings.getMovieLibrarySettings());
        consumer.accept("MovieLibraryDefaultENText", Language.ENGLISH, settings.getMovieLibrarySettings());

        EPISODE_LIBRARY_LANG_CODE_MAPPING.store(this, preferences);
        MOVIE_LIBRARY_LANG_CODE_MAPPING.store(this, preferences);

        if (settings.getEpisodeLibrarySettings().hasAnyLibraryAction(LibraryActionType.RENAME, LibraryActionType.MOVEANDRENAME)) {
            if (StringUtils.isBlank(settings.getEpisodeLibrarySettings().getLibraryFilenameStructure())) {
                settings.getMovieLibrarySettings().setLibraryFilenameStructure("%SHOW NAME%%SEPARATOR%%Season %S%");
                MOVIE_LIBRARY_FILENAME_STRUCTURE.store(this, preferences);
                EPISODE_LIBRARY_FILENAME_STRUCTURE.store(this, preferences);
            }
            if (StringUtils.isBlank(settings.getEpisodeLibrarySettings().getLibraryFolderStructure())) {
                settings.getMovieLibrarySettings().setLibraryFolderStructure("%SHOW NAME%.S%SS%E%EE%.%TITLE%");
                EPISODE_LIBRARY_FOLDER_STRUCTURE.store(this, preferences);
            }
        }

        if (settings.getMovieLibrarySettings().hasAnyLibraryAction(LibraryActionType.RENAME, LibraryActionType.MOVEANDRENAME)) {
            if (StringUtils.isBlank(settings.getMovieLibrarySettings().getLibraryFilenameStructure())) {
                settings.getMovieLibrarySettings().setLibraryFilenameStructure("%MOVIE TITLE% (%YEAR%)");
                MOVIE_LIBRARY_FILENAME_STRUCTURE.store(this, preferences);
            }
        }

        settings.setSettingsVersion(7);
        SETTINGS_VERSION.store(this, preferences);
    }

    public void migrateSettingsV7ToV8() {
        if (settings.isLoginOpenSubtitlesEnabled()
                && !OpenSubtitlesApi.isValidCredentials(settings.getLoginOpenSubtitlesUsername(), settings.getLoginOpenSubtitlesPassword())) {
            settings.setLoginOpenSubtitlesEnabled(false);
            LOGIN_OPEN_SUBTITLES_ENABLED.store(this, preferences);
        }
        settings.setSettingsVersion(8);
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
        int version = manager.valueBuilder().cacheType(CacheType.DISK).key("DATABSE_VERSION").valueSupplier(() -> 0).get();
        if (version == 0) {
            migrateDatabaseV0ToV1();
        }
        if (version <= 1) {
            migrateDatabaseV1ToV2();
        }
    }

    private void migrateDatabaseV0ToV1() {
        manager.valueBuilder().cacheType(CacheType.DISK).keyFilter(k -> k.startsWith("TVDB-SerieMapping-")).remove();
        manager.valueBuilder().cacheType(CacheType.DISK).keyFilter(k -> k.startsWith("TVDB-SerieId-")).remove();
        manager.valueBuilder().cacheType(CacheType.DISK).key("DATABSE_VERSION").value(1).store();
    }

    private void migrateDatabaseV1ToV2() {
        List<Pair<String, SerieMapping>> entries = manager.valueBuilder().cacheType(CacheType.DISK)
                .keyFilter(k -> k.startsWith("SUBSCENE-serieName-")).returnType(SerieMapping.class).getEntries();
        List<Pair<String, SerieMapping>> editedEntries = entries.stream().map(pair -> {
            int lastIndexOfDash = pair.getKey().lastIndexOf("-");
            int season;
            try {
                season = Integer.parseInt(pair.getKey().substring(lastIndexOfDash + 1));
            } catch (NumberFormatException e) {
                season = -1;
            }
            String name = pair.getValue().getName();
            String providerId = pair.getValue().getProviderId();
            String providerName = pair.getValue().getProviderName();
            SerieMapping serieMapping = new SerieMapping(name, providerId, providerName, season);
            System.out.println(serieMapping);
            return Pair.of(pair.getKey(), serieMapping);
        }).toList();
        editedEntries.forEach(entry -> {
            manager.valueBuilder().cacheType(CacheType.DISK).key(entry.getKey()).remove();
            manager.valueBuilder().cacheType(CacheType.DISK).key(entry.getKey()).value(entry.getValue()).store();
        });
        manager.valueBuilder().cacheType(CacheType.DISK).key("DATABSE_VERSION").value(2).store();
    }
}
