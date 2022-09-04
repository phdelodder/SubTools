package org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.model;

public class Addic7edSubtitleDescriptor {

    private String Version;
    private String Language;
    private String Url;
    private String Titel;
    private String Uploader;
    private boolean hearingImpaired;

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getTitel() {
        return Titel;
    }

    public void setTitel(String titel) {
        Titel = titel;
    }

    public String getUploader() {
        return Uploader;
    }

    public void setUploader(String uploader) {
        Uploader = uploader;
    }

    public boolean isHearingImpaired() {
        return hearingImpaired;
    }

    public void setHearingImpaired(boolean hearingImpaired) {
        this.hearingImpaired = hearingImpaired;
    }

}
