package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;
import org.gestdown.model.EpisodeDto;
import org.gestdown.model.SubtitleDto;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.proxy.gestdown.JAddic7edProxyGestdownApi;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.lodder.subtools.sublibrary.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
public class JAddic7edAdapterViaProxy implements SubtitleProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(JAddic7edAdapterViaProxy.class);
    private final Manager manager;
    private final JAddic7edProxyGestdownApi jaapi;

    public JAddic7edAdapterViaProxy(Manager manager) {
        this.manager = manager;
        this.jaapi = new JAddic7edProxyGestdownApi(manager);
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.ADDIC7ED;
    }

    @Override
    public Set<Subtitle> searchSubtitles(TvRelease tvRelease, Language language) {
        return getShowNameForSerie(tvRelease)
                .map(serieName -> tvRelease.getEpisodeNumbers().stream()
                        .flatMap(episode -> {
                            try {
                                return jaapi
                                        .searchSubtitles(serieName, tvRelease.getSeason(), episode, language)
                                        .stream();
                            } catch (Exception e) {
                                LOGGER.error("API %s (via proxy) searchSubtitles for serie [%s] (%s)".formatted(getSubtitleSource().getName(),
                                        TvRelease.formatName(serieName, tvRelease.getSeason(), episode), e.getMessage()), e);
                                return Stream.empty();
                            }
                        })
                        .collect(Collectors.toSet()))
                .orElseGet(Set::of);
    }

    private Optional<String> getShowNameForSerie(TvRelease tvRelease) {
        try {
            return manager.getValueBuilder().key("Gestdown-showname-" + tvRelease.getOriginalName())
                    .cacheType(CacheType.DISK)
                    .optionalSupplier(() -> {
                        List<String> serieNamesForName = jaapi.getSerieNameForName(tvRelease.getOriginalName());
                        Optional<String> selectedSerieName = promptUserSelectFromList(serieNamesForName,
                                "Select correct serie name for name " + tvRelease.getOriginalName(), getSubtitleSource().getName());
                        if (!selectedSerieName.isEmpty()) {
                            return selectedSerieName;
                        } else {
                            List<String> serieNamesForName2 = jaapi.getSerieNameForName(tvRelease.getName());
                            if (!new HashSet<>(serieNamesForName).equals(new HashSet<>(serieNamesForName2))) {
                                selectedSerieName = promptUserSelectFromList(serieNamesForName,
                                        "Select correct serie name for name " + tvRelease.getOriginalName(), getSubtitleSource().getName());
                            }
                        }
                        return selectedSerieName;
                    })
                    .getOptional();
        } catch (Exception e) {
            LOGGER.error("API %s (via proxy) getShowNameForSerie for serie [%s] (%s)".formatted(getSubtitleSource().getName(),
                    tvRelease.getOriginalName(), e.getMessage()), e);
            return Optional.empty();
        }
    }

    private Optional<String> promptUserSelectFromList(List<String> options, String message, String title) {
        if (options.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable((String) JOptionPane.showInputDialog(null, message, title, JOptionPane.DEFAULT_OPTION, null,
                options.stream().toArray(String[]::new), "0"));
    }

    @Override
    public Set<Subtitle> searchSubtitles(MovieRelease movieRelease, Language language) {
        // TODO implement this
        return Set.of();
    }
}
