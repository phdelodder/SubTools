package org.lodder.subtools.multisubdownloader.lib.library;

import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.JTheTVDBAdapter;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBSerie;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;

import java.util.List;

public abstract class LibraryBuilder {

    protected final LibrarySettings librarySettings;
    private Manager manager;

    public LibraryBuilder(LibrarySettings librarySettings, Manager manager) {
        this.librarySettings = librarySettings;
        this.manager = manager;
    }

    public abstract String build(Release release);

    protected String getShowName(TvRelease tvRelease) {
        String show = "";
        if (librarySettings.isLibraryUseTVDBNaming()) {
            final JTheTVDBAdapter jtvdb = JTheTVDBAdapter.getAdapter(manager);
            TheTVDBSerie tvdbs = jtvdb.getSerie(tvRelease);
            if (tvdbs == null) {
                // use showname found for release as tvdb returns null
                show = tvRelease.getShow();
            } else {
                show = tvdbs.getSerieName();
            }
        } else {
            show = tvRelease.getShow();
        }
        return show;
    }

    protected String replaceFormatedEpisodeNumber(String structure, String tag,
            List<Integer> episodeNumbers, boolean leadingZero) {

        String formatedEpisodeNumber = "";
        if (structure.contains(tag)) {
            int posEnd = structure.indexOf(tag);
            String structurePart = structure.substring(0, posEnd);
            int posBegin = structurePart.lastIndexOf("%");
            String seperator = structure.substring(posBegin + 1, posEnd);

            StringBuilder builder = new StringBuilder();
            for (final int epNum : episodeNumbers) {
                builder.append(seperator).append(formatedNumber(epNum, leadingZero));
            }
            formatedEpisodeNumber += builder.toString();

            // strip the first seperator off
            formatedEpisodeNumber = formatedEpisodeNumber.substring(1);
        }
        return structure.replace(tag, formatedEpisodeNumber);

    }

    protected String formatedNumber(int number, boolean leadingZero) {
        if (number < 10 && leadingZero) {
            return "0" + number;
        }
        return Integer.toString(number);
    }
}
