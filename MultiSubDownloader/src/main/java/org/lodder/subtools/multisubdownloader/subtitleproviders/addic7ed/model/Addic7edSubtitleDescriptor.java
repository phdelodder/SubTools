package org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.model;

public class Addic7edSubtitleDescriptor {

    private String Version;
    private String Language;
    private String Url;
    private String Titel;
    private String Uploader;
    private boolean hearingImpaired;

    /**
     * @return the version
     */
    public String getVersion() {
        return Version;
    }

    /**
     * @param version
     *        the version to set
     */
    public void setVersion(String version) {
        Version = version;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return Language;
    }

    /**
     * @param language
     *        the language to set
     */
    public void setLanguage(String language) {
        Language = language;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return Url;
    }

    /**
     * @param url
     *        the url to set
     */
    public void setUrl(String url) {
        Url = url;
    }

    /**
     * @return the titel
     */
    public String getTitel() {
        return Titel;
    }

    /**
     * @param titel
     *        the titel to set
     */
    public void setTitel(String titel) {
        Titel = titel;
    }

    /**
     * @return the uploader
     */
    public String getUploader() {
        return Uploader;
    }

    /**
     * @param uploader the uploader to set
     */
    public void setUploader(String uploader) {
        Uploader = uploader;
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

}
