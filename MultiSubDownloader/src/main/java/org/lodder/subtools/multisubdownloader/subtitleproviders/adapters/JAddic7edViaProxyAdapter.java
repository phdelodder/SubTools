package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gestdown.invoker.ApiException;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.proxy.gestdown.JAddic7edProxyGestdownApi;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.experimental.ExtensionMethod;

@Getter
@ExtensionMethod({ OptionalExtension.class })
public class JAddic7edViaProxyAdapter extends AbstractAdapter<Subtitle, ProviderSerieId, ApiException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JAddic7edViaProxyAdapter.class);
    private final JAddic7edProxyGestdownApi jaapi;

    public JAddic7edViaProxyAdapter(Manager manager, UserInteractionHandler userInteractionHandler) {
        super(manager, userInteractionHandler);
        this.jaapi = new JAddic7edProxyGestdownApi(manager);
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.ADDIC7ED;
    }

    @Override
    public String getProviderName() {
        return getSubtitleSource().name() + "-GESTDOWN";
    }

    private JAddic7edProxyGestdownApi getApi() {
        return jaapi;
    }

    @Override
    public Collection<Subtitle> searchMovieSubtitlesWithHash(String hash, Language language) throws ApiException {
        // TODO implement this
        return List.of();
    }

    @Override
    public Collection<Subtitle> searchMovieSubtitlesWithId(int tvdbId, Language language) throws ApiException {
        // TODO implement this
        return List.of();
    }

    @Override
    public Collection<Subtitle> searchMovieSubtitlesWithName(String name, int year, Language language) throws ApiException {
        // TODO implement this
        return List.of();
    }

    @Override
    public Set<Subtitle> convertToSubtitles(MovieRelease movieRelease, Set<Subtitle> subtitles, Language language) {
        return subtitles;
    }

    @Override
    public Set<Subtitle> searchSerieSubtitles(TvRelease tvRelease, Language language)
            throws ApiException {
        return getProviderSerieId(tvRelease.getOriginalName(), tvRelease.getDisplayName(), tvRelease.getSeason(), tvRelease.getTvdbId())
                .orElseMap(() -> getProviderSerieId(tvRelease.getName(), tvRelease.getDisplayName(), tvRelease.getSeason(),
                        tvRelease.getTvdbId()))
                .map(providerSerieId -> tvRelease.getEpisodeNumbers().stream()
                        .flatMap(episode -> {
                            try {
                                return getApi().getSubtitles(providerSerieId, tvRelease.getSeason(), episode, language).stream();
                            } catch (ApiException e) {
                                LOGGER.error("API %s searchSubtitles for serie [%s] (%s)".formatted(getSubtitleSource().getName(),
                                        TvRelease.formatName(providerSerieId.getProviderName(), tvRelease.getSeason(), episode),
                                        e.getMessage()), e);
                                return Stream.empty();
                            }
                        })
                        .collect(Collectors.toSet()))
                .orElseGet(Set::of);
    }

    // @Override
    // public Set<Subtitle> searchSubtitles(TvRelease tvRelease, Language language, UserInteractionHandler userInteraction) {
    // ThrowingFunction<String, Optional<Addic7edSerieName>, ApiException> getAddic7edSerieNameFunction = name -> {
    // ThrowingFunction<List<String>, Optional<String>, ApiException> multipleResultHandler = shows -> {
    // if (shows.isEmpty()) {
    // return Optional.empty();
    // } else if (!confirmProviderMapping && shows.size() == 1) {
    // return Optional.of(shows.get(0));
    // } else {
    // String displaySerieName =
    // StringUtils.isNotBlank(tvRelease.getOriginalName()) ? tvRelease.getOriginalName() : tvRelease.getName();
    // String formattedName = SerieMapping.formatName(name);
    // return userInteraction.selectFromList(shows.stream().sorted(Comparator
    // .comparing(n -> formattedName.equalsIgnoreCase(SerieMapping.formatName(n)),
    // Comparator.reverseOrder()))
    // .toList(),
    // Messages.getString("SelectDialog.SelectSerieNameForName").formatted(displaySerieName),
    // getSubtitleSource().getName());
    // }
    // };
    // return jaapi.getAddic7edSerieName(name, tvRelease.getTvdbId(), multipleResultHandler);
    // };
    //
    // try {
    // Optional<Addic7edSerieName> addic7edSerieNameOptional = getAddic7edSerieNameFunction.apply(tvRelease.getOriginalName());
    // if (addic7edSerieNameOptional.isEmpty()) {
    // addic7edSerieNameOptional = getAddic7edSerieNameFunction.apply(tvRelease.getName());
    // }
    //
    // return addic7edSerieNameOptional
    // .map(addic7edSerieName -> tvRelease.getEpisodeNumbers().stream()
    // .flatMap(episode -> {
    // try {
    // return jaapi
    // .searchSubtitles(addic7edSerieName, tvRelease.getSeason(), episode, language)
    // .stream();
    // } catch (Exception e) {
    // LOGGER.error("API %s (via proxy) searchSubtitles for serie [%s] (%s)".formatted(getSubtitleSource().getName(),
    // TvRelease.formatName(addic7edSerieName.serieName(), tvRelease.getSeason(), episode), e.getMessage()), e);
    // return Stream.empty();
    // }
    // })
    // .collect(Collectors.toSet()))
    // .orElseGet(Set::of);
    // } catch (ApiException e) {
    // String displayName = StringUtils.isNotBlank(tvRelease.getOriginalName()) ? tvRelease.getOriginalName() : tvRelease.getName();
    // LOGGER.error("API %s (via proxy) searchSubtitles for serie [%s] (%s)".formatted(getSubtitleSource().getName(),
    // TvRelease.formatName(displayName, tvRelease.getSeason(), tvRelease.getFirstEpisodeNumber()), e.getMessage()), e);
    // return Set.of();
    // }
    // }

    @Override
    public List<ProviderSerieId> getSortedProviderSerieIds(String serieName, int season) throws ApiException {
        return getApi().getProviderSerieName(serieName).stream()
                .sorted(Comparator.comparing(n -> !serieName.replaceAll("[^A-Za-z]", "").equalsIgnoreCase(n.getName().replaceAll("[^A-Za-z]", ""))))
                .toList();
    }

    @Override
    public Set<Subtitle> convertToSubtitles(TvRelease tvRelease, Collection<Subtitle> subtitles, Language language) {
        return new HashSet<>(subtitles);
    }

    @Override
    public boolean useSeasonForSerieId() {
        return false;
    }

    @Override
    public String providerSerieIdToDisplayString(ProviderSerieId providerSerieId) {
        return providerSerieId.getName();
    }
}
