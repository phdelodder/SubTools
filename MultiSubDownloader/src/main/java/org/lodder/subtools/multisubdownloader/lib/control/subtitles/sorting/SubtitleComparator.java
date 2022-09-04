package org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting;

import java.io.Serializable;
import java.util.Comparator;

import org.lodder.subtools.sublibrary.model.Subtitle;

public class SubtitleComparator implements Comparator<Subtitle>, Serializable {

    private static final long serialVersionUID = 3952954240904865448L;

    @Override
    public int compare(Subtitle a, Subtitle b) {
        /* inverse sorting */
        return Integer.compare(b.getScore(), a.getScore());
    }
}
