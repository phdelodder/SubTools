package org.lodder.subtools.multisubdownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.lib.Actions;
import org.lodder.subtools.multisubdownloader.lib.Info;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.multisubdownloader.workers.SearchHandler;
import org.lodder.subtools.multisubdownloader.workers.SearchManager;
import org.lodder.subtools.sublibrary.logging.Listener;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public class CommandLine implements Listener, SearchHandler {

  private final SettingsControl prefctrl;
  private final Container app;
  private boolean recursive = false;
  private String languagecode = "";
  private boolean force = false;
  private File folder = null;
  private boolean downloadall = false;
  private final Actions actions;
  private boolean subtitleSelectionDialog = false;
  private List<Release> releases;
  private SearchManager searchManager;
  private ReleaseFactory releaseFactory;

  public CommandLine(final SettingsControl prefctrl, Container app) {
    Logger.instance.addListener(this);
    this.app = app;
    this.prefctrl = prefctrl;
    try {
      if (this.prefctrl.getSettings().isAutoUpdateMapping()) {
        Logger.instance.log("Auto updating mapping ....");
        this.prefctrl.updateMappingFromOnline();
      }
    } catch (Throwable e) {
      Logger.instance.error(Logger.stack2String(e));
    }
    actions = new Actions(prefctrl.getSettings(), true);
  }

  public void setReleaseFactory(ReleaseFactory releaseFactory) {
    this.releaseFactory = releaseFactory;
  }

  private void search() throws Exception {
    List<File> folders = new ArrayList<File>();
    if (folder == null) {
      folders.addAll(prefctrl.getSettings().getDefaultIncomingFolders());
    } else {
      folders.add(folder);
    }

    List<File> files = new ArrayList<>();
    for (File folder : folders) {
      files.addAll(actions.getFileListing(folder, recursive, languagecode, force));
    }
    Logger.instance.debug("# Files found to process: " + files.size());

    releases = new ArrayList<>();
    for (File file : files) {
      Release release = releaseFactory.createRelease(file);
      if (release == null)
        continue;

      releases.add(release);
    }

    SubtitleProviderStore subtitleProviderStore = (SubtitleProviderStore) this.app.make("SubtitleProviderStore");

    searchManager = new SearchManager(this.prefctrl.getSettings());
    searchManager.setLanguage(this.getLanguagecode());

    for (SubtitleProvider subtitleProvider : subtitleProviderStore.getAllProviders()) {
      if (!this.prefctrl.getSettings().isSerieSource(subtitleProvider.getName()))
        continue;

      searchManager.addProvider(subtitleProvider);
    }

    for (Release release : releases) {
      searchManager.addRelease(release);
    }

    searchManager.onFound(this);

    searchManager.start();
  }

  @Override
  public void onFound(Release release, List<Subtitle> subtitles) {
    release.getMatchingSubs().addAll(subtitles);
    if (searchManager.getProgress() < 100) return;

    Logger.instance.debug("found files for doDownload: " + releases.size());
    this.download();
  }

  public void download() {
    Info.downloadOptions(prefctrl.getSettings());
    for(Release release : releases) {
      try {
        this.download(release);
      } catch (Exception e) {
        Logger.instance.error("executeArgs: search" + Logger.stack2String(e));
      }
    }
  }

  public void download(Release release) throws Exception {
    int selection = actions.determineWhatSubtitleDownload(release, subtitleSelectionDialog);
    if (selection >= 0) {
      if (downloadall) {
        for (int j = 0; j < release.getMatchingSubs().size(); j++) {
          actions.download(release, release.getMatchingSubs().get(j), j + 1);
        }
        Logger.instance.log("Downloaded ALL subs for episode: " + release.getFilename());
      } else {
        actions.download(release, release.getMatchingSubs().get(selection));
        Logger.instance.log("Downloaded sub for episode: " + release.getFilename()
            + " using these subs: " + release.getMatchingSubs().get(selection).getFilename());
      }
    } else {
      Logger.instance.log("No subs found for: " + release.getFilename());
    }
  }

  public void Run() {
    Info.subtitleSources(prefctrl.getSettings());
    Info.subtitleFiltering(prefctrl.getSettings());

    try {
      search();
    } catch (Exception e) {
      Logger.instance.error("executeArgs: search" + Logger.stack2String(e));
    }
  }

  @Override
  public void log(String log) {
    System.out.println(log + "\r");
  }

  public void CheckUpdate() {
    UpdateAvailableDropbox u = new UpdateAvailableDropbox();
    if (u.checkProgram()) {
      Logger.instance.log("Update available! : " + u.getUpdateUrl());
    }
  }

  public boolean isRecursive() {
    return recursive;
  }

  public void setRecursive(boolean recursive) {
    this.recursive = recursive;
  }

  public String getLanguagecode() {
    return languagecode;
  }

  public void setLanguagecode(String languagecode) {
    this.languagecode = languagecode;
  }

  public boolean isForce() {
    return force;
  }

  public void setForce(boolean force) {
    this.force = force;
  }

  public File getFolder() {
    return folder;
  }

  public void setFolder(File folder) {
    this.folder = folder;
  }

  public boolean isDownloadall() {
    return downloadall;
  }

  public void setDownloadall(boolean downloadall) {
    this.downloadall = downloadall;
  }

  public boolean isSubtitleSelectionDialog() {
    return subtitleSelectionDialog;
  }

  public void setSubtitleSelectionDialog(boolean subtitleSelectionDialog) {
    this.subtitleSelectionDialog = subtitleSelectionDialog;
  }

}
