package org.lodder.subtools.multisubdownloader.settings.model;

public class ScreenSettings {

    private boolean hideEpisode, hideSeason, hideTitle;
    private boolean hideWIP, hideType, hideFilename;

    public ScreenSettings() {
        hideEpisode = false;
        hideSeason = false;
        hideTitle = false;
        hideWIP = false;
        hideType = false;
        hideFilename = false;
    }

    public boolean isHideEpisode() {
        return hideEpisode;
    }

    public void setHideEpisode(boolean hideEpisode) {
        this.hideEpisode = hideEpisode;
    }

    public boolean isHideSeason() {
        return hideSeason;
    }

    public void setHideSeason(boolean hideSeason) {
        this.hideSeason = hideSeason;
    }

    public boolean isHideTitle() {
        return hideTitle;
    }

    public void setHideTitle(boolean hideTitle) {
        this.hideTitle = hideTitle;
    }

    public boolean isHideWIP() {
        return hideWIP;
    }

    public void setHideWIP(boolean hideWIP) {
        this.hideWIP = hideWIP;
    }

    public boolean isHideType() {
        return hideType;
    }

    public void setHideType(boolean hideType) {
        this.hideType = hideType;
    }

    public boolean isHideFilename() {
        return hideFilename;
    }

    public void setHideFilename(boolean hideFilename) {
        this.hideFilename = hideFilename;
    }
}
