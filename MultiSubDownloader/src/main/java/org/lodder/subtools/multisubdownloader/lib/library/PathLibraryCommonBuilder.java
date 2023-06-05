package org.lodder.subtools.multisubdownloader.lib.library;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.lodder.subtools.multisubdownloader.settings.model.structure.FolderStructureTag;
import org.lodder.subtools.multisubdownloader.settings.model.structure.MovieStructureTag;
import org.lodder.subtools.multisubdownloader.settings.model.structure.SerieStructureTag;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.lodder.subtools.sublibrary.util.StringUtil;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ StringUtil.class })
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

        folder = folder.replace(SerieStructureTag.SHOW_NAME.getLabel(), getShowName(tvRelease.getName()).removeIllegalWindowsChars());
        // order is important!
        folder = replaceFormattedEpisodeNumber(folder, SerieStructureTag.EPISODES_LONG, tvRelease.getEpisodeNumbers(), true);
        folder = replaceFormattedEpisodeNumber(folder, SerieStructureTag.EPISODES_SHORT, tvRelease.getEpisodeNumbers(), false);
        folder = replace(folder, SerieStructureTag.SEASON_LONG, formattedNumber(tvRelease.getSeason(), true));
        folder = replace(folder, SerieStructureTag.SEASON_SHORT, formattedNumber(tvRelease.getSeason(), false));
        folder = replace(folder, SerieStructureTag.EPISODE_LONG, formattedNumber(tvRelease.getEpisodeNumbers().get(0), true));
        folder = replace(folder, SerieStructureTag.EPISODE_SHORT, formattedNumber(tvRelease.getEpisodeNumbers().get(0), false));
        folder = replace(folder, SerieStructureTag.TITLE, tvRelease.getTitle());
        folder = replace(folder, SerieStructureTag.QUALITY, tvRelease.getQuality());
        folder = replace(folder, SerieStructureTag.DESCRIPTION, tvRelease.getDescription());
        if (isFolderReplaceSpace()) {
            folder = folder.replace(" ", getFolderReplacingSpaceSign());
        }
        folder = folder.trim();
        return Paths.get("", folder.split(FolderStructureTag.SEPARATOR.getLabel()));
    }

    protected Path buildMovie(MovieRelease movieRelease) {
        String folder = getFolderStructure();

        folder = replace(folder, MovieStructureTag.MOVIE_TITLE, movieRelease.getName().removeIllegalFilenameChars());
        folder = replace(folder, MovieStructureTag.YEAR, Integer.toString(movieRelease.getYear()));
        folder = replace(folder, MovieStructureTag.QUALITY, movieRelease.getQuality());
        if (isFolderReplaceSpace()) {
            folder = folder.replace(" ", getFolderReplacingSpaceSign());
        }
        folder = folder.trim();
        return Paths.get("", folder.split(FolderStructureTag.SEPARATOR.getLabel()));
    }

    protected abstract boolean hasAnyLibraryAction(LibraryActionType... libraryActions);

    protected abstract Path getLibraryFolder();

    protected abstract String getFolderStructure();

    protected abstract boolean isFolderReplaceSpace();

    protected abstract String getFolderReplacingSpaceSign();

}
