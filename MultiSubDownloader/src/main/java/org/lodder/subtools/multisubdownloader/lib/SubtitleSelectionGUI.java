package org.lodder.subtools.multisubdownloader.lib;

import org.lodder.subtools.multisubdownloader.gui.dialog.SelectDialog;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.model.Release;

public class SubtitleSelectionGUI extends SubtitleSelection {

  public SubtitleSelectionGUI(Settings settings) {
    super(settings);
  }

  @Override
  public int getUserInput(Release release) {
    final SelectDialog sDialog =
        new SelectDialog(null, this.buildDisplayLines(release), release.getFilename());

    if (sDialog.getAnswer() == SelectDialog.SelectionType.OK) {
      return sDialog.getSelection();
    }
    return sDialog.getAnswer().getSelectionCode();
  }
}
