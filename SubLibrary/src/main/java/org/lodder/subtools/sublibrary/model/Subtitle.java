package org.lodder.subtools.sublibrary.model;

import java.io.Serializable;
import java.nio.file.Path;

import com.pivovarit.function.ThrowingSupplier;
import lombok.EqualsAndHashCode;
import manifold.ext.props.rt.api.val;
import manifold.ext.props.rt.api.var;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.exception.SubtitlesProviderException;

@EqualsAndHashCode
public class Subtitle implements Serializable {

    @val DownloadSource downloadSource;
    @var @Nullable String fileName;
    @var @Nullable Language language;
    @var @Nullable String releaseGroup;
    @var @Nullable String uploader;
    @var @Nullable SubtitleMatchType subtitleMatchType;
    @var @Nullable SubtitleSource subtitleSource;
    @var boolean hearingImpaired;
    @var @Nullable String quality;
    @var int score;

    public Subtitle(DownloadSource downloadSource,
        @Nullable String fileName=null,
        @Nullable Language language=null,
        @Nullable String releaseGroup=null,
        @Nullable String uploader=null,
        @Nullable SubtitleMatchType subtitleMatchType=null,
        @Nullable SubtitleSource subtitleSource=null,
        boolean hearingImpaired=false,
        @Nullable String quality=null,
        int score=0) {
        this.downloadSource = downloadSource;
        this.fileName = fileName;
        this.language = language;
        this.releaseGroup = releaseGroup;
        this.uploader = uploader;
        this.subtitleMatchType = subtitleMatchType;
        this.subtitleSource = subtitleSource;
        this.hearingImpaired = hearingImpaired;
        this.quality = quality;
        this.score = score;
    }

    @EqualsAndHashCode
    public static class DownloadSource {
        @val SourceLocation sourceLocation;
        @EqualsExclude @val @Nullable ThrowingSupplier<String, ? extends SubtitlesProviderException> urlSupplier;
        @val @Nullable String url;
        @val @Nullable Path file;

        private DownloadSource(
            SourceLocation sourceLocation,
            @Nullable ThrowingSupplier<String, ? extends SubtitlesProviderException> urlSupplier=null,
            @Nullable String url=null,
            @Nullable Path file=null) {

            this.urlSupplier = urlSupplier;
            this.url = url;
            this.file = file;
            this.sourceLocation = sourceLocation;
        }

        public static DownloadSource of(
            ThrowingSupplier<String, ? extends SubtitlesProviderException> urlSupplier) {
            return new DownloadSource(SourceLocation.URL_SUPPLIER, urlSupplier:urlSupplier);
        }

        public static DownloadSource of(String url) {
            return new DownloadSource(SourceLocation.URL, url:url);
        }

        public static DownloadSource of(Path file) {
            return new DownloadSource(SourceLocation.FILE, file:file);
        }
        
        @SuppressWarnings("ConstantConditions")
        public String getValue() throws SubtitlesProviderException {
            return switch (sourceLocation) {
                case FILE -> file.toString();
                case URL -> url;
                case URL_SUPPLIER -> urlSupplier.get();
            };
        }

        @Override public String toString() {
            return "DownloadSource: " + sourceLocation + " " + switch (sourceLocation) {
                case FILE -> file;
                case URL -> url;
                case URL_SUPPLIER -> "";
            };
        }
    }

    public enum SourceLocation {
        URL, URL_SUPPLIER, FILE
    }

    @Override
    public String toString() {
        return "${getClass().getSimpleName()}: $fileName $quality";
    }
}
