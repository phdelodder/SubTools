package org.lodder.subtools.sublibrary.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class VideoFile extends Video{

    private List<Subtitle> matchingSubs;
    private List<Subtitle> filteredSubs;
    private File path;
    private String extension;
    private String filename;
    private String quality;
    private String description;
    private String team;

    public VideoFile(VideoType videoType) {
        this.setVideoType(videoType);
        extension = "";
        matchingSubs = new ArrayList<Subtitle>();
        filteredSubs = new ArrayList<Subtitle>();
        filename = "";
        path = new File("");
        quality = "";
        description = "";
        team = "";
    }

    public VideoFile(VideoType videoFileType, File file, String extension, String description, String team) {
        this(videoFileType);
        this.extension = extension;
        filename = file.getName();
        path = file.getParentFile();
        this.setDescription(description);
        this.setTeam(team);
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

    public List<Subtitle> getFilteredSubs() {
        return filteredSubs;
    }

    public void setMatchingSubs(List<Subtitle> matchingSubs) {
        this.matchingSubs = matchingSubs;
    }

    public void setFilteredSubs(List<Subtitle> filteredSubs) {
        this.filteredSubs = filteredSubs;
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

}
