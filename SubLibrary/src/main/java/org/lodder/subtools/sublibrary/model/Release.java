package org.lodder.subtools.sublibrary.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import lombok.Getter;

@Getter
public abstract class Release extends Video {

    private final Set<Subtitle> matchingSubs = new HashSet<>();
    private final File file;
    private final String quality;
    private final String description;
    private final String releaseGroup;

    public void addMatchingSub(Subtitle sub) {
        matchingSubs.add(sub);
    }

    public List<Subtitle> getMatchingSubs() {
        return new ArrayList<>(matchingSubs);
    }

    protected Release(VideoType videoFileType, File file, String description, String releaseGroup, String quality) {
        super(videoFileType);
        this.file = file;
        this.description = description;
        this.releaseGroup = releaseGroup;
        this.quality = quality;
    }

    public String getFileName() {
        return file != null ? file.getName() : null;
    }

    public File getPath() {
        return file != null ? file.getParentFile() : null;
    }

    public String getExtension() {
        return file != null ? FilenameUtils.getExtension(file.getName()) : null;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.getFileName() + " " + this.getQuality();
    }

    public String getReleaseDescription() {
        return file.getName();
    }
}
