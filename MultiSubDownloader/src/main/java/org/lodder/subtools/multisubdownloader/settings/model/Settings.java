package org.lodder.subtools.multisubdownloader.settings.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import manifold.ext.props.rt.api.var;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.control.VideoPatterns;
import org.lodder.subtools.sublibrary.data.UserInteractionSettingsIntf;
import org.lodder.subtools.sublibrary.model.SubtitleSource;

public class Settings implements UserInteractionSettingsIntf {

    @var Path lastOutputDir;
    @override @var boolean optionsAlwaysConfirm;
    @var boolean optionSubtitleExactMatch = true;
    @var boolean optionSubtitleKeywordMatch = true;
    @var boolean optionSubtitleExcludeHearingImpaired;
    @var boolean optionsShowOnlyFound;
    @var boolean optionsStopOnSearchError;
    @val Set<PathOrRegex> excludeList = new LinkedHashSet<>();
    @val LibrarySettings movieLibrarySettings = new LibrarySettings();
    @val LibrarySettings episodeLibrarySettings = new LibrarySettings();
    @var String generalProxyHost = "";
    @var int generalProxyPort = 80;
    @var boolean generalProxyEnabled;
    @var List<Path> defaultIncomingFolders = new ArrayList<>();
    @var List<Path> localSourcesFolders = new ArrayList<>();
    @var boolean optionRecursive;
    @val ScreenSettings screenSettings = new ScreenSettings();
    @var boolean loginAddic7edEnabled;
    @var String loginAddic7edUsername;
    @var String loginAddic7edPassword;
    @var boolean loginOpenSubtitlesEnabled;
    @var String loginOpenSubtitlesUsername;
    @var String loginOpenSubtitlesPassword;
    @var boolean serieSourceAddic7ed = true;
    @var boolean serieSourceAddic7edProxy = true;
    @var boolean serieSourceTvSubtitles = true;
    @var boolean serieSourcePodnapisi = true;
    @var boolean serieSourceOpensubtitles = true;
    @var boolean serieSourceLocal = true;
    @var boolean serieSourceSubscene = true;
    @var SettingsProcessEpisodeSource processEpisodeSource = SettingsProcessEpisodeSource.TVDB;
    @val Map<String, Integer> sortWeights;
    @var Language subtitleLanguage;
    @override @var boolean optionsMinAutomaticSelection;
    @override @var int optionsMinAutomaticSelectionValue;
    @var UpdateCheckPeriod updateCheckPeriod;
    @var UpdateType updateType;
    @override @var boolean optionsDefaultSelection;
    @override @var List<VideoPatterns.Source> optionsDefaultSelectionQualityList = new ArrayList<>();
    @var int settingsVersion;
    @override @var boolean optionsConfirmProviderMapping;
    @var Language language;

    public Settings() {
        // TODO: user should be able to edit/add these through a panel
        Map<String, Integer> sortWeightsTemp = new HashMap<>();
        sortWeightsTemp.put("%GROUP%", 5);
        VideoPatterns.Source.values().forEach(source -> sortWeightsTemp.put(source.regex, 2));
        VideoPatterns.AudioEncoding.values().forEach(encoding -> sortWeightsTemp.put(encoding.regex, 2));
        this.sortWeights = Collections.unmodifiableMap(sortWeightsTemp);
    }

    public List<Path> getDefaultFolders() {
        return defaultIncomingFolders;
    }

    public boolean hasDefaultFolders() {
        return !defaultIncomingFolders.isEmpty();
    }

    public boolean useSerieSource(SubtitleSource subtitleSource) {
        // TODO: dynamically inject SubtitleProvider to settings
        return switch (subtitleSource) {
            case ADDIC7ED -> serieSourceAddic7ed;
            case OPENSUBTITLES -> serieSourceOpensubtitles;
            case PODNAPISI -> serieSourcePodnapisi;
            case TVSUBTITLES -> serieSourceTvSubtitles;
            case LOCAL -> serieSourceLocal;
            case SUBSCENE -> serieSourceSubscene;
        };
    }

    public Settings replaceExcludeList(Collection<PathOrRegex> exclusions) {
        excludeList.clear();
        excludeList.addAll(exclusions);
        return this;
    }
}
