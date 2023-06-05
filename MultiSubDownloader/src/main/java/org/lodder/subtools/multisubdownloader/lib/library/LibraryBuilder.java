package org.lodder.subtools.multisubdownloader.lib.library;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.settings.model.structure.StructureTag;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.tvdb.TheTvdbAdapter;
import org.lodder.subtools.sublibrary.data.tvdb.model.TheTvdbSerie;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.lodder.subtools.sublibrary.util.OptionalExtension;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;

@RequiredArgsConstructor
@ExtensionMethod({ OptionalExtension.class, StringUtils.class })
public abstract class LibraryBuilder {

    private final Manager manager;
    private final UserInteractionHandler userInteractionHandler;

    public abstract Path build(Release release);

    protected String getShowName(String name) {
        if (isUseTVDBNaming()) {
            return TheTvdbAdapter.getInstance(manager, userInteractionHandler).getSerie(name).map(TheTvdbSerie::getSerieName).orElse(name);
        } else {
            return name;
        }
    }

    protected abstract boolean isUseTVDBNaming();

    protected String replace(String structure, StructureTag tag, String value) {
        return structure.replace(tag.getLabel(), value);
    }

    protected String replaceFormattedEpisodeNumber(String structure, StructureTag tag, List<Integer> episodeNumbers, boolean leadingZero) {
        if (structure.contains(tag.getLabel())) {
            String afterLabel = structure.substringAfter(tag.getLabel());
            String separator = afterLabel.isNotEmpty() ? afterLabel.substring(0, 1) : "";
            if ("%".equals(separator)) {
                separator = "";
            }
            String formattedEpisodeNumber = episodeNumbers.stream()
                    .map(episode -> formattedNumber(episode, leadingZero))
                    .collect(Collectors.joining(separator));
            return structure.replace(tag.getLabel(), formattedEpisodeNumber);
        }
        return structure;

    }

    protected String formattedNumber(int number, boolean leadingZero) {
        if (number < 10 && leadingZero) {
            return "0" + number;
        }
        return Integer.toString(number);
    }
}
