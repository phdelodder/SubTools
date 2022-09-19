package org.lodder.subtools.multisubdownloader.lib.library;

import java.util.List;

import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.JTheTVDBAdapter;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBSerie;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.OptionalExtension;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;

@RequiredArgsConstructor
@ExtensionMethod({ OptionalExtension.class })
public abstract class LibraryBuilder {

    protected final LibrarySettings librarySettings;
    private final Manager manager;

    public abstract String build(Release release);

    protected String getShowName(TvRelease tvRelease) {
        if (librarySettings.isLibraryUseTVDBNaming()) {
            return JTheTVDBAdapter.getAdapter(manager).getSerie(tvRelease)
                    .mapOrElseGet(TheTVDBSerie::getSerieName, () -> tvRelease.getName());
        } else {
            return tvRelease.getName();
        }
    }

    protected String replaceFormatedEpisodeNumber(String structure, String tag, List<Integer> episodeNumbers, boolean leadingZero) {
        String formatedEpisodeNumber = "";
        if (structure.contains(tag)) {
            int posEnd = structure.indexOf(tag);
            String structurePart = structure.substring(0, posEnd);
            int posBegin = structurePart.lastIndexOf("%");
            String separator = structure.substring(posBegin + 1, posEnd);

            StringBuilder builder = new StringBuilder();
            for (final int epNum : episodeNumbers) {
                builder.append(separator).append(formatedNumber(epNum, leadingZero));
            }
            formatedEpisodeNumber += builder.toString();

            // strip the first separator off
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
