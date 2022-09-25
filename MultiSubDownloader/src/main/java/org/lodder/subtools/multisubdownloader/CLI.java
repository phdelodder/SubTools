package org.lodder.subtools.multisubdownloader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.lodder.subtools.multisubdownloader.actions.DownloadAction;
import org.lodder.subtools.multisubdownloader.actions.FileListAction;
import org.lodder.subtools.multisubdownloader.actions.SubtitleSelectionAction;
import org.lodder.subtools.multisubdownloader.cli.actions.CliSearchAction;
import org.lodder.subtools.multisubdownloader.cli.progress.CLIFileindexerProgress;
import org.lodder.subtools.multisubdownloader.cli.progress.CLISearchProgress;
import org.lodder.subtools.multisubdownloader.exceptions.CliException;
import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.lib.Info;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.lib.SubtitleSelectionCLI;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.Filtering;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.model.Release;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final SubtitleSelectionAction subtitleSelectionAction;
    private boolean dryRun = false;


    public CLI(SettingsControl settingControl, Container app) {
        this.app = app;
        this.settingControl = settingControl;
        this.settings = settingControl.getSettings();
        checkUpdate((Manager) this.app.make("Manager"));
        downloadAction = new DownloadAction(settings, (Manager) this.app.make("Manager"));
        subtitleSelectionAction = new SubtitleSelectionAction(settings);
        subtitleSelectionAction.setSubtitleSelection(new SubtitleSelectionCLI(settings));
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
        this.force = line.hasOption("force");
        this.downloadall = line.hasOption("downloadall");
        this.recursive = line.hasOption("recursive");
        this.subtitleSelection = line.hasOption("selection");
        this.verboseProgress = line.hasOption("verboseprogress");
        this.dryRun = line.hasOption("dryrun");
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
            } catch (IOException | ManagerException e) {
                LOGGER.error("executeArgs: search", e);
            }
        }
    }

    public void search() {
        CliSearchAction searchAction = new CliSearchAction();

        searchAction.setCli(this);
        searchAction.setSettings(this.settings);
        searchAction.setProviderStore((SubtitleProviderStore) app.make("SubtitleProviderStore"));

        searchAction.setFolders(this.folders);
        searchAction.setRecursive(this.recursive);
        searchAction.setOverwriteSubtitles(this.force);
        searchAction.setLanguage(this.language);

        searchAction.setFileListAction(new FileListAction(this.settings));
        searchAction.setFiltering(new Filtering(this.settings));
        searchAction.setReleaseFactory(new ReleaseFactory(this.settings, (Manager) app.make("Manager")));

        CLIFileindexerProgress progressDialog = new CLIFileindexerProgress();
        CLISearchProgress searchProgress = new CLISearchProgress();
        progressDialog.setVerbose(this.verboseProgress);
        searchProgress.setVerbose(this.verboseProgress);

        searchAction.setIndexingProgressListener(progressDialog);
        searchAction.setSearchProgressListener(searchProgress);

        /* CLI has no benefit of running this in a separate Thread */
        searchAction.run();
    }

    private void download(Release release) throws IOException, ManagerException {
        int selection = subtitleSelectionAction.subtitleSelection(release, subtitleSelection, dryRun);
        if (selection >= 0) {
            if (downloadall) {
                System.out.println("Downloading ALL found subtitles for release: " + release.getFileName());
                for (int j = 0; j < release.getMatchingSubs().size(); j++) {
                    System.out.println("Downloading subtitle: " + release.getMatchingSubs().get(0).getFileName());
                    downloadAction.download(release, release.getMatchingSubs().get(j), j + 1);
                }
            } else {
                downloadAction.download(release, release.getMatchingSubs().get(selection));
            }
        } else if (dryRun && release.getMatchingSubs().size() == 0) {
            System.out.println("No substitles found for: " + release.getFileName());
        } else if (selection == -1 && !dryRun) {
            System.out.println("No substitles found for: " + release.getFileName());
        }
    }

    private List<File> getFolders(CommandLine line) {
        if (line.hasOption("folder")) {
            return List.of(new File(line.getOptionValue("folder")));
        } else {
            return new ArrayList<>(this.settings.getDefaultFolders());
        }
    }

    private Language getLanguage(CommandLine line) throws CliException {
        if (line.hasOption("language")) {
            String languageString = line.getOptionValue("language");
            return Arrays.stream(Language.values()).filter(lang -> lang.name().equalsIgnoreCase(languageString)).findAny()
                    .orElseThrow(() -> new CliException(Messages.getString("App.NoValidLanguage")));
        } else {
            System.out.println(Messages.getString("App.NoLanguageUseDefault"));
            return Language.DUTCH;
        }
    }
}
