package org.lodder.subtools.multisubdownloader.util;

import static org.lodder.subtools.multisubdownloader.Messages.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;

import com.google.gson.GsonBuilder;
import io.gsonfire.GsonFireBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.StandardException;
import lombok.experimental.UtilityClass;
import manifold.ext.props.rt.api.val;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.gui.dialog.MappingEpisodeNameDialog.MappingType;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.Manager.Value;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler.MessageSeverity;
import org.lodder.subtools.sublibrary.util.filefilter.ExtensionFileFilter;
import org.lodder.subtools.sublibrary.util.filefilter.JsonFileFilter;
import org.lodder.subtools.sublibrary.util.filefilter.XmlFileFilter;

@RequiredArgsConstructor
public class ExportImport {

    private final Manager manager;
    private final SettingsControl settingsControl;
    private final UserInteractionHandler userInteractionHandler;
    private final Component parent;

    @AllArgsConstructor
    public enum SettingsType {
        PREFERENCES(FileType.XML), SERIE_MAPPING(FileType.JSON);

        @val FileType fileType;
    }

    @AllArgsConstructor
    private enum FileType {
        XML(".xml", new XmlFileFilter()), JSON(".json", new JsonFileFilter());

        @val String extension;
        @val ExtensionFileFilter fileFilter;
    }

    public void importSettings(SettingsType listType) {
        chooseFile(listType.fileType).ifPresent(path -> {
            if (Files.notExists(path)) {
                userInteractionHandler.showMessage(getText("ImportExport.FileDoesNotExist"),
                    getText("ImportExport.ErrorWhileImporting"), MessageSeverity.WARNING);
                return;
            }
            try {
                switch (listType) {
                    case PREFERENCES ->
                        ExportImportPreferences.importSettings(path, userInteractionHandler, settingsControl);
                    case SERIE_MAPPING ->
                        ExportImportSerieMapping.importSettings(path, userInteractionHandler, manager);
                    default -> throw new IllegalArgumentException("Unexpected value: " + listType);
                }
            } catch (CorruptSettingsFileException e) {
                userInteractionHandler.showMessage(getText("ImportExport.ImportCorruptFile"),
                    getText("ImportExport.ErrorWhileImporting"), MessageSeverity.ERROR);
            } catch (Exception e) {
                userInteractionHandler.showMessage(getText("ImportExport.ErrorWhileImporting"),
                    getText("ImportExport.ErrorWhileImporting"), MessageSeverity.ERROR);
            }
        });
    }

    public void exportSettings(SettingsType listType) {
        chooseFile(listType.fileType).map(path -> path.toString().endsWith(listType.fileType.extension) ? path :
                path.getParent().resolve(path.getFileName().toString() + listType.fileType.extension))
            .ifPresent(path -> {
                try {
                    switch (listType) {
                        case PREFERENCES -> ExportImportPreferences.exportSettings(path, settingsControl);
                        case SERIE_MAPPING -> ExportImportSerieMapping.exportSettings(path, manager);
                        default -> throw new IllegalArgumentException("Unexpected value: " + listType);
                    }
                } catch (Exception e) {
                    userInteractionHandler.showMessage(getText("ImportExport.ErrorWhileExporting"),
                        getText("ImportExport.ErrorWhileExporting"), MessageSeverity.ERROR);
                }
            });
    }

    @UtilityClass
    public static class ExportImportPreferences {

        public void exportSettings(Path path, SettingsControl settingsControl) throws Exception {
            settingsControl.exportPreferences(path);
        }

        public void importSettings(Path path, UserInteractionHandler userInteractionHandler,
            SettingsControl settingsControl) throws CorruptSettingsFileException {
            try {
                settingsControl.importPreferences(path);
            } catch (IOException | BackingStoreException | InvalidPreferencesFormatException e) {
                throw new CorruptSettingsFileException(e);
            }
        }
    }

    @UtilityClass
    public static class ExportImportSerieMapping {

        public void exportSettings(Path path, Manager manager) throws IOException {
            List<SeriemappingWithKey> serieMappingsWithKey = MappingType.values().stream()
                .map(MappingType::getSelectionForKeyPrefixList)
                .flatMap(Arrays::stream)
                .flatMap(selectionForKeyPrefix -> manager.getCache(CacheType.DISK,
                        k -> k.startsWith(selectionForKeyPrefix.keyPrefix())).getEntries(SerieMapping.class)
                    .stream()
                    .map(pair -> new SeriemappingWithKey(pair.getKey(), pair.getValue())))
                .toList();
            Files.writeString(path, new GsonBuilder().setPrettyPrinting().create().toJson(serieMappingsWithKey));
        }

        public void importSettings(Path path, UserInteractionHandler userInteractionHandler, Manager manager)
            throws CorruptSettingsFileException {
            SeriemappingWithKey[] serieMappings;
            try {
                serieMappings = new GsonFireBuilder().enableHooks(SerieMapping.class)
                    .createGson()
                    .fromJson(Files.readString(path), SeriemappingWithKey[].class);
            } catch (IOException e) {
                throw new CorruptSettingsFileException(e);
            }
            getImportStyle(userInteractionHandler).ifPresent(importStyle -> {
                if (importStyle == ImportStyle.OVERWRITE) {
                    MappingType.values().stream()
                        .map(MappingType::getSelectionForKeyPrefixList)
                        .flatMap(Arrays::stream)
                        .forEach(selectionForKeyPrefix ->
                            manager.getCache(CacheType.DISK, k -> k.startsWith(selectionForKeyPrefix.keyPrefix))
                                .clearExpiredCache());
                }
                serieMappings.forEach(serieMapping ->
                    manager.getCache(CacheType.DISK, serieMapping.key).store(Value.of(serieMapping.serieMapping)));
            });
        }

        private static Optional<ImportStyle> getImportStyle(UserInteractionHandler userInteractionHandler) {
            return userInteractionHandler.selectFromList(Arrays.asList(ImportStyle.values()),
                getText("ImportExport.OverwriteOrAdd"),
                getText("ImportExport.OverwriteOrAddTitle"),
                option -> switch (option) {
                    case OVERWRITE -> getText("ImportExport.Overwrite");
                    case APPEND -> getText("ImportExport.Add");
                });
        }

        @AllArgsConstructor
        @Data
        private static class SeriemappingWithKey implements Serializable {
            @Serial private static final long serialVersionUID = 1L;
            private String key;
            private SerieMapping serieMapping;
        }
    }

    private Optional<Path> chooseFile(FileType fileType) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(fileType.fileFilter);
        int returnVal = fc.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return Optional.of(fc.getSelectedFile().toPath());
        } else {
            return Optional.empty();
        }
    }

    private enum ImportStyle {
        OVERWRITE, APPEND
    }

    @StandardException
    public static class CorruptSettingsFileException extends Exception {
    }
}
