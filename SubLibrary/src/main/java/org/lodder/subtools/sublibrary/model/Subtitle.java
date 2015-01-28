package org.lodder.subtools.sublibrary.model;

public class Subtitle {

    private String filename, downloadlink, languagecode, quality, releasegroup, uploader;
    private SubtitleMatchType subtitleMatchType;
    private SubtitleSource subtitleSource;
    private boolean hearingImpaired;
    private int score;

    public enum SubtitleSource {
        OPENSUBTITLES, PODNAPISI, ADDIC7ED, TVSUBTITLES, LOCAL, SUBSMAX
    }

    public Subtitle(SubtitleSource subtitleSource, String filename, String downloadlink, String languagecode, String quality, SubtitleMatchType subtitleMatchType, String releasegroup, String uploader, boolean hearingImp) {
        this.subtitleSource = subtitleSource;
        this.filename = filename;
        this.downloadlink = downloadlink;
        this.languagecode = languagecode;
        this.quality = quality;
        this.subtitleMatchType = subtitleMatchType;
        this.setReleasegroup(releasegroup);
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

}
