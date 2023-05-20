package org.lodder.subtools.sublibrary.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

@Getter
public abstract class Release extends Video {

    private final Set<Subtitle> matchingSubs = new HashSet<>();
    private final Path path;
    private final String quality;
    private final String description;
    private final String releaseGroup;

    public void addMatchingSub(Subtitle sub) {
        matchingSubs.add(sub);
    }

    public List<Subtitle> getMatchingSubs() {
        return new ArrayList<>(matchingSubs);
    }

    public int getMatchingSubCount() {
        return matchingSubs.size();
    }

    protected Release(VideoType videoFileType, Path path, String description, String releaseGroup, String quality) {
        super(videoFileType);
        this.path = path;
        this.description = description;
        this.releaseGroup = releaseGroup;
        this.quality = quality;
    }

    public String getFileName() {
        return path != null ? path.getFileName().toString() : null;
    }

    public Path getPath() {
        return path != null ? path.getParent() : null;
    }

    public String getExtension() {
        return StringUtils.substringAfterLast(getFileName(), ".");
    }

    public boolean hasExtension(String extension) {
        return StringUtils.endsWith(getFileName(), "." + extension);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.getFileName() + " " + this.getQuality();
    }

    public String getReleaseDescription() {
        return getFileName();
    }
}
