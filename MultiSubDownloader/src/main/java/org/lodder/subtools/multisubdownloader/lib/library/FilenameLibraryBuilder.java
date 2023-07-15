package org.lodder.subtools.multisubdownloader.lib.library;

import java.nio.file.Path;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.multisubdownloader.settings.model.structure.SerieStructureTag;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.tvdb.TheTvdbAdapter;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.lodder.subtools.sublibrary.util.StringUtil;

import lombok.Setter;
import lombok.experimental.Accessors;

public class FilenameLibraryBuilder extends LibraryBuilder {

    private final String structure;
    private final boolean replaceSpace;
    private final Character replacingSpaceChar;
    private final boolean includeLanguageCode;
    private final Map<Language, String> languageTags;
    private final boolean rename;

    private FilenameLibraryBuilder(String structure, boolean replaceSpace, char replacingSpaceChar, boolean includeLanguageCode,
            Map<Language, String> languageTags, boolean useTvdb, TheTvdbAdapter tvdbAdapter, boolean rename) {
        super(useTvdb, tvdbAdapter);
        this.structure = structure;
        this.replaceSpace = replaceSpace;
        this.replacingSpaceChar = replacingSpaceChar;
        this.includeLanguageCode = includeLanguageCode;
        this.languageTags = languageTags;
        this.rename = rename;
    }

    public static FilenameLibraryBuilder fromSettings(LibrarySettings librarySettings, Manager manager,
            UserInteractionHandler userInteractionHandler) {
        return FilenameLibraryBuilder.builder()
                .structure(librarySettings.getLibraryFolderStructure())
                .replaceSpace(librarySettings.isLibraryFolderReplaceSpace())
                .replacingSpaceChar(librarySettings.getLibraryFolderReplacingSpaceChar())
                .includeLanguageCode(librarySettings.isLibraryIncludeLanguageCode())
                .languageTags(librarySettings.getLangCodeMap())
                .useTvdbName(librarySettings.isLibraryUseTVDBNaming())
                .tvdbAdapter(TheTvdbAdapter.getInstance(manager, userInteractionHandler))
                .rename(librarySettings.hasAnyLibraryAction(LibraryActionType.RENAME, LibraryActionType.MOVEANDRENAME))
                .build();
    }

    public static FilenameLibraryBuilderStructureIntf builder() {
        return new FilenameLibraryBuilderBuilder();
    }

    public interface FilenameLibraryBuilderStructureIntf {
        FilenameLibraryBuilderReplaceSpaceIntf structure(String structure);
    }

    public interface FilenameLibraryBuilderReplaceSpaceIntf {
        FilenameLibraryBuilderReplaceSpaceCharIntf replaceSpace(boolean replaceSpace);
    }

    public interface FilenameLibraryBuilderReplaceSpaceCharIntf {
        FilenameLibraryBuilderIncludeLanguageCodeIntf replacingSpaceChar(char replacingSpaceChar);
    }

    public interface FilenameLibraryBuilderIncludeLanguageCodeIntf {
        FilenameLibraryBuilderLanguageTagIntf includeLanguageCode(boolean includeLanguageCode);
    }

    public interface FilenameLibraryBuilderLanguageTagIntf {
        FilenameLibraryBuilderUseTvdbNameIntf languageTags(Map<Language, String> languageTags);
    }

    public interface FilenameLibraryBuilderUseTvdbNameIntf extends FilenameLibraryBuilderBuildIntf {
        FilenameLibraryBuilderTvdbAdapterIntf useTvdbName(boolean useTvdbName);
    }

    public interface FilenameLibraryBuilderTvdbAdapterIntf {
        FilenameLibraryBuilderRenameIntf tvdbAdapter(TheTvdbAdapter tvdbAdapter);
    }

    public interface FilenameLibraryBuilderRenameIntf {
        FilenameLibraryBuilderBuildIntf rename(boolean rename);
    }

    public interface FilenameLibraryBuilderBuildIntf {
        FilenameLibraryBuilder build();
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class FilenameLibraryBuilderBuilder implements
            FilenameLibraryBuilderStructureIntf,
            FilenameLibraryBuilderReplaceSpaceIntf,
            FilenameLibraryBuilderReplaceSpaceCharIntf,
            FilenameLibraryBuilderIncludeLanguageCodeIntf,
            FilenameLibraryBuilderLanguageTagIntf,
            FilenameLibraryBuilderUseTvdbNameIntf,
            FilenameLibraryBuilderTvdbAdapterIntf,
            FilenameLibraryBuilderRenameIntf,
            FilenameLibraryBuilderBuildIntf {
        private String structure;

        private boolean replaceSpace;
        private char replacingSpaceChar;

        private boolean includeLanguageCode;
        private Map<Language, String> languageTags;

        private boolean useTvdbName;
        private TheTvdbAdapter tvdbAdapter;

        private boolean rename;

        @Override
        public FilenameLibraryBuilder build() {
            return new FilenameLibraryBuilder(structure, replaceSpace, replacingSpaceChar, includeLanguageCode, languageTags, useTvdbName,
                    tvdbAdapter, rename);
        }
    }

    @Override
    public Path build(Release release) {
        if (rename) {
            String filename;
            if (release instanceof TvRelease tvRelease && StringUtils.isNotBlank(structure)) {
                filename = structure;
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
            if (replaceSpace) {
                filename = filename.replace(' ', replacingSpaceChar);
            }
            return Path.of(filename);
        } else {
            return Path.of(release.getFileName());
        }
    }

    public String buildSubtitle(Release release, Subtitle sub, String filename, Integer version) {
        return buildSubtitle(release, filename, sub.getLanguage(), version);
    }

    public String buildSubtitle(Release release, String filename, Language language, Integer version) {
        final String extension = "." + release.getExtension();
        if (version != null) {
            filename = filename.substring(0, filename.indexOf(extension)) + "-v" + version + "." + release.getExtension();
        }
        if (includeLanguageCode) {
            String langCode = language == null ? "" : languageTags.getOrDefault(language, language.getLangCode());
            filename = changeExtension(filename, !"".equals(langCode) ? ".%s.srt".formatted(langCode) : ".srt");
        } else {
            filename = changeExtension(filename, ".srt");
        }

        filename = StringUtil.removeIllegalWindowsChars(filename);
        if (replaceSpace) {
            filename = filename.replace(' ', replacingSpaceChar);
        }
        return filename;
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
