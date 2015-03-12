package org.lodder.subtools.multisubdownloader.lib;

import javax.swing.JFrame;

import org.lodder.subtools.multisubdownloader.gui.dialog.SelectDialog;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.model.Release;

public class SubtitleSelectionGUI extends SubtitleSelection {

  private JFrame frame;

  public SubtitleSelectionGUI(Settings settings, JFrame frame) {
    super(settings);
    this.frame = frame;
  }

  @Override
  public int getUserInput(Release release) {
    final SelectDialog sDialog = new SelectDialog(frame, release.getMatchingSubs(), release);

    if (sDialog.getAnswer() == SelectDialog.SelectionType.OK) {
      return sDialog.getSelection();
    }
    return sDialog.getAnswer().getSelectionCode();
  }
}
