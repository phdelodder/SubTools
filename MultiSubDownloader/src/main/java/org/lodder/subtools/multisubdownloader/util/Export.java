package org.lodder.subtools.multisubdownloader.util;

import java.io.File;

import org.lodder.subtools.multisubdownloader.lib.xml.XMLExclude;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.sublibrary.xml.XMLMappingTvdbScene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA. User: lodder Date: 4/20/11 Time: 7:52 AM To change this template use
 * File | Settings | File Templates.
 */
public class Export {

    private final SettingsControl settingsControl;
    private static final Logger LOGGER = LoggerFactory.getLogger(Export.class);

    public enum ExportListType {
        EXCLUDE, TRANSLATE, PREFERENCES
    }

    public Export(SettingsControl settingsControl) {
        this.settingsControl = settingsControl;
    }

    public void exclude(File file) {
        doExport(ExportListType.EXCLUDE, file);
    }

    public void translate(File file) {
        doExport(ExportListType.TRANSLATE, file);
    }

    public void preferences(File file) {
        doExport(ExportListType.PREFERENCES, file);
    }

    public void doExport(ExportListType listType, File file) {
        try {
            switch (listType) {
                case PREFERENCES -> settingsControl.exportPreferences(file);
                case EXCLUDE -> XMLExclude.write(settingsControl.getSettings().getExcludeList(), file);
                case TRANSLATE -> XMLMappingTvdbScene.write(settingsControl.getSettings().getMappingSettings().getMappingList(), file);
                default -> throw new IllegalArgumentException("Unexpected value: " + listType);
            }
        } catch (final Throwable e) {
            LOGGER.error("doExport", e);
        }
    }
}
