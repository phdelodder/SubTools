package org.lodder.subtools.multisubdownloader.lib;

import org.lodder.subtools.multisubdownloader.gui.dialog.SelectDialog;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.model.VideoFile;

public class SubtitleSelectionGUI extends SubtitleSelection {

  public SubtitleSelectionGUI(Settings settings, VideoFile videoFile) {
    super(settings, videoFile);
  }

  @Override
  protected int getUserInput(VideoFile videoFile) {
    final SelectDialog sDialog =
        new SelectDialog(null, videoFile.getMatchingSubs(), videoFile.getFilename());

    if (sDialog.getAnswer() == SelectDialog.SelectionType.OK) {
      return sDialog.getSelection();
    }
    return sDialog.getAnswer().getSelectionCode();
  }
}
