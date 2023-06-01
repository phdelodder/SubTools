package org.lodder.subtools.multisubdownloader.lib.library;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.lodder.subtools.sublibrary.util.StringUtil;

public abstract class PathLibraryCommonBuilder extends LibraryBuilder {

    public PathLibraryCommonBuilder(Manager manager, UserInteractionHandler userInteractionHandler) {
        super(manager, userInteractionHandler);
    }

    @Override
    public Path build(Release release) {
        if (hasAnyLibraryAction(LibraryActionType.MOVE, LibraryActionType.MOVEANDRENAME)) {
            Path subpath;
            if (release instanceof TvRelease tvRelease) {
                subpath = buildEpisode(tvRelease);
            } else if (release instanceof MovieRelease movieRelease) {
                subpath = buildMovie(movieRelease);
            } else {
                subpath = Path.of("");
            }
            return getLibraryFolder().resolve(subpath);
        } else {
            return release.getPath();
        }
    }

    protected Path buildEpisode(TvRelease tvRelease) {
        String folder = getFolderStructure();
        String show = getShowName(tvRelease.getName());
        if (isReplaceChars()) {
            show = StringUtil.removeIllegalWindowsChars(show);
        }

        folder = folder.replace("%SHOW NAME%", show);
        // order is important!
        folder = replaceFormattedEpisodeNumber(folder, "%EEX%", tvRelease.getEpisodeNumbers(), true);
        folder = replaceFormattedEpisodeNumber(folder, "%EX%", tvRelease.getEpisodeNumbers(), false);
        folder = folder.replace("%SS%", formattedNumber(tvRelease.getSeason(), true));
        folder = folder.replace("%S%", formattedNumber(tvRelease.getSeason(), false));
        folder = folder.replace("%EE%", formattedNumber(tvRelease.getEpisodeNumbers().get(0), true));
        folder = folder.replace("%E%", formattedNumber(tvRelease.getEpisodeNumbers().get(0), false));
        folder = folder.replace("%TITLE%", tvRelease.getTitle());
        folder = folder.replace("%QUALITY%", tvRelease.getQuality());
        folder = folder.replace("%DESCRIPTION%", tvRelease.getDescription());
        if (isFolderReplaceSpace()) {
            folder = folder.replace(" ", getFolderReplacingSpaceSign());
        }
        return Paths.get("", folder.split("%SEPARATOR%"));
    }

    protected Path buildMovie(MovieRelease movieRelease) {
        String folder = getFolderStructure();
        String title = movieRelease.getName();

        if (isReplaceChars()) {
            title = StringUtil.removeIllegalWindowsChars(title);
        }

        folder = folder.replace("%MOVIE TITLE%", title);
        folder = folder.replace("%YEAR%", Integer.toString(movieRelease.getYear()));
        folder = folder.replace("%QUALITY%", movieRelease.getQuality());
        if (isFolderReplaceSpace()) {
            folder = folder.replace(" ", getFolderReplacingSpaceSign());
        }
        return Paths.get("", folder.split("%SEPARATOR%"));
    }

    protected abstract boolean hasAnyLibraryAction(LibraryActionType... libraryActions);

    protected abstract Path getLibraryFolder();

    protected abstract String getFolderStructure();

    protected abstract boolean isReplaceChars();

    protected abstract boolean isFolderReplaceSpace();

    protected abstract String getFolderReplacingSpaceSign();

}
