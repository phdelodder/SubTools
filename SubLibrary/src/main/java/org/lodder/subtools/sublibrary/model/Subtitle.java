package org.lodder.subtools.sublibrary.model;

public class Subtitle {

    private String filename, downloadlink, languagecode, quality, team, uploader;
    private SubtitleMatchType subtitleMatchType;
    private SubtitleSource subtitleSource;
    private boolean hearingImpaired;

    public enum SubtitleSource {
        OPENSUBTITLES, PODNAPISI, ADDIC7ED, TVSUBTITLES, LOCAL, PRIVATEREPO, SUBSMAX
    }

    public Subtitle(SubtitleSource subtitleSource, String filename, String downloadlink, String languagecode, String quality, SubtitleMatchType subtitleMatchType, String team, String uploader, boolean hearingImp) {
        this.subtitleSource = subtitleSource;
        this.filename = filename;
        this.downloadlink = downloadlink;
        this.languagecode = languagecode;
        this.quality = quality;
        this.subtitleMatchType = subtitleMatchType;
        this.setTeam(team);
        this.setUploader(uploader);
        this.setHearingImpaired(hearingImp);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDownloadlink() {
        return downloadlink;
    }

    public void setDownloadlink(String downloadlink) {
        this.downloadlink = downloadlink;
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
	 * @return the team
	 */
	public String getTeam() {
		return team;
	}

	/**
	 * @param team the team to set
	 */
	public void setTeam(String team) {
		this.team = team;
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

}
