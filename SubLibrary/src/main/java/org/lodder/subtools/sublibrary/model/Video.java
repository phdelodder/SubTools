package org.lodder.subtools.sublibrary.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class Video {

    private final VideoType videoType;

    public Video(VideoType videoType) {
        this.videoType = videoType;
    }
}
