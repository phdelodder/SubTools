package org.lodder.subtools.multisubdownloader.lib.library;

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
    public String build(Release release) {
        String filename = "";
        if ((LibraryActionType.RENAME.equals(getLibrarySettings().getLibraryAction())
                || LibraryActionType.MOVEANDRENAME.equals(getLibrarySettings().getLibraryAction()))
                && release instanceof TvRelease tvRelease
                && !getLibrarySettings().getLibraryFilenameStructure().isEmpty()) {
            String show = getShowName(tvRelease.getName());

            filename = getLibrarySettings().getLibraryFilenameStructure();
            // order is important!
            filename = filename.replaceAll("%SHOW NAME%", show);
            filename = replaceFormatedEpisodeNumber(filename, "%EEX%", tvRelease.getEpisodeNumbers(), true);
            filename = replaceFormatedEpisodeNumber(filename, "%EX%", tvRelease.getEpisodeNumbers(), false);
            filename = filename.replaceAll("%SS%", formatedNumber(tvRelease.getSeason(), true));
            filename = filename.replaceAll("%S%", formatedNumber(tvRelease.getSeason(), false));
            filename = filename.replaceAll("%EE%", formatedNumber(tvRelease.getEpisodeNumbers().get(0), true));
            filename = filename.replaceAll("%E%", formatedNumber(tvRelease.getEpisodeNumbers().get(0), false));
            filename = filename.replaceAll("%TITLE%", tvRelease.getTitle());
            filename = filename.replaceAll("%QUALITY%", release.getQuality());
            filename = filename.replaceAll("%DESCRIPTION%", release.getDescription());

            filename += "." + release.getExtension();
        } else {
            filename = release.getFileName();
        }
        if (getLibrarySettings().isLibraryReplaceChars()) {
            filename = StringUtil.removeIllegalWindowsChars(filename);
        }
        if (getLibrarySettings().isLibraryFilenameReplaceSpace()) {
            filename = filename.replaceAll(" ", getLibrarySettings().getLibraryFilenameReplacingSpaceSign());
        }
        return filename;
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
                filename = changeExtension(filename, ".nld.srt");
            } else {
                filename = switch (language) {
                    case DUTCH -> {
                        if ("".equals(getLibrarySettings().getDefaultNlText())) {
                            yield changeExtension(filename, ".nld.srt");
                        } else {
                            final String ext = "." + getLibrarySettings().getDefaultNlText() + ".srt";
                            yield changeExtension(filename, ext);
                        }
                    }
                    case ENGLISH -> {
                        if ("".equals(getLibrarySettings().getDefaultEnText())) {
                            yield changeExtension(filename, ".eng.srt");
                        } else {
                            final String ext = "." + getLibrarySettings().getDefaultEnText() + ".srt";
                            yield changeExtension(filename, ext);
                        }
                    }
                    default -> changeExtension(filename, ".nld.srt");
                };
            }
        } else {
            filename = changeExtension(filename, ".srt");
        }
        if (getLibrarySettings().isLibraryReplaceChars()) {
            filename = StringUtil.removeIllegalWindowsChars(filename);
        }
        if (getLibrarySettings().isLibraryFilenameReplaceSpace()) {
            filename = filename.replaceAll(" ", getLibrarySettings().getLibraryFilenameReplacingSpaceSign());
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
