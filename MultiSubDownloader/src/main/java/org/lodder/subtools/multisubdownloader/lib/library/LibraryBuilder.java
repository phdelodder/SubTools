package org.lodder.subtools.multisubdownloader.lib.library;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

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

    public abstract Path build(Release release);

    protected String getShowName(String name) {
        if (librarySettings.isLibraryUseTVDBNaming()) {
            return TheTvdbAdapter.getInstance(manager, userInteractionHandler).getSerie(name).map(TheTvdbSerie::getSerieName).orElse(name);
        } else {
            return name;
        }
    }

    protected String replaceFormattedEpisodeNumber(String structure, String tag, List<Integer> episodeNumbers, boolean leadingZero) {
        String formattedEpisodeNumber = "";
        if (structure.contains(tag)) {
            int posEnd = structure.indexOf(tag);
            String structurePart = structure.substring(0, posEnd);
            int posBegin = structurePart.lastIndexOf("%");
            String separator = structure.substring(posBegin + 1, posEnd);

            formattedEpisodeNumber += episodeNumbers.stream()
                    .map(episode -> separator + formattedNumber(episode, leadingZero))
                    .collect(Collectors.joining());

            // strip the first separator off
            formattedEpisodeNumber = formattedEpisodeNumber.substring(1);
        }
        return structure.replace(tag, formattedEpisodeNumber);

    }

    protected String formattedNumber(int number, boolean leadingZero) {
        if (number < 10 && leadingZero) {
            return "0" + number;
        }
        return Integer.toString(number);
    }
}
