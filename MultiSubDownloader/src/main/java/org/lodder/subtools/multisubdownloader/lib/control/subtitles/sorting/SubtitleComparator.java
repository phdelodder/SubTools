package org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting;

import java.util.Comparator;
import org.lodder.subtools.sublibrary.model.Subtitle;

public class SubtitleComparator implements Comparator<Subtitle> {
  @Override
  public int compare(Subtitle a, Subtitle b) {
    /* inverse sorting */
    return Integer.compare(b.getScore(), a.getScore());
  }
}
