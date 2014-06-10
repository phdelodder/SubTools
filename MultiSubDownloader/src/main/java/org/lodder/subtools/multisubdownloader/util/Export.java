package org.lodder.subtools.multisubdownloader.util;

import java.io.File;

import org.lodder.subtools.multisubdownloader.lib.xml.XMLExclude;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.xml.XMLMappingTvdbScene;

/**
 * Created by IntelliJ IDEA.
 * User: lodder
 * Date: 4/20/11
 * Time: 7:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class Export {

    private final SettingsControl settingsControl;

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

    public void preferences(File file){
        doExport(ExportListType.PREFERENCES, file);
    }

    public void doExport(ExportListType listType, File file) {
        try {
            if (listType == ExportListType.PREFERENCES){
                settingsControl.exportPreferences(file);
            }else if (listType == ExportListType.EXCLUDE) {
                XMLExclude.Write(settingsControl.getSettings().getExcludeList(), file);
            } else if (listType == ExportListType.TRANSLATE) {
                XMLMappingTvdbScene.Write(settingsControl.getSettings().getMappingSettings().getMappingList(), file);
            }
        } catch (final Throwable e) {
            Logger.instance.error(Logger.stack2String(e));
        }
    }
}
