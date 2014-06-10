package org.lodder.subtools.sublibrary.subtitlesource.podnapisi.model;


/**
 * Created by IntelliJ IDEA.
 * User: lodder
 * Date: 20/08/11
 * Time: 13:44
 * To change this template use File | Settings | File Templates.
 */
public class PodnapisiSubtitleDescriptor {

    private String subtitleId;
    private String languageCode;
    private String uploaderName;
    private String uploaderUid;
    private String matchRanking;
    private String releaseString;
    private String flagsString;
    private String subtitleRating;
    private boolean isInexact;

    public String getSubtitleId() {
        return subtitleId;
    }

    public void setSubtitleId(String subtitleId) {
        this.subtitleId = subtitleId;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public String getUploaderUid() {
        return uploaderUid;
    }

    public void setUploaderUid(String uploaderUid) {
        this.uploaderUid = uploaderUid;
    }

    public String getMatchRanking() {
        return matchRanking;
    }

    public void setMatchRanking(String matchRanking) {
        this.matchRanking = matchRanking;
    }

    public String getReleaseString() {
        return releaseString;
    }

    public void setReleaseString(String releaseString) {
        this.releaseString = releaseString;
    }

    public String getFlagsString() {
        return flagsString;
    }

    public void setFlagsString(String flagsString) {
        this.flagsString = flagsString;
    }

    public String getSubtitleRating() {
        return subtitleRating;
    }

    public void setSubtitleRating(String subtitleRating) {
        this.subtitleRating = subtitleRating;
    }

    public boolean isInexact() {
        return isInexact;
    }

    public void setInexact(boolean inexact) {
        isInexact = inexact;
    }

}
