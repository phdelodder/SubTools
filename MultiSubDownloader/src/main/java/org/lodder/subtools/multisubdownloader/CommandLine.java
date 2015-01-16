package org.lodder.subtools.multisubdownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.multisubdownloader.lib.Actions;
import org.lodder.subtools.multisubdownloader.lib.Info;
import org.lodder.subtools.multisubdownloader.lib.control.VideoFileFactory;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.sublibrary.logging.Listener;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.VideoFile;

public class CommandLine implements Listener {

  private final SettingsControl prefctrl;
  private boolean recursive = false;
  private String languagecode = "";
  private boolean force = false;
  private File folder = null;
  private boolean downloadall = false;
  private final Actions actions;
  private boolean subtitleSelectionDialog = false;

  public CommandLine(final SettingsControl prefctrl) {
    Logger.instance.addListener(this);
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

  private List<VideoFile> search() throws Exception {
    List<VideoFile> l = new ArrayList<VideoFile>();
    List<File> folders = new ArrayList<File>();
    if (folder == null) {
      folders.addAll(prefctrl.getSettings().getDefaultIncomingFolders());
    } else {
      folders.add(folder);
    }

    for (File f : folders) {
      List<File> files = actions.getFileListing(f, recursive, languagecode, force);
      Logger.instance.debug("# Files found to process: " + files.size());
      VideoFile videoFile;
      for (File file : files) {
        try {
          videoFile = VideoFileFactory.get(file, f, prefctrl.getSettings(), languagecode);
          if (videoFile != null) l.add(videoFile);
        } catch (Exception e) {
          Logger.instance.error("Search Process" + Logger.stack2String(e));
        }
      }
    }
    Logger.instance.debug("found files for doDownload: " + l.size());
    return l;
  }

  public void download(VideoFile videoFile) throws Exception {
    int selection = actions.determineWhatSubtitleDownload(videoFile, subtitleSelectionDialog);
    if (selection >= 0) {
      if (downloadall) {
        for (int j = 0; j < videoFile.getMatchingSubs().size(); j++) {
          actions.download(videoFile, videoFile.getMatchingSubs().get(j), j + 1);
        }
        Logger.instance.log("Downloaded ALL subs for episode: " + videoFile.getFilename());
      } else {
        actions.download(videoFile, videoFile.getMatchingSubs().get(selection));
        Logger.instance.log("Downloaded sub for episode: " + videoFile.getFilename()
            + " using these subs: " + videoFile.getMatchingSubs().get(selection).getFilename());
      }
    } else {
      Logger.instance.log("No subs found for: " + videoFile.getFilename());
    }
  }

  public void Run() {
    Info.subtitleFiltering(prefctrl.getSettings());
    
    List<VideoFile> l;
    try {
      l = search();
      Info.downloadOptions(prefctrl.getSettings());
      for (VideoFile ef : l) {
        download(ef);
      }
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
