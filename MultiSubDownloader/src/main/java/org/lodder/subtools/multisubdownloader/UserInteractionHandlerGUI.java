package org.lodder.subtools.multisubdownloader;

import java.util.List;

import javax.swing.JFrame;

import org.lodder.subtools.multisubdownloader.gui.dialog.SelectDialog;
import org.lodder.subtools.sublibrary.data.UserInteractionSettingsIntf;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

import lombok.Getter;

@Getter
public class UserInteractionHandlerGUI extends org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandlerGUI implements UserInteractionHandler {

    public UserInteractionHandlerGUI(UserInteractionSettingsIntf settings, JFrame frame) {
        super(settings, frame);
    }

    @Override
    public List<Subtitle> selectSubtitles(Release release) {
        List<Integer> selection = new SelectDialog(getFrame(), release.getMatchingSubs(), release).getSelection();
        return selection.stream().map(i -> release.getMatchingSubs().get(i)).toList();

    }

    @Override
    public void dryRunOutput(Release release) {
        // TODO Auto-generated method stub

    }
}
