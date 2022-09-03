package org.lodder.subtools.sublibrary.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        extension = "";
        matchingSubs = new ArrayList<>();
        filename = "";
        path = new File("");
        quality = "";
        description = "";
        releasegroup = "";
    }

    public Release(VideoType videoFileType, File file, String extension, String description, String releasegroup) {
        this(videoFileType);
        this.extension = extension;
        filename = file.getName();
        path = file.getParentFile();
        this.setDescription(description);
        this.setReleasegroup(releasegroup);
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public List<Subtitle> getMatchingSubs() {
        return matchingSubs;
    }

    public void setMatchingSubs(List<Subtitle> matchingSubs) {
        this.matchingSubs = matchingSubs;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public File getPath() {
        return path;
    }

    public void setPath(File path) {
        this.path = path;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the Releasegroup
     */
    public String getReleasegroup() {
        return releasegroup;
    }

    /**
     * @param Releasegroup the Releasegroup to set
     */
    public void setReleasegroup(String releasegroup) {
        this.releasegroup = releasegroup;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.getFilename() + " " + this.getQuality();
    }
}
