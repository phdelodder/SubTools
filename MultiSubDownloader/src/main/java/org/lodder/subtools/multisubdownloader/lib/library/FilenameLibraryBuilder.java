package org.lodder.subtools.multisubdownloader.lib.library;

import java.nio.file.Path;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.multisubdownloader.settings.model.structure.MovieStructureTag;
import org.lodder.subtools.multisubdownloader.settings.model.structure.SerieStructureTag;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.tvdb.TheTvdbAdapter;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;

public final class FilenameLibraryBuilder extends LibraryBuilder {

    private final String structure;
    private final boolean replaceSpace;
    private final Character replacingSpaceChar;
    private final boolean includeLanguageCode;
    private final Map<Language, String> languageTags;
    private final boolean rename;

    public FilenameLibraryBuilder(String structure, boolean replaceSpace, char replacingSpaceChar,
        boolean includeLanguageCode, Map<Language, String> languageTags, TheTvdbAdapter tvdbAdapter=null,
        boolean rename) {
        super(tvdbAdapter);
        this.structure = structure;
        this.replaceSpace = replaceSpace;
        this.replacingSpaceChar = replacingSpaceChar;
        this.includeLanguageCode = includeLanguageCode;
        this.languageTags = languageTags;
        this.rename = rename;
    }

    public static FilenameLibraryBuilder fromSettings(LibrarySettings libSettings, Manager manager,
        UserInteractionHandler userInteractionHandler) {
        return new FilenameLibraryBuilder(
            structure:libSettings.folderStructure,
            replaceSpace:libSettings.folderReplaceSpace,
            replacingSpaceChar:libSettings.folderReplacingSpaceChar,
            includeLanguageCode:libSettings.includeLanguageCode,
            languageTags:libSettings.langCodeMap,
            tvdbAdapter:libSettings.useTVDBNaming ? TheTvdbAdapter.getInstance(manager, userInteractionHandler) : null,
            rename:libSettings.hasAnyLibraryAction(LibraryActionType.RENAME, LibraryActionType.MOVEANDRENAME));
    }

    @Override
    public Path build(Release release) {
        if (rename && StringUtils.isNotBlank(structure)) {
            String filename = switch (release) {
                case TvRelease tvRelease -> {
                    String fName = structure;
                    // order is important!
                    fName = replace(fName, SerieStructureTag.SHOW_NAME, getShowName(tvRelease.name));
                    fName =
                        replaceFormattedEpisodeNumber(fName, SerieStructureTag.EPISODES_LONG, tvRelease.episodes, true);
                    fName = replaceFormattedEpisodeNumber(fName, SerieStructureTag.EPISODES_SHORT, tvRelease.episodes,
                        false);
                    fName = replace(fName, SerieStructureTag.SEASON_LONG, formatNumber(tvRelease.season, true));
                    fName = replace(fName, SerieStructureTag.SEASON_SHORT, formatNumber(tvRelease.season, false));
                    fName = replace(fName, SerieStructureTag.EPISODE_LONG, formatNumber(tvRelease.firstEpisode, true));
                    fName =
                        replace(fName, SerieStructureTag.EPISODE_SHORT, formatNumber(tvRelease.firstEpisode, false));
                    fName = replace(fName, SerieStructureTag.TITLE, tvRelease.title);
                    fName = replace(fName, SerieStructureTag.QUALITY, release.quality);
                    fName = replace(fName, SerieStructureTag.RELEASE_GROUP, release.releaseGroup);

                    fName += "." + release.extension;
                    yield fName;
                }
                case MovieRelease movieRelease -> {
                    String fName = structure;
                    // order is important!
                    fName = replace(fName, MovieStructureTag.MOVIE_TITLE, getShowName(movieRelease.name));
                    fName = replace(fName, MovieStructureTag.YEAR, formatNumber(movieRelease.year, false));
                    fName = replace(fName, MovieStructureTag.QUALITY, release.quality);
                    fName = replace(fName, MovieStructureTag.RELEASE_GROUP, release.releaseGroup);

                    fName += "." + release.extension;
                    yield fName;
                }
            };

            filename = filename.removeIllegalWindowsChars();
            if (replaceSpace) {
                filename = filename.replace(' ', replacingSpaceChar);
            }
            return Path.of(filename);
        } else {
            return Path.of(release.fileName);
        }
    }

    public String buildSubtitle(Release release, Subtitle sub, String filename, @Nullable Integer version) {
        return buildSubtitle(release, filename, sub.language, version);
    }

    public String buildSubtitle(Release release, String filename, Language language, @Nullable Integer version) {
        final String extension = "." + release.extension;
        String subFileName = filename;
        if (version != null) {
            subFileName = subFileName.substring(0, subFileName.indexOf(extension)) + "-v$version.${release.extension}";
        }
        if (includeLanguageCode) {
            String langCode = language == null ? "" : languageTags.getOrDefault(language, language.langCode);
            subFileName = changeExtension(subFileName, !"".equals(langCode) ? ".$langCode.srt" : ".srt");
        } else {
            subFileName = changeExtension(subFileName, ".srt");
        }
        subFileName = subFileName.removeIllegalWindowsChars();
        if (replaceSpace) {
            subFileName = subFileName.replace(' ', replacingSpaceChar);
        }
        return subFileName;
    }

    /**
     * Changes the extension of a file to a new extension.
     * <p>
     * Example: changeExtension("data.txt", ".java") will result in "data.java".
     *
     * @param fileName the name of the file
     * @param newExtension the new extension to be applied to the filename
     * @return the filename with the updated extension
     */
    private static String changeExtension(String fileName, String newExtension) {
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot != -1) {
            return fileName.substring(0, lastDot) + newExtension;
        } else {
            return fileName + newExtension;
        }
    }
}
