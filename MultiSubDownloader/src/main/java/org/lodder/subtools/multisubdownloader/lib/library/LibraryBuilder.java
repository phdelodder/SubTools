package org.lodder.subtools.multisubdownloader.lib.library;

import java.util.List;

import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.tvdb.TheTvdbAdapter;
import org.lodder.subtools.sublibrary.data.tvdb.model.TheTvdbSerie;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.lodder.subtools.sublibrary.util.OptionalExtension;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;

@RequiredArgsConstructor
@ExtensionMethod({ OptionalExtension.class })
public abstract class LibraryBuilder {

    @Getter
    private final LibrarySettings librarySettings;
    private final Manager manager;
    private final UserInteractionHandler userInteractionHandler;

    public abstract String build(Release release);

    protected String getShowName(String name) {
        if (librarySettings.isLibraryUseTVDBNaming()) {
            return TheTvdbAdapter.getInstance(manager, userInteractionHandler).getSerie(name).map(TheTvdbSerie::getSerieName).orElse(name);
        } else {
            return name;
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
