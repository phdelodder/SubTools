package org.lodder.subtools.multisubdownloader.settings;

import static org.lodder.subtools.multisubdownloader.settings.SettingValue.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsExcludeItem;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsExcludeType;
import org.lodder.subtools.multisubdownloader.settings.model.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;

public class SettingsControl {

    private final Preferences preferences;
    @Getter
    private Settings settings;
    @Getter
    private State state;
    private static final String BACKING_STORE_AVAIL = "BackingStoreAvail";
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsControl.class);

    public SettingsControl() {
        if (!backingStoreAvailable()) {
            LOGGER.error("Unable to store preferences, used debug for reason");
        }
        preferences = Preferences.userRoot().node("MultiSubDownloader");
        settings = new Settings();
        state = new State();
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
        Arrays.stream(SettingValue.values()).forEach(sv -> sv.load(this, preferences));
        updateProxySettings();
    }

    public void exportPreferences(File file) {
        store();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            preferences.exportSubtree(fos);
        } catch (IOException | BackingStoreException e) {
            LOGGER.error("exportPreferences", e);
        }
    }

    public void importPreferences(File file) {
        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            preferences.clear();
            Preferences.importPreferences(is);
            load();
        } catch (IOException | BackingStoreException | InvalidPreferencesFormatException e) {
            LOGGER.error("importPreferences", e);
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

    public void updateMappingFromOnline() throws Throwable {
        // TODO not working anymore
        // LOGGER.info(Messages.getString("SettingsControl.UpdateMapping"));
        // mappingSettingsCtrl.updateMappingFromOnline();
        // settings.setMappingSettings(mappingSettingsCtrl.getMappingSettings());
    }

    /**
     * Migrate settings layout for backward incompatibility changes.
     */
    private void migrateSettings() {
        SETTINGS_VERSION.load(this, preferences);
        int version = settings.getSettingsVersion();
        if (version == 0) {
            migrateSettingsV0ToV1();
            settings = new Settings();
            state = new State();
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

        int lastItemExclude = preferences.getInt("lastItemExclude", 0);
        if (lastItemExclude > 0) {
            List<SettingsExcludeItem> excludeList = settings.getExcludeList();
            IntStream.range(0, preferences.getInt("lastItemExclude", 0)).forEach(i -> {
                String description = preferences.get("ExcludeDescription" + i, "");
                SettingsExcludeType type;
                try {
                    type = SettingsExcludeType.valueOf(preferences.get("ExcludeType" + i, ""));
                } catch (IllegalArgumentException e) {
                    type = SettingsExcludeType.FOLDER;
                }
                excludeList.add(new SettingsExcludeItem(description, type));
            });
            EXCLUDE_ITEM.store(this, preferences);
        }

        EPISODE_LIBRARY_STRUCTURE.load(this, preferences);
        settings.getEpisodeLibrarySettings()
                .setLibraryFolderStructure(migrateLibraryStructureV0(settings.getEpisodeLibrarySettings().getLibraryFolderStructure()));
        EPISODE_LIBRARY_STRUCTURE.store(this, preferences);

        EPISODE_LIBRARY_FILENAME.load(this, preferences);
        settings.getEpisodeLibrarySettings()
                .setLibraryFilenameStructure(migrateLibraryStructureV0(settings.getEpisodeLibrarySettings().getLibraryFilenameStructure()));
        EPISODE_LIBRARY_FILENAME.store(this, preferences);

        MOVIE_LIBRARY_STRUCTURE.load(this, preferences);
        settings.getEpisodeLibrarySettings()
                .setLibraryFolderStructure(migrateLibraryStructureV0(settings.getEpisodeLibrarySettings().getLibraryFolderStructure()));
        MOVIE_LIBRARY_STRUCTURE.store(this, preferences);

        MOVIE_LIBRARY_FILENAME.load(this, preferences);
        settings.getEpisodeLibrarySettings()
                .setLibraryFilenameStructure(migrateLibraryStructureV0(settings.getEpisodeLibrarySettings().getLibraryFilenameStructure()));
        MOVIE_LIBRARY_FILENAME.store(this, preferences);

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

    private static String migrateLibraryStructureV0(String oldStructure) {
        return switch (oldStructure) {
            case "Show\\Season" -> "%SHOW NAME%%SEPARATOR%Season %S%";
            case "Show\\Series" -> "%SHOW NAME%%SEPARATOR%Series %S%";
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
}
