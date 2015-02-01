package org.lodder.subtools.multisubdownloader.gui.workers;

import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;

import org.lodder.subtools.multisubdownloader.gui.dialog.Cancelable;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusMessenger;
import org.lodder.subtools.multisubdownloader.lib.Actions;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.control.VideoPatterns;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.util.FilenameExtensionFilter;
import org.lodder.subtools.sublibrary.util.StringUtils;

public class TypedRenameWorker extends SwingWorker<Void, String> implements Cancelable {


  private File dir;
  private File basedir;
  private Settings settings;
  private LibrarySettings librarySettings;
  private VideoType videoType;
  private final FilenameExtensionFilter patterns;
  private boolean isRecursive;

  public TypedRenameWorker(File dir, File basedir, Settings settings,
      LibrarySettings librarySettings, VideoType videoType, boolean isRecursive) {
    setParameters(dir, basedir, settings, librarySettings, videoType, isRecursive);
    patterns =
        new FilenameExtensionFilter(
            StringUtils.join(VideoPatterns.EXTENSIONS, new String[] {"srt"}));
  }

  public void setParameters(File dir, File basedir, Settings settings,
      LibrarySettings librarySettings, VideoType videoType, boolean isRecursive) {
    this.dir = dir;
    this.basedir = basedir;
    this.settings = settings;
    this.librarySettings = librarySettings;
    this.videoType = videoType;
    this.isRecursive = isRecursive;
  }

  @Override
  protected Void doInBackground() throws Exception {
    rename(dir);
    return null;
  }

  private void rename(File dir) {
    File[] contents = dir.listFiles();
    for (final File file : contents) {
      if (file.isFile() && !file.getName().contains("sample")
          && patterns.accept(file.getAbsoluteFile(), file.getName())) {
        Release release;
        try {
          release = ReleaseFactory.createRelease(file, settings);
          publish(release.getFilename());
          if (release.getVideoType() == videoType && release != null)
            Actions.rename(librarySettings, file, release);

        } catch (Exception e) {
          Logger.instance.log("Series Rename " + e.getMessage());
        }
      } else if (file.isDirectory() && isRecursive) {
        rename(file);
      }
    }
  }

  protected void process(List<String> data) {
    for (String s : data)
      StatusMessenger.instance.message("Bestand hernoemen: " + s);
  }
}
