package org.lodder.subtools.sublibrary.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Release extends Video {

    private List<Subtitle> matchingSubs;
    private File path;
    private String extension;
    private String filename;
    private String quality;
    private String description;
    private String releasegroup;

    public Release(VideoType videoType) {
        this.setVideoType(videoType);
        this.extension = "";
        this.matchingSubs = new ArrayList<>();
        this.filename = "";
        this.path = new File("");
        this.quality = "";
        this.description = "";
        this.releasegroup = "";
    }

    public Release(VideoType videoFileType, File file, String extension, String description, String releasegroup) {
        this(videoFileType);
        this.extension = extension;
        this.filename = file.getName();
        this.path = file.getParentFile();
        this.description = description;
        this.releasegroup = releasegroup;
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.getFilename() + " " + this.getQuality();
    }
}
