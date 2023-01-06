package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.time.Duration;
import java.util.Arrays;
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

import com.pivovarit.function.ThrowingSupplier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
                                return retry(() -> getApi().getSubtitles(providerSerieId, tvRelease.getSeason(), episode, language).stream(),
                                        "getSubtitles: [%s]"
                                                .formatted(TvRelease.formatName(providerSerieId.getProviderName(), tvRelease.getSeason(), episode)),
                                        ReturnCode.REFRESHING, ReturnCode.RATE_LIMIT_REACHED);
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

    @Override
    public List<ProviderSerieId> getSortedProviderSerieIds(String serieName, int season) throws ApiException {
        try {
            return retry(() -> getApi().getProviderSerieName(serieName).stream()
                    .sorted(Comparator
                            .comparing(n -> !serieName.replaceAll("[^A-Za-z]", "").equalsIgnoreCase(n.getName().replaceAll("[^A-Za-z]", ""))))
                    .toList(), "getProviderSerieName [%s]".formatted(serieName),
                    ReturnCode.RATE_LIMIT_REACHED);
        } catch (ApiException e) {
            if (ReturnCode.NOT_FOUND.isSameCode(e.getCode())) {
                LOGGER.info("API %s - Could not find serie name [%s]".formatted(getProviderName(), serieName));
                return List.of();
            }
            throw e;
        }
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

    @Getter
    @RequiredArgsConstructor
    private enum ReturnCode {
        NOT_FOUND(404),
        RATE_LIMIT_REACHED(429),
        REFRESHING(423);

        final int code;

        public boolean isSameCode(int code) {
            return this.code == code;
        }
    }

    private <T> T retry(ThrowingSupplier<T, ApiException> supplier, String message, ReturnCode... returnCodes) throws ApiException {
        return retry(supplier, 3, message, returnCodes);
    }

    private <T> T retry(ThrowingSupplier<T, ApiException> supplier, int retriesLeft, String message, ReturnCode... returnCodes) throws ApiException {
        if (retriesLeft <= 0) {
            throw new RuntimeException("API %s - Max retries reached when calling %s".formatted(getProviderName(), message));
        }
        try {
            return supplier.get();
        } catch (ApiException e) {
            if (retriesLeft == 0) {
                throw e;
            }
            if (Arrays.stream(returnCodes).anyMatch(returnCode -> returnCode.isSameCode(e.getCode()))) {
                try {
                    Thread.sleep(Duration.ofSeconds(5));
                } catch (InterruptedException e1) {
                    // continue
                }
                return retry(supplier, retriesLeft--, message);
            } else {
                throw e;
            }
        }
    }
}
