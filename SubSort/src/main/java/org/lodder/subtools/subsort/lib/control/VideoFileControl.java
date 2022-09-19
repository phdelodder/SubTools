package org.lodder.subtools.subsort.lib.control;

import org.lodder.subtools.sublibrary.model.Release;

public abstract class VideoFileControl {

    protected Release release;

    public VideoFileControl(Release release) {
        this.release = release;
    }

    public void setVideoFile(Release release) {
        this.release = release;
    }

    public Release getVideoFile() {
        return release;
    }
}
