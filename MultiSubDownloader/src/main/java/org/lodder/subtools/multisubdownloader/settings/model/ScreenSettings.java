package org.lodder.subtools.multisubdownloader.settings.model;

public class ScreenSettings {
    
    private boolean hideEpisode, hideSeason, hideTitle;
    private boolean hideWIP, hideType, hideFilename;
    
    public ScreenSettings(){
        hideEpisode = false;
        hideSeason = false;
        hideTitle = false;
        hideWIP = false;
        hideType = false;
        hideFilename = false;
    }

    /**
     * @return the hideEpisode
     */
    public boolean isHideEpisode() {
        return hideEpisode;
    }

    /**
     * @param hideEpisode the hideEpisode to set
     */
    public void setHideEpisode(boolean hideEpisode) {
        this.hideEpisode = hideEpisode;
    }

    /**
     * @return the hideSeason
     */
    public boolean isHideSeason() {
        return hideSeason;
    }

    /**
     * @param hideSeason the hideSeason to set
     */
    public void setHideSeason(boolean hideSeason) {
        this.hideSeason = hideSeason;
    }

    /**
     * @return the hideTitle
     */
    public boolean isHideTitle() {
        return hideTitle;
    }

    /**
     * @param hideTitle the hideTitle to set
     */
    public void setHideTitle(boolean hideTitle) {
        this.hideTitle = hideTitle;
    }

    /**
     * @return the hideWIP
     */
    public boolean isHideWIP() {
        return hideWIP;
    }

    /**
     * @param hideWIP the hideWIP to set
     */
    public void setHideWIP(boolean hideWIP) {
        this.hideWIP = hideWIP;
    }

    /**
     * @return the hideType
     */
    public boolean isHideType() {
        return hideType;
    }

    /**
     * @param hideType the hideType to set
     */
    public void setHideType(boolean hideType) {
        this.hideType = hideType;
    }

    /**
     * @return the hideFilename
     */
    public boolean isHideFilename() {
        return hideFilename;
    }

    /**
     * @param hideFilename the hideFilename to set
     */
    public void setHideFilename(boolean hideFilename) {
        this.hideFilename = hideFilename;
    }
}
