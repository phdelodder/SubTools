package org.lodder.subtools.multisubdownloader.lib.control;

import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.exception.VideoControlException;
import org.lodder.subtools.sublibrary.model.VideoFile;
import org.lodder.subtools.sublibrary.settings.model.MappingTvdbScene;

public abstract class VideoFileControl {

  protected VideoFile videoFile;
  protected SubtitleControl sc;
  protected Settings settings;

  public VideoFileControl(VideoFile videoFile, Settings settings) {
    this.settings = settings;
    this.sc = new SubtitleControl(settings);
    this.videoFile = videoFile;
  }

  abstract void process(List<MappingTvdbScene> dict) throws VideoControlException;

  abstract void processWithSubtitles(List<MappingTvdbScene> dict, String languageCode)
      throws VideoControlException;

  public void process() throws VideoControlException {
    this.process(new ArrayList<MappingTvdbScene>());
  }

  public void processWithSubtitles(String languageCode) throws VideoControlException {
    this.processWithSubtitles(new ArrayList<MappingTvdbScene>(), languageCode);
  }

  public void setVideoFile(VideoFile videoFile) {
    this.videoFile = videoFile;
  }

  public VideoFile getVideoFile() {
    return videoFile;
  }
}
