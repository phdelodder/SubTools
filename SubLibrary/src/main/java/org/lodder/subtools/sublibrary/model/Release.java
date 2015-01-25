package org.lodder.subtools.sublibrary.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Release extends Video{

    private List<Subtitle> matchingSubs;
    private File path;
    private String extension;
    private String filename;
    private String quality;
    private String description;
    private String releasegroup;
    private List<Subtitle> subtitles;
    private List <String> tags;
    private int maxScore;

    public Release(VideoType videoType) {
        this.setVideoType(videoType);
        extension = "";
        matchingSubs = new ArrayList<Subtitle>();
        filename = "";
        path = new File("");
        quality = "";
        description = "";
        releasegroup = "";
        subtitles = new ArrayList<Subtitle>();
        tags = new ArrayList<String>();
    }

    public Release(VideoType videoFileType, File file, String extension, String description, String releasegroup) {
        this(videoFileType);
        this.extension = extension;
        filename = file.getName();
        path = file.getParentFile();
        this.setDescription(description);
        this.setReleasegroup(releasegroup);
        subtitles = new ArrayList<Subtitle>();
        tags = new ArrayList<String>();
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public List<Subtitle> getMatchingSubs() {
        return matchingSubs;
    }

    public void setMatchingSubs(List<Subtitle> matchingSubs) {
        this.matchingSubs = matchingSubs;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public File getPath() {
        return path;
    }

    public void setPath(File path) {
        this.path = path;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
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

  public List<Subtitle> getSubtitles() {
    return subtitles;
  }

  public void setSubtitles(List<Subtitle> subtitles) {
    this.subtitles = subtitles;
  }

  public List <String> getTags() {
    return tags;
  }

  public void setTags(List <String> tags) {
    this.tags = tags;
  }

  public int getMaxScore() {
    return maxScore;
  }

  public void setMaxScore(int maxScore) {
    this.maxScore = maxScore;
  }

}
