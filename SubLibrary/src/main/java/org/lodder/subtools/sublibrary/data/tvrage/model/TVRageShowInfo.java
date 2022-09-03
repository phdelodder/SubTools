package org.lodder.subtools.sublibrary.data.tvrage.model;

/*
 *      Copyright (c) 2004-2013 Stuart Boston
 *
 *      This file is part of the TVRage API.
 *
 *      TVRage API is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      any later version.
 *
 *      TVRage API is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with TVRage API.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

import static org.lodder.subtools.sublibrary.data.tvrage.TVRageApi.isValidString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.math.NumberUtils;
import org.lodder.subtools.sublibrary.data.tvrage.TVRageApi;
import org.lodder.subtools.sublibrary.data.tvrage.TVRageParser;

/**
 * Full information about the show
 *
 * @author Stuart.Boston
 *
 */
public class TVRageShowInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String airDay = TVRageApi.UNKNOWN;
    private String airTime = TVRageApi.UNKNOWN;
    private List<TVRageCountryDetail> akas = new ArrayList<>();
    private String classification = TVRageApi.UNKNOWN;
    private String country = TVRageApi.UNKNOWN;
    private String ended = TVRageApi.UNKNOWN;
    private List<String> genres = new ArrayList<>();
    private List<TVRageCountryDetail> network = new ArrayList<>();
    private String originCountry = TVRageApi.UNKNOWN;
    private int runtime = 0;
    private int showID = 0;
    private String showLink = TVRageApi.UNKNOWN;
    private String showName = TVRageApi.UNKNOWN;
    private Date startDate = null;
    private int started = 0;
    private String status = TVRageApi.UNKNOWN;
    private String summary = TVRageApi.UNKNOWN;
    private String timezone = TVRageApi.UNKNOWN;
    private int totalSeasons = 0;

    public boolean isValid() {
        return showID > 0;
    }

    /**
     * Add a single AKA to the list
     *
     * @param newAka
     */
    public void addAka(TVRageCountryDetail newAka) {
        if (newAka.isValid()) {
            this.akas.add(newAka);
        }
    }

    /**
     * Add single AKA from a country/aka pairing
     *
     * @param country
     * @param aka
     */
    public void addAka(String country, String aka) {
        if (!isValidString(country) || !isValidString(aka)) {
            return;
        }

        this.akas.add(new TVRageCountryDetail(country, aka));
    }

    /**
     * Add a single Genre to the list
     *
     * @param genre
     */
    public void addGenre(String genre) {
        if (isValidString(genre)) {
            this.genres.add(genre);
        }
    }

    /**
     * Add a single network to the list
     *
     * @param newNetwork
     */
    public void addNetwork(TVRageCountryDetail newNetwork) {
        if (newNetwork.isValid()) {
            this.network.add(newNetwork);
        }
    }

    /**
     * Add a single network to the list
     *
     * @param country
     * @param networkName
     */
    public void addNetwork(String country, String networkName) {
        if (!isValidString(country) || !isValidString(networkName)) {
            return;
        }

        this.network.add(new TVRageCountryDetail(country, networkName));
    }

    public String getAirDay() {
        return airDay;
    }

    public String getAirTime() {
        return airTime;
    }

    public List<TVRageCountryDetail> getAkas() {
        return akas;
    }

    public String getClassification() {
        return classification;
    }

    public String getCountry() {
        return country;
    }

    public String getEnded() {
        return ended;
    }

    public List<String> getGenres() {
        return genres;
    }

    public List<TVRageCountryDetail> getNetwork() {
        return network;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public int getRuntime() {
        return runtime;
    }

    public int getShowID() {
        return showID;
    }

    public String getShowLink() {
        return showLink;
    }

    public String getShowName() {
        return showName;
    }

    public Date getStartDate() {
        return (Date) startDate.clone();
    }

    public int getStarted() {
        return started;
    }

    public String getStatus() {
        return status;
    }

    public String getSummary() {
        return summary;
    }

    public String getTimezone() {
        return timezone;
    }

    public int getTotalSeasons() {
        return totalSeasons;
    }

    public void setAirDay(String airDay) {
        this.airDay = isValidString(airDay) ? airDay : TVRageApi.UNKNOWN;
    }

    public void setAirTime(String airTime) {
        this.airTime = isValidString(airTime) ? airTime : TVRageApi.UNKNOWN;
    }

    public void setAkas(List<TVRageCountryDetail> akas) {
        this.akas = akas;
    }

    public void setClassification(String classification) {
        this.classification = isValidString(classification) ? classification : TVRageApi.UNKNOWN;
    }

    public void setCountry(String country) {
        this.country = isValidString(country) ? country : TVRageApi.UNKNOWN;
    }

    public void setEnded(String ended) {
        this.ended = isValidString(ended) ? ended : TVRageApi.UNKNOWN;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public void setNetwork(List<TVRageCountryDetail> network) {
        this.network = network;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = isValidString(originCountry) ? originCountry : TVRageApi.UNKNOWN;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = NumberUtils.toInt(runtime, 0);
    }

    public void setShowID(int showID) {
        this.showID = showID;
    }

    public void setShowID(String showID) {
        this.showID = NumberUtils.toInt(showID, 0);
    }

    public void setShowLink(String showLink) {
        this.showLink = isValidString(showLink) ? showLink : TVRageApi.UNKNOWN;
    }

    public void setShowName(String showName) {
        this.showName = isValidString(showName) ? showName : TVRageApi.UNKNOWN;
    }

    public void setStartDate(Date startDate) {
        this.startDate = (Date) startDate.clone();
    }

    public void setStartDate(String startDate) {
        if (isValidString(startDate)) {
            try {
                this.startDate = TVRageParser.parseDate(startDate);
            } catch (Exception ex) {
                // We can't do anything about this error, so return
                this.startDate = null;
            }
        }
    }

    public void setStarted(int started) {
        this.started = started;
    }

    public void setStarted(String started) {
        this.started = NumberUtils.toInt(started, 0);
    }

    public void setStatus(String status) {
        this.status = isValidString(status) ? status : TVRageApi.UNKNOWN;
    }

    public void setSummary(String summary) {
        this.summary = isValidString(summary) ? summary : TVRageApi.UNKNOWN;
    }

    public void setTimezone(String timezone) {
        this.timezone = isValidString(timezone) ? timezone : TVRageApi.UNKNOWN;
    }

    public void setTotalSeasons(int totalSeasons) {
        this.totalSeasons = totalSeasons;
    }

    public void setTotalSeasons(String totalSeasons) {
        this.totalSeasons = NumberUtils.toInt(totalSeasons, 0);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
