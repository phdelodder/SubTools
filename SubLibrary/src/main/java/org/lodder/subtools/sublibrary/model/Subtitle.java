package org.lodder.subtools.sublibrary.model;

import java.io.File;
import org.lodder.subtools.sublibrary.ManagerException;
import com.pivovarit.function.ThrowingSupplier;

public class Subtitle {

    private String filename, languagecode, quality, releasegroup, uploader;
    private SubtitleMatchType subtitleMatchType;
    private final SubtitleSource subtitleSource;
    private boolean hearingImpaired;
    private int score;
    private final ThrowingSupplier<String, ManagerException> urlSupplier;
    private final String url;
    private final File file;
    private final SourceLocation sourceLcation;

    public enum SubtitleSource {
        OPENSUBTITLES, PODNAPISI, ADDIC7ED, TVSUBTITLES, LOCAL, SUBSMAX
    }

    public enum SourceLocation {
        URL, URL_SUPPLIER, FILE;
    }

    public Subtitle(SubtitleSource subtitleSource, String filename, ThrowingSupplier<String, ManagerException> urlSupplier, String languagecode,
            String quality, SubtitleMatchType subtitleMatchType, String releasegroup, String uploader, boolean hearingImpaired) {
        this.subtitleSource = subtitleSource;
        this.filename = filename;
        this.urlSupplier = urlSupplier;
        this.url = null;
        this.file = null;
        this.languagecode = languagecode;
        this.quality = quality;
        this.subtitleMatchType = subtitleMatchType;
        this.releasegroup = releasegroup;
        this.uploader = uploader;
        this.hearingImpaired = hearingImpaired;
        this.sourceLcation = SourceLocation.URL_SUPPLIER;
    }

    public Subtitle(SubtitleSource subtitleSource, String filename, String url, String languagecode, String quality,
            SubtitleMatchType subtitleMatchType, String releasegroup, String uploader, boolean hearingImpaired) {
        this.subtitleSource = subtitleSource;
        this.filename = filename;
        this.urlSupplier = null;
        this.url = url;
        this.file = null;
        this.languagecode = languagecode;
        this.quality = quality;
        this.subtitleMatchType = subtitleMatchType;
        this.releasegroup = releasegroup;
        this.uploader = uploader;
        this.hearingImpaired = hearingImpaired;
        this.sourceLcation = SourceLocation.URL;
    }

    public Subtitle(SubtitleSource subtitleSource, String filename, File file, String languagecode, String quality,
            SubtitleMatchType subtitleMatchType, String releasegroup, String uploader, boolean hearingImpaired) {
        this.subtitleSource = subtitleSource;
        this.filename = filename;
        this.urlSupplier = null;
        this.url = null;
        this.file = file;
        this.languagecode = languagecode;
        this.quality = quality;
        this.subtitleMatchType = subtitleMatchType;
        this.releasegroup = releasegroup;
        this.uploader = uploader;
        this.hearingImpaired = hearingImpaired;
        this.sourceLcation = SourceLocation.FILE;
    }

    public String getUrl() {
        return url;
    }

    public ThrowingSupplier<String, ManagerException> getUrlSupplier() {
        return urlSupplier;
    }

    public File getFile() {
        return file;
    }

    public SourceLocation getSourceLcation() {
        return sourceLcation;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getLanguagecode() {
        return languagecode;
    }

    public void setLanguagecode(String languagecode) {
        this.languagecode = languagecode;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getQuality() {
        return quality;
    }

    public void setSubtitleMatchType(SubtitleMatchType subtitleMatchType) {
        this.subtitleMatchType = subtitleMatchType;
    }

    public SubtitleMatchType getSubtitleMatchType() {
        return subtitleMatchType;
    }

    public SubtitleSource getSubtitleSource() {
        return subtitleSource;
    }

    /**
     * @return the releasegroup
     */
    public String getReleasegroup() {
        return releasegroup;
    }

    /**
     * @param releasegroup the releasegroup to set
     */
    public void setReleasegroup(String releasegroup) {
        this.releasegroup = releasegroup;
    }

    /**
     * @return the uploader
     */
    public String getUploader() {
        return uploader;
    }

    /**
     * @param uploader the uploader to set
     */
    public void setUploader(String uploader) {
        if (uploader == null) {
            uploader = "";
        }
        this.uploader = uploader;
    }

    /**
     * @return the hearingImpaired
     */
    public boolean isHearingImpaired() {
        return hearingImpaired;
    }

    /**
     * @param hearingImpaired the hearingImpaired to set
     */
    public void setHearingImpaired(boolean hearingImpaired) {
        this.hearingImpaired = hearingImpaired;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.getFilename() + " " + this.getQuality();
    }

}
