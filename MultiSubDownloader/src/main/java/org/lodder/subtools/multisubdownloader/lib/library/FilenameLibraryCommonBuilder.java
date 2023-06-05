package org.lodder.subtools.multisubdownloader.lib.library;

import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.settings.model.structure.SerieStructureTag;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.lodder.subtools.sublibrary.util.StringUtil;

public abstract class FilenameLibraryCommonBuilder extends LibraryBuilder {

    public FilenameLibraryCommonBuilder(Manager manager, UserInteractionHandler userInteractionHandler) {
        super(manager, userInteractionHandler);
    }

    @Override
    public Path build(Release release) {
        String filename;
        if (hasAnyLibraryAction(LibraryActionType.RENAME, LibraryActionType.MOVEANDRENAME)
                && release instanceof TvRelease tvRelease
                && StringUtils.isNotBlank(getFilenameStructure())) {

            filename = getFilenameStructure();
            // order is important!
            filename = replace(filename, SerieStructureTag.SHOW_NAME, getShowName(tvRelease.getName()));
            filename = replaceFormattedEpisodeNumber(filename, SerieStructureTag.EPISODES_LONG, tvRelease.getEpisodeNumbers(), true);
            filename = replaceFormattedEpisodeNumber(filename, SerieStructureTag.EPISODES_SHORT, tvRelease.getEpisodeNumbers(), false);
            filename = replace(filename, SerieStructureTag.SEASON_LONG, formattedNumber(tvRelease.getSeason(), true));
            filename = replace(filename, SerieStructureTag.SEASON_SHORT, formattedNumber(tvRelease.getSeason(), false));
            filename = replace(filename, SerieStructureTag.EPISODE_LONG, formattedNumber(tvRelease.getEpisodeNumbers().get(0), true));
            filename = replace(filename, SerieStructureTag.EPISODE_SHORT, formattedNumber(tvRelease.getEpisodeNumbers().get(0), false));
            filename = replace(filename, SerieStructureTag.TITLE, tvRelease.getTitle());
            filename = replace(filename, SerieStructureTag.QUALITY, release.getQuality());
            filename = replace(filename, SerieStructureTag.DESCRIPTION, release.getDescription());

            filename += "." + release.getExtension();
        } else {
            filename = release.getFileName();
        }
        filename = StringUtil.removeIllegalWindowsChars(filename);
        if (isFilenameReplaceSpace()) {
            filename = filename.replace(" ", getFilenameReplacingSpaceSign());
        }
        return Path.of(filename);
    }

    public String buildSubtitle(Release release, Subtitle sub, String filename, Integer version) {
        return buildSubtitle(release, filename, sub.getLanguage(), version);
    }

    public String buildSubtitle(Release release, String filename, Language language, Integer version) {
        final String extension = "." + release.getExtension();
        if (version != null) {
            filename = filename.substring(0, filename.indexOf(extension)) + "-v" + version + "." + release.getExtension();
        }
        if (isIncludeLanguageCode()) {
            if (language == null) {
                filename = changeExtension(filename, ".%s.srt".formatted(Language.ENGLISH.getLangCode()));
            } else {
                filename = switch (language) {
                    case DUTCH -> changeExtension(filename,
                            ".%s.srt".formatted(StringUtils.defaultIfBlank(getDefaultNlText(), Language.DUTCH.getLangCode())));
                    case ENGLISH -> changeExtension(filename,
                            ".%s.srt".formatted(StringUtils.defaultIfBlank(getDefaultEnText(), Language.ENGLISH.getLangCode())));
                    default -> changeExtension(filename, ".%s.srt".formatted(language.getLangCode()));
                };
            }
        } else {
            filename = changeExtension(filename, ".srt");
        }
        filename = StringUtil.removeIllegalWindowsChars(filename);
        if (isFilenameReplaceSpace()) {
            filename = filename.replace(" ", getFilenameReplacingSpaceSign());
        }
        return filename;
    }

    // ============================================== changeExtension
    // changes extension to new extension
    // example: x = changeExtension("data.txt", ".java")
    // will assign "data.java" to x.
    public static String changeExtension(String originalName, String newExtension) {
        int lastDot = originalName.lastIndexOf(".");
        if (lastDot != -1) {
            return originalName.substring(0, lastDot) + newExtension;
        } else {
            return originalName + newExtension;
        }
    }

    protected abstract boolean hasAnyLibraryAction(LibraryActionType... libraryActions);

    protected abstract String getFilenameStructure();

    protected abstract boolean isFilenameReplaceSpace();

    protected abstract String getFilenameReplacingSpaceSign();

    protected abstract boolean isIncludeLanguageCode();

    protected abstract String getDefaultNlText();

    protected abstract String getDefaultEnText();
}
