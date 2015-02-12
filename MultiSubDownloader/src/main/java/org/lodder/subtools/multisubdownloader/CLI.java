package org.lodder.subtools.multisubdownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.lodder.subtools.multisubdownloader.actions.DownloadAction;
import org.lodder.subtools.multisubdownloader.actions.FileListAction;
import org.lodder.subtools.multisubdownloader.actions.SubtitleSelectionAction;
import org.lodder.subtools.multisubdownloader.cli.progress.CLIFileindexerProgressDialog;
import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.gui.actions.search.CliSearchAction;
import org.lodder.subtools.multisubdownloader.gui.dialog.progress.search.CLISearchProgress;
import org.lodder.subtools.multisubdownloader.lib.Info;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.lib.SubtitleSelectionCLI;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.Filtering;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.sublibrary.logging.Listener;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Release;

public class CLI implements Listener {

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
    Logger.instance.addListener(this);
    this.app = app;
    this.settings = settings;
    downloadAction = new DownloadAction(settings);
    subtitleSelectionAction = new SubtitleSelectionAction(settings);
    subtitleSelectionAction.setSubtitleSelection(new SubtitleSelectionCLI(settings));
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
    Info.subtitleSources(this.settings);
    Info.subtitleFiltering(this.settings);
    this.search();
  }

  public void download(List<Release> releases) {
    Info.downloadOptions(this.settings);
    for (Release release : releases) {
      try {
        this.download(release);
      } catch (Exception e) {
        Logger.instance.error("executeArgs: search" + Logger.stack2String(e));
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
    searchAction.setReleaseFactory(new ReleaseFactory(this.settings));

    CLIFileindexerProgressDialog progressDialog = new CLIFileindexerProgressDialog();
    CLISearchProgress searchProgress = new CLISearchProgress();
    progressDialog.setVerbose(this.verboseProgress);
    searchProgress.setVerbose(this.verboseProgress);

    searchAction.setIndexingProgressListener(progressDialog);
    searchAction.setSearchProgressListener(searchProgress);

    /* CLI has no benefit of running this in a separate Thread */
    searchAction.run();
  }

  @Override
  public void log(String log) {
    System.out.println(log + "\r");
  }

  private void download(Release release) throws Exception {
    int selection = subtitleSelectionAction.subtitleSelection(release, subtitleSelection);
    if (selection >= 0) {
      if (downloadall) {
        Logger.instance
            .log("Downloading ALL found subtitles for release: " + release.getFilename());
        for (int j = 0; j < release.getMatchingSubs().size(); j++) {
          Logger.instance.log("Downloading subtitle: "
                              + release.getMatchingSubs().get(0).getFilename());
          downloadAction.download(release, release.getMatchingSubs().get(j), j + 1);
        }
      } else {
        downloadAction.download(release, release.getMatchingSubs().get(selection));
      }
    } else {
      Logger.instance.log("No subs found for: " + release.getFilename());
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
      Logger.instance.log(Messages.getString("App.NoLanguageUseDefault"));
      return "nl";
    }
  }

  private boolean isValidlanguageCode(String languagecode) {
    return languagecode.equals("nl") || languagecode.equals("en");
  }

}
