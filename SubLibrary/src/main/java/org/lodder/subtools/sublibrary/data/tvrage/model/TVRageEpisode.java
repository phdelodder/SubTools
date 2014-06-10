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
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.math.NumberUtils;
import org.lodder.subtools.sublibrary.data.tvrage.TVRageApi;
import org.lodder.subtools.sublibrary.data.tvrage.TVRageParser;

public class TVRageEpisode implements Serializable {

    /*
     * Serial Version
     */
    private static final long serialVersionUID = 1L;
    /*
     * Properties
     */
    private static final String UNKNOWN = TVRageApi.UNKNOWN;
    private TVRageEpisodeNumber episodeNumber = new TVRageEpisodeNumber(0, 0);
    private String productionId = UNKNOWN;
    private Date airDate = null;
    private String link = UNKNOWN;
    private String title = UNKNOWN;
    private String summary = UNKNOWN;
    private float rating = 0.0f;
    private String screenCap = UNKNOWN;

    public void setEpisodeNumber(TVRageEpisodeNumber episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public TVRageEpisodeNumber getEpisodeNumber() {
        return episodeNumber;
    }

    public boolean isValid() {
        return episodeNumber.isValid() && isValidString(title);
    }

    /**
     * Added as a convenience method
     *
     * @return
     */
    public int getEpisode() {
        return episodeNumber.getEpisode();
    }

    /**
     * Added as a convenience method
     *
     * @return
     */
    public int getSeason() {
        return episodeNumber.getSeason();
    }

    public String getProductionId() {
        return productionId;
    }

    public void setProductionId(String productionId) {
        if (isValidString(productionId)) {
            this.productionId = productionId.trim();
        } else {
            this.productionId = UNKNOWN;
        }
    }

    public Date getAirDate() {
        return (Date) airDate.clone();
    }

    public void setAirDate(Date airDate) {
        this.airDate = (Date) airDate.clone();
    }

    public void setAirDate(String airDate) {
        if (isValidString(airDate)) {
            try {
                this.airDate = TVRageParser.ParseDate(airDate);
            } catch (Exception ex) {
                this.airDate = null;
            }
        } else {
            this.airDate = null;
        }
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        if (isValidString(link)) {
            this.link = link.trim();
        } else {
            this.link = UNKNOWN;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (isValidString(title)) {
            this.title = title.trim();
        } else {
            this.title = UNKNOWN;
        }
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        if (isValidString(summary)) {
            this.summary = summary.trim();
        } else {
            this.summary = UNKNOWN;
        }
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setRating(String rating) {
        this.rating = NumberUtils.toFloat(rating, 0.0f);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getScreenCap() {
        return screenCap;
    }

    public void setScreenCap(String screenCap) {
        if (isValidString(screenCap)) {
            this.screenCap = screenCap.trim();
        } else {
            this.screenCap = UNKNOWN;
        }
    }
}