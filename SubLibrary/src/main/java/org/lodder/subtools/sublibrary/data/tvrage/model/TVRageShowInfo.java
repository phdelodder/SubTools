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
import static org.lodder.subtools.sublibrary.data.tvrage.TVRageApi.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.math.NumberUtils;
import org.lodder.subtools.sublibrary.data.tvrage.TVRageApi;
import org.lodder.subtools.sublibrary.data.tvrage.TVRageParser;

import lombok.Getter;

/**
 * Full information about the show
 *
 * @author Stuart.Boston
 *
 */
@Getter
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
    private int showId = 0;
    private String showLink = TVRageApi.UNKNOWN;
    private String showName = TVRageApi.UNKNOWN;
    private LocalDate startDate = null;
    private int started = 0;
    private String status = TVRageApi.UNKNOWN;
    private String summary = TVRageApi.UNKNOWN;
    private String timezone = TVRageApi.UNKNOWN;
    private int totalSeasons = 0;

    public boolean isValid() {
        return showId > 0;
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

    public void setShowId(int showID) {
        this.showId = showID;
    }

    public void setShowId(String showID) {
        this.showId = NumberUtils.toInt(showID, 0);
    }

    public void setShowLink(String showLink) {
        this.showLink = isValidString(showLink) ? showLink : TVRageApi.UNKNOWN;
    }

    public void setShowName(String showName) {
        this.showName = isValidString(showName) ? showName : TVRageApi.UNKNOWN;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setStartDate(String startDate) {
        if (isValidString(startDate)) {
            try {
                this.startDate = TVRageParser.parseDate(startDate);
            } catch (DateTimeParseException ex) {
                // We can't do anything about this error, so return
                this.startDate = null;
            }
        } else {
            this.startDate = null;
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
