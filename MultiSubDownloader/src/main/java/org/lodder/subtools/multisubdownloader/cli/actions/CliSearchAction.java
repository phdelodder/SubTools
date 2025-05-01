package org.lodder.subtools.multisubdownloader.cli.actions;

import static manifold.ext.props.rt.api.PropOption.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import lombok.experimental.ExtensionMethod;
import manifold.ext.props.rt.api.get;
import manifold.ext.props.rt.api.override;
import org.lodder.subtools.multisubdownloader.CLI;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.UserInteractionHandlerCLI;
import org.lodder.subtools.multisubdownloader.actions.FileListAction;
import org.lodder.subtools.multisubdownloader.actions.SearchAction;
import org.lodder.subtools.multisubdownloader.exceptions.SearchSetupException;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.SubtitleFiltering;
import org.lodder.subtools.multisubdownloader.listeners.IndexingProgressListener;
import org.lodder.subtools.multisubdownloader.listeners.SearchProgressListener;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtensionMethod({Files.class})
public class CliSearchAction extends SearchAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(CliSearchAction.class);

    private final CLI cli;
    private final FileListAction fileListAction;
    private final ReleaseFactory releaseFactory;
    private final SubtitleFiltering filtering;
    private final boolean overwriteSubtitles;
    private final List<Path> folders;
    private final boolean recursive;

    @get @override Language language;
    @get(Protected) @override IndexingProgressListener indexingProgressListener;
    @get(Protected) @override SearchProgressListener searchProgressListener;

    public CliSearchAction(Settings settings, SubtitleProviderStore subtitleProviderStore,
        IndexingProgressListener indexingProgressListener, SearchProgressListener searchProgressListener, CLI cli,
        FileListAction fileListAction, Language language, ReleaseFactory releaseFactory, SubtitleFiltering filtering,
        List<Path> folders, boolean overwriteSubtitles=true, boolean recursive=true)
        throws SearchSetupException {
        super(settings, subtitleProviderStore);
        this.indexingProgressListener = indexingProgressListener;
        this.searchProgressListener = searchProgressListener;
        this.cli = cli;
        this.fileListAction = fileListAction;
        this.language = language;
        this.releaseFactory = releaseFactory;
        this.filtering = filtering;
        this.folders = folders;
        this.overwriteSubtitles = overwriteSubtitles;
        this.recursive = recursive;
        if (this.folders.isEmpty()) {
            throw new SearchSetupException("Folders must be set.");
        }
    }

    @Override
    protected List<Release> createReleases() {
        fileListAction.indexingProgressListener = this.indexingProgressListener;

        List<Path> files = this.folders.stream()
            .flatMap(folder -> fileListAction.getFileListing(folder, recursive, language, overwriteSubtitles)
                .stream())
            .toList();

        /* fix: remove carriage return from progressbar */
        System.out.println();

        int total = files.size();
        int index = 0;
        int progress = 0;

        LOGGER.debug("# Files found to process [{}] ", total);

        System.out.println(Messages.getText("CliSearchAction.ParsingFoundFiles"));
        this.indexingProgressListener.progress(progress);

        List<Release> releases = new ArrayList<>();
        for (Path file : files) {
            index++;
            progress = (int) Math.floor((float) index / total * 100);

            /* Tell progressListener which file we are processing */
            this.indexingProgressListener.progress(file.getFileNameAsString());

            Release release = this.releaseFactory.createRelease(file, userInteractionHandler);
            if (release == null) {
                continue;
            }

            releases.add(release);

            /* Update progressListener */
            this.indexingProgressListener.progress(progress);
        }

        return releases;
    }

    @Override
    public void onFound(Release release, List<Subtitle> subtitles) {
        subtitles.stream()
            .filter(subtitle -> filtering.useSubtitle(subtitle, release))
            .forEach(release::addMatchingSub);
        if (searchManager.progress < 100) {
            return;
        }
        LOGGER.debug("found files for doDownload [{}]", releases.size());

        /* stop printing progress */
        this.searchProgressListener.completed();

        this.cli.download(releases);
    }

    @Override
    protected UserInteractionHandler getUserInteractionHandler() {
        return new UserInteractionHandlerCLI(settings);
    }

    @Override
    protected void validate() {
    }
}
