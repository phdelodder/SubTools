package org.lodder.subtools.multisubdownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.lodder.subtools.multisubdownloader.actions.DownloadAction;
import org.lodder.subtools.multisubdownloader.actions.FileListAction;
import org.lodder.subtools.multisubdownloader.actions.SubtitleSelectionAction;
import org.lodder.subtools.multisubdownloader.cli.actions.CliSearchAction;
import org.lodder.subtools.multisubdownloader.cli.progress.CLIFileindexerProgress;
import org.lodder.subtools.multisubdownloader.cli.progress.CLISearchProgress;
import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.lib.Info;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.lib.SubtitleSelectionCLI;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.Filtering;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.Release;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CLI {

  private static final Logger LOGGER = LoggerFactory.getLogger(CLI.class);

  private final Container app;
  private Settings settings;
  private boolean recursive = false;
  private String languagecode = "";
  private boolean force = false;
  private List<File> folders = new ArrayList<>();
  private boolean downloadall = false;
  private boolean subtitleSelection = false;
  private boolean verboseProgress = false;
  private DownloadAction downloadAction;
  private SubtitleSelectionAction subtitleSelectionAction;

  public CLI(Settings settings, Container app) {
    this.app = app;
    this.settings = settings;
    checkUpdate((Manager) this.app.make("Manager"));
    downloadAction = new DownloadAction(settings, (Manager) this.app.make("Manager"));
    subtitleSelectionAction = new SubtitleSelectionAction(settings);
    subtitleSelectionAction.setSubtitleSelection(new SubtitleSelectionCLI(settings));
  }

  private void checkUpdate(Manager manager) {
    UpdateAvailableDropbox u = new UpdateAvailableDropbox(manager);
    if (u.checkProgram(settings.getUpdateCheckPeriod())) {
      System.out.println(Messages.getString("UpdateAppAvailable") + ": " + u.getUpdateUrl());
    }
  }

  public void setUp(CommandLine line) throws Exception {
    this.folders = getFolders(line);
    this.languagecode = getLanguageCode(line);
    this.force = line.hasOption("force");
    this.downloadall = line.hasOption("downloadall");
    this.recursive = line.hasOption("recursive");
    this.subtitleSelection = line.hasOption("selection");
    this.verboseProgress = line.hasOption("verboseprogress");
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
        LOGGER.error("executeArgs: search", e);
      }
    }
  }

  public void search() {
    CliSearchAction searchAction = new CliSearchAction();

    searchAction.setCommandLine(this);
    searchAction.setSettings(this.settings);
    searchAction.setProviderStore((SubtitleProviderStore) app.make("SubtitleProviderStore"));

    searchAction.setFolders(this.folders);
    searchAction.setRecursive(this.recursive);
    searchAction.setOverwriteSubtitles(this.force);
    searchAction.setLanguageCode(this.languagecode);

    searchAction.setFileListAction(new FileListAction(this.settings));
    searchAction.setFiltering(new Filtering(this.settings));
    searchAction
        .setReleaseFactory(new ReleaseFactory(this.settings, (Manager) app.make("Manager")));

    CLIFileindexerProgress progressDialog = new CLIFileindexerProgress();
    CLISearchProgress searchProgress = new CLISearchProgress();
    progressDialog.setVerbose(this.verboseProgress);
    searchProgress.setVerbose(this.verboseProgress);

    searchAction.setIndexingProgressListener(progressDialog);
    searchAction.setSearchProgressListener(searchProgress);

    /* CLI has no benefit of running this in a separate Thread */
    searchAction.run();
  }

  private void download(Release release) throws Exception {
    int selection = subtitleSelectionAction.subtitleSelection(release, subtitleSelection);
    if (selection >= 0) {
      if (downloadall) {
        System.out.println("Downloading ALL found subtitles for release: " + release.getFilename());
        for (int j = 0; j < release.getMatchingSubs().size(); j++) {
          System.out.println("Downloading subtitle: " + release.getMatchingSubs().get(0).getFilename());
          downloadAction.download(release, release.getMatchingSubs().get(j), j + 1);
        }
      } else {
        downloadAction.download(release, release.getMatchingSubs().get(selection));
      }
    } else {
      System.out.println("No substitles found for: " + release.getFilename());
    }
  }

  private List<File> getFolders(CommandLine line) {
    List<File> folders = new ArrayList<>();
    if (line.hasOption("folder")) {
      folders.add(new File(line.getOptionValue("folder")));
    } else {
      folders.addAll(this.settings.getDefaultFolders());
    }
    return folders;
  }

  private String getLanguageCode(CommandLine line) throws Exception {
    if (line.hasOption("language")) {
      String languagecode = line.getOptionValue("language").toLowerCase();
      if (!isValidlanguageCode(languagecode)) {
        throw new Exception(Messages.getString("App.NoValidLanguage"));
      }
      return languagecode;
    } else {
      System.out.println(Messages.getString("App.NoLanguageUseDefault"));
      return "nl";
    }
  }

  private boolean isValidlanguageCode(String languagecode) {
    return languagecode.equals("nl") || languagecode.equals("en");
  }

}
