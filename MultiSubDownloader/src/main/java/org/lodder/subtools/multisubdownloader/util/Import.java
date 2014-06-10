package org.lodder.subtools.multisubdownloader.util;

import javax.swing.*;

import org.lodder.subtools.multisubdownloader.lib.xml.XMLExclude;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.xml.XMLMappingTvdbScene;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: lodder
 * Date: 4/20/11
 * Time: 7:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class Import {

    private final SettingsControl settingsControl;
    private final JFrame frame;

    public enum ImportListType {
        EXCLUDE, TRANSLATE, PREFERENCES
    }

    public Import(JFrame frame, SettingsControl settingsControl) {
        this.frame = frame;
        this.settingsControl = settingsControl;
    }

    public void exclude(File file) {
        doImport(ImportListType.EXCLUDE, file);
    }

    public void translate(File file) {
        doImport(ImportListType.TRANSLATE, file);
    }

    public void preferences(File file){
        doImport(ImportListType.PREFERENCES, file);
    }

    public void doImport(ImportListType listType, File file) {
        try {
            if (listType == ImportListType.PREFERENCES){
                settingsControl.importPreferences(file);
            }else if (listType == ImportListType.TRANSLATE
                    && settingsControl.getSettings().getMappingSettings().getMappingList().size() == 0) {
                settingsControl.getSettings().getMappingSettings().setMappingList(XMLMappingTvdbScene.Read(file));
            } else if (listType == ImportListType.EXCLUDE
                    && settingsControl.getSettings().getExcludeList().size() == 0) {
                settingsControl.getSettings().setExcludeList(XMLExclude.Read(file));
            } else {
                final int response = JOptionPane
                        .showConfirmDialog(
                                frame,
                                "Do you want to add the imported list to the existing list?",
                                "Confirm", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    if (listType == ImportListType.EXCLUDE) {
                        settingsControl.getSettings().getExcludeList().addAll(XMLExclude.Read(file));
                    } else if (listType == ImportListType.TRANSLATE) {
                        settingsControl.getSettings().getMappingSettings().getMappingList().addAll(
                                XMLMappingTvdbScene.Read(file));
                    }
                }
            }
        } catch (final Throwable e) {
            Logger.instance.error(Logger.stack2String(e));
        }
    }
}
