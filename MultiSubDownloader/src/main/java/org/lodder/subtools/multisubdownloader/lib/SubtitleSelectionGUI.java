package org.lodder.subtools.multisubdownloader.lib;

import java.util.List;

import javax.swing.JFrame;

import org.lodder.subtools.multisubdownloader.gui.dialog.SelectDialog;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.model.Release;

public class SubtitleSelectionGUI extends SubtitleSelection {

    private final JFrame frame;

    public SubtitleSelectionGUI(Settings settings, JFrame frame) {
        super(settings);
        this.frame = frame;
    }

    @Override
    public List<Integer> getUserInput(Release release) {
        return new SelectDialog(frame, release.getMatchingSubs(), release).getSelection();
    }

    @Override
    public void dryRunOutput(Release release) {
        // TODO Auto-generated method stub

    }
}
