package org.lodder.subtools.multisubdownloader.lib.library;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.multisubdownloader.settings.model.structure.StructureTag;
import org.lodder.subtools.sublibrary.data.tvdb.TheTvdbAdapter;
import org.lodder.subtools.sublibrary.data.tvdb.model.TheTvdbSerie;
import org.lodder.subtools.sublibrary.model.Release;

@RequiredArgsConstructor
public abstract sealed class LibraryBuilder permits FilenameLibraryBuilder, PathLibraryBuilder {

    private final @Nullable TheTvdbAdapter tvdbAdapter;

    public abstract Path build(Release release);

    protected String getShowName(String name) {
        return tvdbAdapter != null ? tvdbAdapter.getSerie(name).map(TheTvdbSerie::getSerieName).orElse(name) : name;
    }

    protected String replace(String structure, StructureTag tag, String value) {
        return structure.replace(tag.label, value);
    }

    protected String replaceFormattedEpisodeNumber(String structure, StructureTag tag, List<Integer> episodeNumbers,
        boolean leadingZero) {
        if (structure.contains(tag.label)) {
            String afterLabel = StringUtils.substringAfter(structure, tag.label);
            String separator = StringUtils.isNotEmpty(afterLabel) ? afterLabel.substring(0, 1) : "";
            if ("%".equals(separator)) {
                separator = "";
            }
            String formattedEpisodeNumber = episodeNumbers.stream()
                .map(episode -> formatNumber(episode, leadingZero))
                .collect(Collectors.joining(separator));
            return structure.replace(tag.label, formattedEpisodeNumber);
        }
        return structure;

    }

    protected String formatNumber(int number, boolean leadingZero) {
        return number < 10 && leadingZero ? "0" + number : Integer.toString(number);
    }
}
