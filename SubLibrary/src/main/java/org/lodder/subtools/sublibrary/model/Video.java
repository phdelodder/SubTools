package org.lodder.subtools.sublibrary.model;

public abstract class Video {

    private VideoType videoType;

    public Video() {

    }

    public Video(VideoType videoType) {
        this.setVideoType(videoType);
    }

    public void setVideoType(VideoType videoType) {
        this.videoType = videoType;
    }

    public VideoType getVideoType() {
        return videoType;
    }

}
