package org.lodder.subtools.multisubdownloader.util;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;

import javax.swing.JFileChooser;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.gui.dialog.MappingEpisodeNameDialog;
import org.lodder.subtools.multisubdownloader.gui.dialog.MappingEpisodeNameDialog.MappingType;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler.MessageSeverity;
import org.lodder.subtools.sublibrary.util.StreamExtension;
import org.lodder.subtools.sublibrary.util.filefilter.ExtensionFileFilter;
import org.lodder.subtools.sublibrary.util.filefilter.JsonFileFilter;
import org.lodder.subtools.sublibrary.util.filefilter.XmlFileFilter;

import com.google.gson.GsonBuilder;

import java.awt.Component;

import io.gsonfire.GsonFireBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.StandardException;
import lombok.experimental.UtilityClass;

@RequiredArgsConstructor
public class ExportImport {

    private final Manager manager;
    private final SettingsControl settingsControl;
    private final UserInteractionHandler userInteractionHandler;
    private final Component parent;

    @RequiredArgsConstructor
    @Getter
    public enum SettingsType {
        PREFERENCES(FileType.XML),
        SERIE_MAPPING(FileType.JSON);

        private final FileType fileType;
    }

    @RequiredArgsConstructor
    @Getter
    private enum FileType {
        XML(".xml", new XmlFileFilter()),
        JSON(".json", new JsonFileFilter());

        private final String extension;
        private final ExtensionFileFilter fileFilter;
    }

    public void importSettings(SettingsType listType) {
        chooseFile(listType.getFileType()).ifPresent(path -> {
            if (Files.notExists(path)) {
                userInteractionHandler.showMessage(Messages.getString("ImportExport.FileDoesNotExist"),
                        Messages.getString("ImportExport.ErrorWhileImporting"), MessageSeverity.WARNING);
                return;
            }
            try {
                switch (listType) {
                    case PREFERENCES -> ExportImportPreferences.importSettings(path, userInteractionHandler, settingsControl);
                    case SERIE_MAPPING -> ExportImportSerieMapping.importSettings(path, userInteractionHandler, manager);
                    default -> throw new IllegalArgumentException("Unexpected value: " + listType);
                }
            } catch (CorruptSettingsFileException e) {
                userInteractionHandler.showMessage(
                        Messages.getString("ImportExport.ImportCorruptFile"),
                        Messages.getString("ImportExport.ErrorWhileImporting"),
                        MessageSeverity.ERROR);
            } catch (Exception e) {
                userInteractionHandler.showMessage(Messages.getString("ImportExport.ErrorWhileImporting"),
                        Messages.getString("ImportExport.ErrorWhileImporting"), MessageSeverity.ERROR);
            }
        });
    }

    public void exportSettings(SettingsType listType) {
        chooseFile(listType.getFileType())
                .map(path -> path.toString().endsWith(listType.getFileType().getExtension()) ? path
                        : path.getParent().resolve(path.getFileName().toString() + listType.getFileType().getExtension()))
                .ifPresent(path -> {
                    try {
                        switch (listType) {
                            case PREFERENCES -> ExportImportPreferences.exportSettings(path, settingsControl);
                            case SERIE_MAPPING -> ExportImportSerieMapping.exportSettings(path, manager);
                            default -> throw new IllegalArgumentException("Unexpected value: " + listType);
                        }
                    } catch (Exception e) {
                        userInteractionHandler.showMessage(Messages.getString("ImportExport.ErrorWhileExporting"),
                                Messages.getString("ImportExport.ErrorWhileExporting"), MessageSeverity.ERROR);
                    }
                });
    }

    @ExtensionMethod({ StreamExtension.class })
    @UtilityClass
    public static class ExportImportPreferences {

        public void exportSettings(Path path, SettingsControl settingsControl) throws Exception {
            settingsControl.exportPreferences(path);
        }

        public void importSettings(Path path, UserInteractionHandler userInteractionHandler, SettingsControl settingsControl)
                throws CorruptSettingsFileException {
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
            List<SeriemappingWithKey> serieMappingsWithKey = Arrays.stream(MappingEpisodeNameDialog.MappingType.values())
                    .map(MappingType::getSelectionForKeyPrefixList)
                    .map(Arrays::stream).flatMap(s -> s)
                    .flatMap(selectionForKeyPrefix -> manager.valueBuilder()
                            .cacheType(CacheType.DISK)
                            .keyFilter(k -> k.startsWith(selectionForKeyPrefix.keyPrefix()))
                            .returnType(SerieMapping.class)
                            .getEntries().stream().map(pair -> new SeriemappingWithKey(pair.getKey(), pair.getValue())))
                    .toList();
            Files.writeString(path, new GsonBuilder().setPrettyPrinting().create().toJson(serieMappingsWithKey));
        }

        public void importSettings(Path path, UserInteractionHandler userInteractionHandler, Manager manager) throws CorruptSettingsFileException {
            SeriemappingWithKey[] serieMappings;
            try {
                serieMappings = new GsonFireBuilder().enableHooks(SerieMapping.class).createGson().fromJson(Files.readString(path),
                        SeriemappingWithKey[].class);
            } catch (IOException e) {
                throw new CorruptSettingsFileException(e);
            }
            getImportStyle(userInteractionHandler).ifPresent(importStyle -> {
                if (importStyle == ImportStyle.OVERWRITE) {
                    Arrays.stream(MappingEpisodeNameDialog.MappingType.values())
                            .map(MappingType::getSelectionForKeyPrefixList)
                            .map(Arrays::stream).flatMap(s -> s)
                            .forEach(selectionForKeyPrefix -> manager.clearExpiredCacheBuilder()
                                    .cacheType(CacheType.DISK)
                                    .keyFilter((String k) -> k.startsWith(selectionForKeyPrefix.keyPrefix()))
                                    .clear());
                }
                Arrays.stream(serieMappings).forEach(serieMapping -> manager.valueBuilder()
                        .cacheType(CacheType.DISK)
                        .key(serieMapping.key)
                        .value(serieMapping.serieMapping)
                        .store());
            });
        }

        @AllArgsConstructor
        @Data
        private static class SeriemappingWithKey implements Serializable {
            private static final long serialVersionUID = 1L;
            private String key;
            private SerieMapping serieMapping;
        }
    }

    private Optional<Path> chooseFile(FileType fileType) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(fileType.getFileFilter());
        int returnVal = fc.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return Optional.of(fc.getSelectedFile().toPath());
        } else {
            return Optional.empty();
        }
    }

    private static Optional<ImportStyle> getImportStyle(UserInteractionHandler userInteractionHandler) {
        return userInteractionHandler.choice(Arrays.asList(ImportStyle.values()),
                Messages.getString("ImportExport.OverwriteOrAdd"), Messages.getString("ImportExport.OverwriteOrAddTitle"),
                option -> switch (option) {
                    case OVERWRITE -> Messages.getString("ImportExport.Overwrite");
                    case APPEND -> Messages.getString("ImportExport.Add");
                });
    }

    private enum ImportStyle {
        OVERWRITE, APPEND;
    }

    @StandardException
    private static class CorruptSettingsFileException extends Exception {
    }
}
