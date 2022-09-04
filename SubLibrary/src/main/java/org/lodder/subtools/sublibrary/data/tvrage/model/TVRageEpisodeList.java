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
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.math.NumberUtils;
import org.lodder.subtools.sublibrary.data.tvrage.TVRageApi;

/**
 * A list of episode in a HashMap format for easy searching and retrieval
 *
 * @author stuart.boston
 *
 */
public class TVRageEpisodeList implements Serializable {

    private static final long serialVersionUID = 1L;
    private String showName;
    private int totalSeasons;
    private Map<TVRageEpisodeNumber, TVRageEpisode> episodeList;

    public TVRageEpisodeList() {
        showName = TVRageApi.UNKNOWN;
        totalSeasons = 0;
        episodeList = new TreeMap<>();
    }

    public boolean isValid() {
        return isValidString(showName) && !episodeList.isEmpty();
    }

    public Map<TVRageEpisodeNumber, TVRageEpisode> getEpisodeList() {
        return episodeList;
    }

    public void setEpisodeList(Map<TVRageEpisodeNumber, TVRageEpisode> episodeList) {
        this.episodeList = episodeList;
    }

    public void addEpisode(TVRageEpisode episode) {
        episodeList.put(episode.getEpisodeNumber(), episode);
    }

    public TVRageEpisode getEpisode(TVRageEpisodeNumber episodeNumber) {
        return episodeList.get(episodeNumber);
    }

    public TVRageEpisode getEpisode(int season, int episode) {
        return getEpisode(new TVRageEpisodeNumber(season, episode));
    }

    public TVRageEpisode getEpisode(String season, String episode) {
        return getEpisode(new TVRageEpisodeNumber(season, episode));
    }

    public String getShowName() {
        return showName;
    }

    public int getTotalSeasons() {
        return totalSeasons;
    }

    public void setShowName(String showName) {
        this.showName = isValidString(showName) ? showName.trim() : TVRageApi.UNKNOWN;
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
