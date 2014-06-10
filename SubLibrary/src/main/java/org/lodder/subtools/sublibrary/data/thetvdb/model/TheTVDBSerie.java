package org.lodder.subtools.sublibrary.data.thetvdb.model;

import java.util.ArrayList;
import java.util.List;

public class TheTVDBSerie {
	private String id;
    private String serieId;
    private String language;
    private String serieName;
    private String banner;
    private String overview;
    private String firstAired;
    private String imdbId;
    private String zap2ItId;
    private List<String> actors = new ArrayList<String>();
    private String airsDayOfWeek;
    private String airsTime;
    private String contentRating;
    private List<String> genres = new ArrayList<String>();
    private String network;
    private String rating;
    private String runtime;
    private String status;
    private String fanart;
    private String lastUpdated;
    private String poster;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSerieId() {
		return serieId;
	}
	public void setSerieId(String serieId) {
		this.serieId = serieId;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getSerieName() {
		return serieName;
	}
	public void setSerieName(String serieName) {
		this.serieName = serieName;
	}
	public String getBanner() {
		return banner;
	}
	public void setBanner(String banner) {
		this.banner = banner;
	}
	public String getOverview() {
		return overview;
	}
	public void setOverview(String overview) {
		this.overview = overview;
	}
	public String getFirstAired() {
		return firstAired;
	}
	public void setFirstAired(String firstAired) {
		this.firstAired = firstAired;
	}
	public String getImdbId() {
		return imdbId;
	}
	public void setImdbId(String imdbId) {
		this.imdbId = imdbId;
	}
	public String getZap2ItId() {
		return zap2ItId;
	}
	public void setZap2ItId(String zap2ItId) {
		this.zap2ItId = zap2ItId;
	}
	public List<String> getActors() {
		return actors;
	}
	public void setActors(List<String> actors) {
		this.actors = actors;
	}
	public String getAirsDayOfWeek() {
		return airsDayOfWeek;
	}
	public void setAirsDayOfWeek(String airsDayOfWeek) {
		this.airsDayOfWeek = airsDayOfWeek;
	}
	public String getAirsTime() {
		return airsTime;
	}
	public void setAirsTime(String airsTime) {
		this.airsTime = airsTime;
	}
	public String getContentRating() {
		return contentRating;
	}
	public void setContentRating(String contentRating) {
		this.contentRating = contentRating;
	}
	public List<String> getGenres() {
		return genres;
	}
	public void setGenres(List<String> genres) {
		this.genres = genres;
	}
	public String getNetwork() {
		return network;
	}
	public void setNetwork(String network) {
		this.network = network;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getRuntime() {
		return runtime;
	}
	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFanart() {
		return fanart;
	}
	public void setFanart(String fanart) {
		this.fanart = fanart;
	}
	public String getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public String getPoster() {
		return poster;
	}
	public void setPoster(String poster) {
		this.poster = poster;
	}    


}
