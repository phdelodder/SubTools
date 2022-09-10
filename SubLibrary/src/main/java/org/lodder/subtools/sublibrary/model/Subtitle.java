package org.lodder.subtools.sublibrary.model;

import java.io.File;

import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.ManagerException;

import com.pivovarit.function.ThrowingSupplier;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Subtitle {

    private final ThrowingSupplier<String, ManagerException> urlSupplier;
    private final String url;
    private final File file;
    private final SourceLocation sourceLocation;

    private String fileName;
    private Language language;
    private String releaseGroup;
    private String uploader;
    private SubtitleMatchType subtitleMatchType;
    private SubtitleSource subtitleSource;
    private boolean hearingImpaired;

    private String quality;
    private int score;

    @AllArgsConstructor
    @Getter
    public enum SubtitleSource {
        OPENSUBTITLES("OpenSubtitles"),
        PODNAPISI("Podnapisi"),
        ADDIC7ED("Addic7ed"),
        TVSUBTITLES("TvSubtitles"),
        LOCAL("Local"),
        SUBSMAX("SubsMax"),
        SUBSCENE("Subscene");

        private final String name;
    }

    public enum SourceLocation {
        URL, URL_SUPPLIER, FILE;
    }

    private Subtitle(ThrowingSupplier<String, ManagerException> urlSupplier) {
        this.urlSupplier = urlSupplier;
        this.url = null;
        this.file = null;
        this.sourceLocation = SourceLocation.URL_SUPPLIER;
    }

    private Subtitle(String url) {
        this.urlSupplier = null;
        this.url = url;
        this.file = null;
        this.sourceLocation = SourceLocation.URL;
    }

    private Subtitle(File file) {
        this.urlSupplier = null;
        this.url = null;
        this.file = file;
        this.sourceLocation = SourceLocation.FILE;
    }

    public static Subtitle downloadSource(ThrowingSupplier<String, ManagerException> urlSupplier) {
        return new Subtitle(urlSupplier);
    }

    public static Subtitle downloadSource(String url) {
        return new Subtitle(url);
    }

    public static Subtitle downloadSource(File file) {
        return new Subtitle(file);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.getFileName() + " " + this.getQuality();
    }

    public Subtitle fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public Subtitle language(Language language) {
        this.language = language;
        return this;
    }

    public Subtitle releaseGroup(String releaseGroup) {
        this.releaseGroup = releaseGroup;
        return this;
    }

    public Subtitle uploader(String uploader) {
        this.uploader = uploader;
        return this;
    }

    public Subtitle subtitleMatchType(SubtitleMatchType subtitleMatchType) {
        this.subtitleMatchType = subtitleMatchType;
        return this;
    }

    public Subtitle subtitleSource(SubtitleSource subtitleSource) {
        this.subtitleSource = subtitleSource;
        return this;
    }

    public Subtitle hearingImpaired(boolean hearingImpaired) {
        this.hearingImpaired = hearingImpaired;
        return this;
    }

    public Subtitle quality(String quality) {
        this.quality = quality;
        return this;
    }

    public Subtitle score(int score) {
        this.score = score;
        return this;
    }

}
