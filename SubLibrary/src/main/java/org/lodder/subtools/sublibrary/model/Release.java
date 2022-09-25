package org.lodder.subtools.sublibrary.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import lombok.Getter;

@Getter
public abstract class Release extends Video {

    private final List<Subtitle> matchingSubs = new ArrayList<>();
    private final File file;
    private final String quality;
    private final String description;
    private final String releaseGroup;

    // public interface ReleaseBuilderVideoType {
    // ReleaseBuilderFile videoType(VideoType videoType);
    // }
    //
    // public interface ReleaseBuilderFile {
    // ReleaseBuilderOther file(File file);
    // }
    //
    // public interface ReleaseBuilderOther {
    // ReleaseBuilderOther quality(String quality);
    //
    // ReleaseBuilderOther description(String description);
    //
    // ReleaseBuilderOther releaseGroup(String releaseGroup);
    //
    // Release build();
    // }
    //
    // public interface ReleaseBuilderDescription {
    // }
    //
    // public interface ReleaseBuilderReleaseGroup {
    // }
    //
    // @Setter
    // @Accessors(chain = true, fluent = true)
    // public static class ReleaseBuilder implements ReleaseBuilderOther, ReleaseBuilderFile, ReleaseBuilderVideoType {
    // private VideoType videoType;
    // private File file;
    // private String quality;
    // private String description;
    // private String releaseGroup;
    //
    // @Override
    // public Release build() {
    // return new Release(videoType, file, description, releaseGroup, quality);
    // }
    // }

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

    public void addMatchingSubs(Subtitle subtitle) {
        matchingSubs.add(subtitle);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.getFileName() + " " + this.getQuality();
    }

    public String getReleaseDescription() {
        return file.getName();
    }
}
