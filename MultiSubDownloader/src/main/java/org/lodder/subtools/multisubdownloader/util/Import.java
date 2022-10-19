package org.lodder.subtools.multisubdownloader.util;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.lodder.subtools.multisubdownloader.lib.xml.XMLExclude;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.sublibrary.Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA. User: lodder Date: 4/20/11 Time: 7:53 AM To change this template use
 * File | Settings | File Templates.
 */
public class Import {

    private final SettingsControl settingsControl;
    private final JFrame frame;
    private static final Logger LOGGER = LoggerFactory.getLogger(Import.class);

    public enum ImportListType {
        EXCLUDE, PREFERENCES
    }

    public Import(JFrame frame, SettingsControl settingsControl) {
        this.frame = frame;
        this.settingsControl = settingsControl;
    }

    public void exclude(Manager manager, File file) {
        doImport(manager, ImportListType.EXCLUDE, file);
    }

    public void preferences(Manager manager, File file) {
        doImport(manager, ImportListType.PREFERENCES, file);
    }

    public void doImport(Manager manager, ImportListType listType, File file) {
        try {
            if (listType == ImportListType.PREFERENCES) {
                settingsControl.importPreferences(file);
            } else if (listType == ImportListType.EXCLUDE && settingsControl.getSettings().getExcludeList().size() == 0) {
                settingsControl.getSettings().setExcludeList(XMLExclude.read(file));
            } else {
                final int response = JOptionPane.showConfirmDialog(frame,
                        "Do you want to add the imported list to the existing list?", "Confirm",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    if (listType == ImportListType.EXCLUDE) {
                        settingsControl.getSettings().getExcludeList().addAll(XMLExclude.read(file));
                    }
                }
            }
        } catch (final Throwable e) {
            LOGGER.error("doImport", e);
        }
    }
}
