package org.lodder.subtools.multisubdownloader.util;

import java.io.File;

import org.lodder.subtools.multisubdownloader.lib.xml.XMLExclude;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.sublibrary.Manager;
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
        EXCLUDE, PREFERENCES
    }

    public Export(SettingsControl settingsControl) {
        this.settingsControl = settingsControl;
    }

    public void exclude(Manager manager, File file) {
        doExport(manager, ExportListType.EXCLUDE, file);
    }


    public void preferences(Manager manager, File file) {
        doExport(manager, ExportListType.PREFERENCES, file);
    }

    public void doExport(Manager manager, ExportListType listType, File file) {
        try {
            switch (listType) {
                case PREFERENCES -> settingsControl.exportPreferences(file);
                case EXCLUDE -> XMLExclude.write(settingsControl.getSettings().getExcludeList(), file);
                default -> throw new IllegalArgumentException("Unexpected value: " + listType);
            }
        } catch (final Throwable e) {
            LOGGER.error("doExport", e);
        }
    }
}
