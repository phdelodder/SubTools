package org.lodder.subtools.multisubdownloader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.cli.CommandLine;
import org.lodder.subtools.multisubdownloader.actions.DownloadAction;
import org.lodder.subtools.multisubdownloader.actions.FileListAction;
import org.lodder.subtools.multisubdownloader.actions.UserInteractionHandlerAction;
import org.lodder.subtools.multisubdownloader.cli.CliOption;
import org.lodder.subtools.multisubdownloader.cli.actions.CliSearchAction;
import org.lodder.subtools.multisubdownloader.cli.progress.CLIFileindexerProgress;
import org.lodder.subtools.multisubdownloader.cli.progress.CLISearchProgress;
import org.lodder.subtools.multisubdownloader.exceptions.CliException;
import org.lodder.subtools.multisubdownloader.exceptions.SearchSetupException;
import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.lib.Info;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.Filtering;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.multisubdownloader.util.CLIExtension;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ CLIExtension.class })
public class CLI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CLI.class);

    private final Container app;
    private final SettingsControl settingControl;
    private final Settings settings;
    private boolean recursive = false;
    private Language language;
    private boolean force = false;
    private List<File> folders = new ArrayList<>();
    private boolean downloadall = false;
    private boolean subtitleSelection = false;
    private boolean verboseProgress = false;
    private final DownloadAction downloadAction;
    private final UserInteractionHandlerCLI userInteractionHandler;
    private final UserInteractionHandlerAction userInteractionHandlerAction;
    private boolean dryRun = false;

    public CLI(SettingsControl settingControl, Container app) {
        this.app = app;
        this.settingControl = settingControl;
        this.settings = settingControl.getSettings();
        checkUpdate((Manager) this.app.make("Manager"));
        userInteractionHandler = new UserInteractionHandlerCLI(settings);
        userInteractionHandlerAction = new UserInteractionHandlerAction(settings, userInteractionHandler);
        downloadAction = new DownloadAction(settings, (Manager) this.app.make("Manager"), userInteractionHandler);
    }

    private void checkUpdate(Manager manager) {
        UpdateAvailableGithub u = new UpdateAvailableGithub(manager, settingControl);
        if (u.shouldCheckForNewUpdate(settings.getUpdateCheckPeriod())) {
            System.out.println(Messages.getString("UpdateAppAvailable") + ": " + u.getLatestDownloadUrl());
        }
    }

    public void setUp(CommandLine line) throws CliException {
        this.folders = getFolders(line);
        this.language = getLanguage(line);
        this.force = line.hasCliOption(CliOption.FORCE);
        this.downloadall = line.hasCliOption(CliOption.DOWNLOAD_ALL);
        this.recursive = line.hasCliOption(CliOption.RECURSIVE);
        this.subtitleSelection = line.hasCliOption(CliOption.SELECTION);
        this.verboseProgress = line.hasCliOption(CliOption.VERBOSE_PROGRESS);
        this.dryRun = line.hasCliOption(CliOption.DRY_RUN);
    }

    public void run() {
        Info.subtitleSources(this.settings, true);
        Info.subtitleFiltering(this.settings, true);
        this.search();
    }

    public void download(List<Release> releases) {
        Info.downloadOptions(this.settings, true);
        for (Release release : releases) {
            try {
                this.download(release);
            } catch (Exception e) {
                LOGGER.error("Errow while downloading subtitle for %s (%s)".formatted(release.getReleaseDescription(), e.getMessage()), e);
            }
        }
    }

    public void search() {
        try {
            CliSearchAction
                    .createWithSettings(settings)
                    .subtitleProviderStore((SubtitleProviderStore) app.make("SubtitleProviderStore"))
                    .indexingProgressListener(new CLIFileindexerProgress().verbose(verboseProgress))
                    .searchProgressListener(new CLISearchProgress().verbose(verboseProgress))
                    .cli(this)
                    .fileListAction(new FileListAction(this.settings))
                    .language(language)
                    .releaseFactory(new ReleaseFactory(this.settings, (Manager) app.make("Manager")))
                    .filtering(new Filtering(this.settings))
                    .folders(folders)
                    .recursive(recursive)
                    .overwriteSubtitles(force)
                    .build()
                    /* CLI has no benefit of running this in a separate Thread */
                    .run();
        } catch (SearchSetupException e) {
            LOGGER.error("executeArgs: search (%s)".formatted(e.getMessage()), e);
        }
    }

    private void download(Release release) {
        List<Subtitle> selection;
        if (downloadall) {
            selection = release.getMatchingSubs();
            if (!selection.isEmpty()) {
                System.out.println("Downloading ALL found subtitles for release: " + release.getFileName());
            }
        } else {
            selection = userInteractionHandlerAction.subtitleSelection(release, subtitleSelection, dryRun);
        }
        if (selection.isEmpty()) {
            System.out.println("No substitles found for: " + release.getFileName());
        } else {
            IntStream.range(0, selection.size()).forEach(j -> {
                System.out.println("Downloading subtitle: " + release.getMatchingSubs().get(0).getFileName());
                try {
                    downloadAction.download(release, release.getMatchingSubs().get(j), selection.size() == 1 ? null : j + 1);
                } catch (IOException | ManagerException e) {
                    LOGGER.error("Errow while downloading subtitle for %s (%s)".formatted(release.getReleaseDescription(), e.getMessage()), e);
                }
            });
        }
    }

    private List<File> getFolders(CommandLine line) {
        if (line.hasCliOption(CliOption.FOLDER)) {
            return List.of(new File(line.getCliOptionValue(CliOption.FOLDER)));
        } else {
            return new ArrayList<>(this.settings.getDefaultFolders());
        }
    }

    private Language getLanguage(CommandLine line) throws CliException {
        if (line.hasCliOption(CliOption.LANGUAGE)) {
            String languageString = line.getCliOptionValue(CliOption.LANGUAGE);
            return Arrays.stream(Language.values()).filter(lang -> lang.name().equalsIgnoreCase(languageString)).findAny()
                    .orElseThrow(() -> new CliException(Messages.getString("App.NoValidLanguage")));
        } else {
            System.out.println(Messages.getString("App.NoLanguageUseDefault"));
            return Language.DUTCH;
        }
    }
}
