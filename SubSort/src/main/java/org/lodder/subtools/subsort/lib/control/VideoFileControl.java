package org.lodder.subtools.subsort.lib.control;

import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.sublibrary.exception.VideoControlException;
import org.lodder.subtools.sublibrary.model.VideoFile;
import org.lodder.subtools.sublibrary.settings.model.MappingTvdbScene;

public abstract class VideoFileControl {

    protected VideoFile videoFile;

    public VideoFileControl(VideoFile videoFile) {
        this.videoFile = videoFile;
    }

    abstract VideoFile process(List<MappingTvdbScene> dict) throws VideoControlException;

    public VideoFile process() throws VideoControlException {
        return this.process(new ArrayList<MappingTvdbScene>());
    }
    
    public void setVideoFile(VideoFile videoFile) {
        this.videoFile = videoFile;
    }
    
    public VideoFile getVideoFile() {
        return videoFile;
    }
}
