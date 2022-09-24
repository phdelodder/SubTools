package org.lodder.subtools.sublibrary.data.tvrage;

/*
 * Copyright (c) 2004-2013 Stuart Boston
 *
 * This file is part of the TVRage API.
 *
 * TVRage API is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * TVRage API is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with TVRage API. If not,
 * see <http://www.gnu.org/licenses/>.
 */
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.XmlHTTP;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageCountryDetail;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageEpisode;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageEpisodeList;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageEpisodeNumber;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageShowInfo;
import org.lodder.subtools.sublibrary.xml.XmlExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ XmlExtension.class })
public class TVRageParser extends XmlHTTP {

    // Literals
    private static final String EPISODE = "episode";
    private static final String SUMMARY = "summary";
    private static final String TITLE = "title";
    private static final String AIRDATE = "airdate";
    private static final String COUNTRY = "country";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TVRageParser(Manager manager) {
        super(manager);
    }

    public Optional<TVRageEpisode> getEpisodeInfo(String searchUrl) throws TvrageException {
        return getManager().getValueBuilder()
                .key("TVRage-EpisodeInfo-" + searchUrl.toLowerCase())
                .cacheType(CacheType.DISK)
                .optionalValueSupplier(() -> {
                    try {
                        return getXML(searchUrl).cacheType(CacheType.MEMORY).getAsDocument()
                                // The EpisodeInfo contains show information as well, but we will skip this
                                .map(doc -> doc.getElementsByTagName(EPISODE))
                                .filter(nlEpisode -> nlEpisode.getLength() > 0)
                                .map(nlEpisode -> parseEpisodeInfo((Element) nlEpisode.item(0)));
                    } catch (ParserConfigurationException | ManagerException e) {
                        throw new TvrageException(e);
                    }
                }).getOptional();
    }

    public TVRageEpisodeList getEpisodeList(String searchUrl) throws TvrageException {
        return getManager().getValueBuilder()
                .key("TVRage-EpisodeInfo-" + searchUrl.toLowerCase())
                .cacheType(CacheType.MEMORY)
                .valueSupplier(() -> {
                    TVRageEpisodeList epList = new TVRageEpisodeList();
                    try {
                        Optional<Document> doc = getXML(searchUrl).cacheType(CacheType.NONE).getAsDocument();
                        if (doc.isEmpty()) {
                            return epList;
                        }
                        NodeList nlEpisodeList = doc.get().getElementsByTagName("Show");
                        if (nlEpisodeList.getLength() == 0) {
                            return epList;
                        }

                        // Get the show name and total seasons
                        nlEpisodeList.stream()
                                .filter(nEpisodeList -> nEpisodeList.getNodeType() == Node.ELEMENT_NODE)
                                .map(Element.class::cast)
                                .forEach(eEpisodeList -> {
                                    epList.setShowName(DOMHelper.getValueFromElement(eEpisodeList, "name"));
                                    epList.setTotalSeasons(DOMHelper.getValueFromElement(eEpisodeList, "totalseasons"));
                                });

                        // Now process the individual seasons
                        processSeasons(epList, doc.get().getElementsByTagName("Season"));

                        return epList;
                    } catch (ParserConfigurationException | ManagerException e) {
                        throw new TvrageException(e);
                    }
                }).get();

    }

    /**
     * process the individual seasons
     *
     * @param epList
     * @param nlSeasons
     */
    private static void processSeasons(TVRageEpisodeList epList, NodeList nlSeasons) {
        if (nlSeasons == null || nlSeasons.getLength() == 0) {
            return;
        }
        nlSeasons.stream()
                .filter(nEpisodeList -> nEpisodeList.getNodeType() == Node.ELEMENT_NODE)
                .map(Element.class::cast)
                .forEach(eEpisodeList -> {
                    // Get the season number
                    String season = eEpisodeList.getAttribute("no");

                    NodeList nlEpisode = eEpisodeList.getElementsByTagName(EPISODE);
                    if (nlEpisode != null && nlEpisode.getLength() != 0) {
                        nlEpisode.stream()
                                .filter(nEpisode -> nEpisode.getNodeType() == Node.ELEMENT_NODE)
                                .map(Element.class::cast)
                                .forEach(eEpisode -> epList.addEpisode(parseEpisode(eEpisode, season)));
                    }
                });
    }

    public List<TVRageShowInfo> getSearchShow(String searchUrl) throws TvrageException {
        return getManager().getValueBuilder()
                .key("TVRage-SearchShow-" + searchUrl.toLowerCase())
                .cacheType(CacheType.MEMORY)
                .valueSupplier(() -> {
                    try {
                        return (ArrayList<TVRageShowInfo>) getXML(searchUrl).cacheType(CacheType.NONE).getAsDocument()
                                .map(doc -> doc.getElementsByTagName("show"))
                                .filter(nlShowInfo -> nlShowInfo.getLength() > 0)
                                .map(nlShowInfo -> nlShowInfo.stream()
                                        .filter(nShowInfo -> nShowInfo.getNodeType() == Node.ELEMENT_NODE)
                                        .map(Element.class::cast)
                                        .map(TVRageParser::parseNextShowInfo)
                                        .collect(Collectors.toList()))
                                .orElseGet(ArrayList::new);
                    } catch (ParserConfigurationException | ManagerException e) {
                        throw new TvrageException(e);
                    }
                }).get();
    }

    public List<TVRageShowInfo> getShowInfo(String searchUrl) throws TvrageException {
        return getManager().getValueBuilder()
                .key("TVRage-ShowInfo-" + searchUrl.toLowerCase())
                .cacheType(CacheType.MEMORY)
                .valueSupplier(() -> {
                    try {
                        return (ArrayList<TVRageShowInfo>) getXML(searchUrl).cacheType(CacheType.MEMORY).getAsDocument()
                                .map(doc -> doc.getElementsByTagName("Showinfo"))
                                .filter(nlShowInfo -> nlShowInfo.getLength() > 0)
                                .map(nlShowInfo -> nlShowInfo.stream()
                                        .filter(nShowInfo -> nShowInfo.getNodeType() == Node.ELEMENT_NODE)
                                        .map(Element.class::cast)
                                        .map(TVRageParser::parseNextShowInfo)
                                        .collect(Collectors.toList()))
                                .orElseGet(ArrayList::new);
                    } catch (ParserConfigurationException | ManagerException e) {
                        throw new TvrageException(e);
                    }
                }).get();
    }

    private static TVRageEpisode parseEpisode(Element eEpisode, String season) {
        TVRageEpisode episode = new TVRageEpisode();
        TVRageEpisodeNumber en = new TVRageEpisodeNumber();

        en.setSeason(season);
        en.setEpisode(DOMHelper.getValueFromElement(eEpisode, "seasonnum"));
        en.setAbsolute(DOMHelper.getValueFromElement(eEpisode, "epnum"));
        episode.setEpisodeNumber(en);

        episode.setProductionId(DOMHelper.getValueFromElement(eEpisode, "prodnum"));
        episode.setAirDate(DOMHelper.getValueFromElement(eEpisode, AIRDATE));
        episode.setLink(DOMHelper.getValueFromElement(eEpisode, "link"));
        episode.setTitle(DOMHelper.getValueFromElement(eEpisode, TITLE));
        episode.setSummary(DOMHelper.getValueFromElement(eEpisode, SUMMARY));
        episode.setRating(DOMHelper.getValueFromElement(eEpisode, "rating"));
        episode.setScreenCap(DOMHelper.getValueFromElement(eEpisode, "screencap"));

        return episode;
    }

    private static TVRageEpisode parseEpisodeInfo(Element eEpisodeInfo) {
        TVRageEpisode episode = new TVRageEpisode();

        episode.setTitle(DOMHelper.getValueFromElement(eEpisodeInfo, TITLE));
        episode.setAirDate(DOMHelper.getValueFromElement(eEpisodeInfo, AIRDATE));
        episode.setLink(DOMHelper.getValueFromElement(eEpisodeInfo, "url"));
        episode.setSummary(DOMHelper.getValueFromElement(eEpisodeInfo, SUMMARY));

        // Process the season & episode field
        Pattern pattern = Pattern.compile("(\\d*)[x](\\d*)");
        Matcher matcher = pattern.matcher(DOMHelper.getValueFromElement(eEpisodeInfo, "number"));
        if (matcher.find()) {
            TVRageEpisodeNumber en = new TVRageEpisodeNumber();
            en.setSeason(matcher.group(1));
            en.setEpisode(matcher.group(2));
            episode.setEpisodeNumber(en);
        }

        return episode;
    }

    private static TVRageShowInfo parseNextShowInfo(Element eShowInfo) {
        TVRageShowInfo showInfo = new TVRageShowInfo();
        String text;

        // ShowID
        showInfo.setShowId(DOMHelper.getValueFromElement(eShowInfo, "showid"));

        // ShowName
        text = DOMHelper.getValueFromElement(eShowInfo, "showname");
        if (!TVRageApi.isValidString(text)) {
            text = DOMHelper.getValueFromElement(eShowInfo, "name");
        }
        showInfo.setShowName(text);

        // ShowLink
        text = DOMHelper.getValueFromElement(eShowInfo, "showlink");
        if (!TVRageApi.isValidString(text)) {
            text = DOMHelper.getValueFromElement(eShowInfo, "link");
        }
        showInfo.setShowLink(text);

        // Country
        text = DOMHelper.getValueFromElement(eShowInfo, COUNTRY);
        if (!TVRageApi.isValidString(text)) {
            text = DOMHelper.getValueFromElement(eShowInfo, "origin_country");
        }
        showInfo.setCountry(text);

        // Started
        showInfo.setStarted(DOMHelper.getValueFromElement(eShowInfo, "started"));

        // StartDate
        showInfo.setStartDate(DOMHelper.getValueFromElement(eShowInfo, "startdate"));

        // Ended
        showInfo.setEnded(DOMHelper.getValueFromElement(eShowInfo, "ended"));

        // Seasons
        showInfo.setTotalSeasons(DOMHelper.getValueFromElement(eShowInfo, "seasons"));

        // Status
        showInfo.setStatus(DOMHelper.getValueFromElement(eShowInfo, "status"));

        // Classification
        showInfo.setClassification(DOMHelper.getValueFromElement(eShowInfo, "classification"));

        // Summary
        showInfo.setSummary(DOMHelper.getValueFromElement(eShowInfo, SUMMARY));

        // Runtime
        showInfo.setRuntime(DOMHelper.getValueFromElement(eShowInfo, "runtime"));

        // Air Time
        showInfo.setAirTime(DOMHelper.getValueFromElement(eShowInfo, "airtime"));

        // Air Day
        showInfo.setAirDay(DOMHelper.getValueFromElement(eShowInfo, "airday"));

        // Time Zone
        showInfo.setTimezone(DOMHelper.getValueFromElement(eShowInfo, "timezone"));

        // Network
        processNetwork(showInfo, eShowInfo);

        // AKAs
        processAka(showInfo, eShowInfo);

        // Genres
        processGenre(showInfo, eShowInfo);

        return showInfo;
    }

    /**
     * Process network information
     *
     * @param showInfo
     * @param eShowInfo
     */
    private static void processNetwork(TVRageShowInfo showInfo, Element eShowInfo) {
        eShowInfo.getElementsByTagName("network").stream()
                .filter(nShowInfo -> nShowInfo.getNodeType() == Node.ELEMENT_NODE)
                .map(Element.class::cast)
                .map(eNetwork -> new TVRageCountryDetail(eNetwork.getAttribute(COUNTRY), eNetwork.getTextContent()))
                .forEach(showInfo::addNetwork);
    }

    /**
     * Process AKA information
     *
     * @param showInfo
     * @param eShowInfo
     */
    private static void processAka(TVRageShowInfo showInfo, Element eShowInfo) {
        eShowInfo.getElementsByTagName("aka").stream()
                .filter(nShowInfo -> nShowInfo.getNodeType() == Node.ELEMENT_NODE)
                .map(Element.class::cast)
                .map(eAka -> new TVRageCountryDetail(eAka.getAttribute(COUNTRY), eAka.getTextContent()))
                .forEach(showInfo::addAka);
    }

    /**
     * Process Genres
     *
     * @param showInfo
     * @param eShowInfo
     */
    private static void processGenre(TVRageShowInfo showInfo, Element eShowInfo) {
        eShowInfo.getElementsByTagName("genre").stream()
                .filter(nShowInfo -> nShowInfo.getNodeType() == Node.ELEMENT_NODE)
                .map(Element.class::cast)
                .map(Element::getNodeValue)
                .forEach(showInfo::addGenre);
    }

    public static LocalDate parseDate(String strDate) throws DateTimeParseException {
        return LocalDate.parse(strDate, DATE_FORMATTER);
    }
}
