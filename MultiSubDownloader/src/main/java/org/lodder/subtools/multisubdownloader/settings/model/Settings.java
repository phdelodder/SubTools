package org.lodder.subtools.multisubdownloader.settings.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.control.VideoPatterns;
import org.lodder.subtools.sublibrary.data.UserInteractionSettingsIntf;
import org.lodder.subtools.sublibrary.model.SubtitleSource;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;

@Getter
@Setter
@Accessors(chain = true)
@ExtensionMethod({ Arrays.class })
public class Settings implements UserInteractionSettingsIntf {

    private Path lastOutputDir;
    private boolean optionsAlwaysConfirm;
    private boolean optionSubtitleExactMatch = true;
    private boolean optionSubtitleKeywordMatch = true;
    private boolean optionSubtitleExcludeHearingImpaired;
    private boolean optionsShowOnlyFound, optionsStopOnSearchError;
    private final Set<PathOrRegex> excludeList = new LinkedHashSet<>();
    private final LibrarySettings movieLibrarySettings = new LibrarySettings();
    private final LibrarySettings episodeLibrarySettings = new LibrarySettings();
    private String generalProxyHost = "";
    private int generalProxyPort = 80;
    private boolean generalProxyEnabled;
    private List<Path> defaultIncomingFolders = new ArrayList<>();
    private List<Path> localSourcesFolders = new ArrayList<>();
    private boolean optionRecursive;
    private final ScreenSettings screenSettings = new ScreenSettings();
    private boolean loginAddic7edEnabled;
    private String loginAddic7edUsername;
    private String loginAddic7edPassword;
    private boolean loginOpenSubtitlesEnabled;
    private String loginOpenSubtitlesUsername;
    private String loginOpenSubtitlesPassword;
    private boolean serieSourceAddic7ed = true;
    private boolean serieSourceAddic7edProxy = true;
    private boolean serieSourceTvSubtitles = true;
    private boolean serieSourcePodnapisi = true;
    private boolean serieSourceOpensubtitles = true;
    private boolean serieSourceLocal = true;
    private boolean serieSourceSubscene = true;
    private SettingsProcessEpisodeSource processEpisodeSource = SettingsProcessEpisodeSource.TVDB;
    private final Map<String, Integer> sortWeights;
    private Language subtitleLanguage;
    private boolean optionsMinAutomaticSelection;
    private int optionsMinAutomaticSelectionValue;
    private UpdateCheckPeriod updateCheckPeriod;
    private UpdateType updateType;
    private boolean optionsDefaultSelection;
    private List<VideoPatterns.Source> optionsDefaultSelectionQualityList = new ArrayList<>();
    private int settingsVersion;
    private boolean optionsConfirmProviderMapping;
    private Language language;

    public Settings() {
        // TODO: user should be able to edit/add these through a panel
        Map<String, Integer> sortWeightsTemp = new HashMap<>();
        sortWeightsTemp.put("%GROUP%", 5);
        VideoPatterns.Source.values().stream()
                .forEach(source -> source.getValues().stream()
                        .forEach(keyword -> sortWeightsTemp.put(keyword, source.isManyDifferentSources() ? 1 : 2)));
        VideoPatterns.AudioEncoding.values().stream()
                .forEach(encoding -> encoding.getValues().stream().forEach(keyword -> sortWeightsTemp.put(keyword, 2)));
        this.sortWeights = Collections.unmodifiableMap(sortWeightsTemp);
    }

    public List<Path> getDefaultFolders() {
        return getDefaultIncomingFolders();
    }

    public boolean hasDefaultFolders() {
        return !getDefaultIncomingFolders().isEmpty();
    }

    public boolean isSerieSource(SubtitleSource sbtitleSource) {
        // TODO: dynamically inject SubtitleProvider to settings
        return switch (sbtitleSource) {
            case ADDIC7ED -> this.isSerieSourceAddic7ed();
            case OPENSUBTITLES -> this.isSerieSourceOpensubtitles();
            case PODNAPISI -> this.isSerieSourcePodnapisi();
            case TVSUBTITLES -> this.isSerieSourceTvSubtitles();
            case LOCAL -> this.isSerieSourceLocal();
            case SUBSCENE -> this.isSerieSourceSubscene();
        };
    }

    public Settings setExcludeList(Collection<PathOrRegex> exclusions) {
        excludeList.clear();
        excludeList.addAll(exclusions);
        return this;
    }
}
