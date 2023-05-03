package org.lodder.subtools.multisubdownloader.lib.library;

import java.nio.file.Path;

import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.lodder.subtools.sublibrary.util.StringUtil;

public class FilenameLibraryBuilder extends LibraryBuilder {

    public FilenameLibraryBuilder(LibrarySettings librarySettings, Manager manager, UserInteractionHandler userInteractionHandler) {
        super(librarySettings, manager, userInteractionHandler);
    }

    @Override
    public Path build(Release release) {
        String filename = "";
        if (getLibrarySettings().hasAnyLibraryAction(LibraryActionType.RENAME, LibraryActionType.MOVEANDRENAME)
                && release instanceof TvRelease tvRelease
                && !getLibrarySettings().getLibraryFilenameStructure().isEmpty()) {
            String show = getShowName(tvRelease.getName());

            filename = getLibrarySettings().getLibraryFilenameStructure();
            // order is important!
            filename = filename.replace("%SHOW NAME%", show);
            filename = replaceFormatedEpisodeNumber(filename, "%EEX%", tvRelease.getEpisodeNumbers(), true);
            filename = replaceFormatedEpisodeNumber(filename, "%EX%", tvRelease.getEpisodeNumbers(), false);
            filename = filename.replace("%SS%", formatedNumber(tvRelease.getSeason(), true));
            filename = filename.replace("%S%", formatedNumber(tvRelease.getSeason(), false));
            filename = filename.replace("%EE%", formatedNumber(tvRelease.getEpisodeNumbers().get(0), true));
            filename = filename.replace("%E%", formatedNumber(tvRelease.getEpisodeNumbers().get(0), false));
            filename = filename.replace("%TITLE%", tvRelease.getTitle());
            filename = filename.replace("%QUALITY%", release.getQuality());
            filename = filename.replace("%DESCRIPTION%", release.getDescription());

            filename += "." + release.getExtension();
        } else {
            filename = release.getFileName();
        }
        if (getLibrarySettings().isLibraryReplaceChars()) {
            filename = StringUtil.removeIllegalWindowsChars(filename);
        }
        if (getLibrarySettings().isLibraryFilenameReplaceSpace()) {
            filename = filename.replace(" ", getLibrarySettings().getLibraryFilenameReplacingSpaceSign());
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
        if (getLibrarySettings().isLibraryIncludeLanguageCode()) {
            if (language == null) {
                filename = changeExtension(filename, ".%s.srt".formatted(Language.ENGLISH.getLangCode()));
            } else {
                filename = switch (language) {
                    case DUTCH -> {
                        if ("".equals(getLibrarySettings().getDefaultNlText())) {
                            yield changeExtension(filename, ".%s.srt".formatted(Language.DUTCH.getLangCode()));
                        } else {
                            final String ext = "." + getLibrarySettings().getDefaultNlText() + ".srt";
                            yield changeExtension(filename, ext);
                        }
                    }
                    case ENGLISH -> {
                        if ("".equals(getLibrarySettings().getDefaultEnText())) {
                            yield changeExtension(filename, ".%s.srt".formatted(Language.ENGLISH.getLangCode()));
                        } else {
                            final String ext = "." + getLibrarySettings().getDefaultEnText() + ".srt";
                            yield changeExtension(filename, ext);
                        }
                    }
                    default -> changeExtension(filename, ".%s.srt".formatted(language.getLangCode()));
                };
            }
        } else {
            filename = changeExtension(filename, ".srt");
        }
        if (getLibrarySettings().isLibraryReplaceChars()) {
            filename = StringUtil.removeIllegalWindowsChars(filename);
        }
        if (getLibrarySettings().isLibraryFilenameReplaceSpace()) {
            filename = filename.replace(" ", getLibrarySettings().getLibraryFilenameReplacingSpaceSign());
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
}
