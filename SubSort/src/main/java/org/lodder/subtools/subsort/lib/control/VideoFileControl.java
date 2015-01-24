package org.lodder.subtools.subsort.lib.control;

import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.sublibrary.exception.VideoControlException;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.settings.model.MappingTvdbScene;

public abstract class VideoFileControl {

    protected Release release;

    public VideoFileControl(Release release) {
        this.release = release;
    }

    abstract Release process(List<MappingTvdbScene> dict) throws VideoControlException;

    public Release process() throws VideoControlException {
        return this.process(new ArrayList<MappingTvdbScene>());
    }
    
    public void setVideoFile(Release release) {
        this.release = release;
    }
    
    public Release getVideoFile() {
        return release;
    }
}
