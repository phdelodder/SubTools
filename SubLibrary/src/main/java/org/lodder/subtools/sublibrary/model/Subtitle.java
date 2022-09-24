package org.lodder.subtools.sublibrary.model;

import java.io.File;

import org.apache.commons.lang3.builder.EqualsExclude;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.exception.SubtitlesProviderException;

import com.pivovarit.function.ThrowingSupplier;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Subtitle {
    @EqualsExclude
    private final ThrowingSupplier<String, ? extends SubtitlesProviderException> urlSupplier;
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

    public enum SourceLocation {
        URL, URL_SUPPLIER, FILE;
    }

    private Subtitle(ThrowingSupplier<String, ? extends SubtitlesProviderException> urlSupplier) {
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

    public static Subtitle downloadSource(ThrowingSupplier<String, ? extends SubtitlesProviderException> urlSupplier) {
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
