package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.pivovarit.function.ThrowingSupplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import org.gestdown.invoker.ApiException;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.proxy.gestdown.JAddic7edProxyGestdownApi;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public final class JAddic7edViaProxyAdapter extends AbstractAdapter<Subtitle, ProviderSerieId, ApiException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JAddic7edViaProxyAdapter.class);

    private final JAddic7edProxyGestdownApi jaapi;
    @val @override SubtitleSource subtitleSource = SubtitleSource.ADDIC7ED;
    @val @override String providerName = subtitleSource.name() + "-GESTDOWN";
    @val @override boolean useSeasonForSerieId = false;

    public JAddic7edViaProxyAdapter(Manager manager, UserInteractionHandler userInteractionHandler) {
        super(manager, userInteractionHandler);
        this.jaapi = new JAddic7edProxyGestdownApi(manager);
    }

    private JAddic7edProxyGestdownApi getApi() {
        return jaapi;
    }

    @Override
    public Collection<Subtitle> searchMovieSubtitlesWithHash(String hash, Language language) {
        // TODO implement this
        return List.of();
    }

    @Override
    public Collection<Subtitle> searchMovieSubtitlesWithId(int tvdbId, Language language) {
        // TODO implement this
        return List.of();
    }

    @Override
    public Collection<Subtitle> searchMovieSubtitlesWithName(String name, @Nullable Integer year, Language language) {
        // TODO implement this
        return List.of();
    }

    @Override
    public Set<Subtitle> convertToSubtitles(MovieRelease movieRelease, Set<Subtitle> subtitles, Language language) {
        return subtitles;
    }

    @Override
    public Set<Subtitle> searchSerieSubtitles(TvRelease tvRelease, Language language) throws ApiException {
        return getProviderSerieId(tvRelease).map(
            providerSerieId -> tvRelease.episodes.stream().flatMap(episode -> {
                try {
                    return new ExecuteCall<>(
                        () -> getApi().getSubtitles(providerSerieId, tvRelease.season, episode, language))
                        .message("getSubtitles: [%s]".formatted(
                            TvRelease.formatName(providerSerieId.providerName, tvRelease.season, episode)))
                        .retryWhenHttpCode(ReturnCode.REFRESHING)
                        .retryWhenHttpCode(ReturnCode.RATE_LIMIT_REACHED)
                        .execute()
                        .stream();
                } catch (ApiException e) {
                    LOGGER.error("API %s searchSubtitles for serie [%s] (%s)".formatted(subtitleSource.name,
                        TvRelease.formatName(providerSerieId.providerName, tvRelease.season, episode),
                        e.getMessage()), e);
                    return Stream.empty();
                }
            }).collect(Collectors.toSet())).orElseGet(Set::of);
    }

    @Override
    public List<ProviderSerieId> getSortedProviderSerieIds(@Nullable Integer tvdbId, String serieName, int season)
        throws ApiException {
        List<ProviderSerieId> serieIds = tvdbId == null ? List.of() :
            new ExecuteCall<>(() -> getApi().getProviderSerieName(tvdbId))
                .message("getProviderSerieName: [$tvdbId]")
                .retryWhenHttpCode(ReturnCode.RATE_LIMIT_REACHED)
                .handleHttpCode(ReturnCode.NOT_FOUND, () -> {
                    LOGGER.info("API %s - Could not find tvdbId [%s]".formatted(providerName, tvdbId));
                    return List.of();
                })
                .execute();

        if (serieIds.isEmpty()) {
            serieIds = new ExecuteCall<>(() -> getApi().getProviderSerieName(serieName)).message(
                    "getProviderSerieName: [$serieName]")
                .retryWhenHttpCode(ReturnCode.RATE_LIMIT_REACHED)
                .handleHttpCode(ReturnCode.NOT_FOUND, () -> {
                    LOGGER.info("API $providerName - Could not find serie name [$serieName]");
                    return List.of();
                })
                .execute();
        }
        return serieIds.stream()
            .sorted(Comparator.comparing(n -> !serieName.replaceAll("[^A-Za-z]", "")
                .equalsIgnoreCase(n.name.replaceAll("[^A-Za-z]", ""))))
            .toList();
    }

    @Override
    public Set<Subtitle> convertToSubtitles(TvRelease tvRelease, Collection<Subtitle> subtitles, Language language) {
        return new HashSet<>(subtitles);
    }

    @Override
    public String providerSerieIdToDisplayString(ProviderSerieId providerSerieId) {
        return providerSerieId.name;
    }

    @Getter
    @RequiredArgsConstructor
    private enum ReturnCode {
        NOT_FOUND(404), RATE_LIMIT_REACHED(429), REFRESHING(423);

        final int code;

        public boolean isSameCode(int code) {
            return this.code == code;
        }
    }

    private static class ExecuteCall<T> extends AbstractAdapter.ExecuteCall<T, ApiException> {

        public ExecuteCall(ThrowingSupplier<T, ApiException> supplier) {
            super(supplier);
        }

        public ExecuteCall<T> retryWhenHttpCode(ReturnCode returnCode) {
            super.retryWhenException(e -> returnCode.isSameCode(e.getCode()));
            return this;
        }

        public ExecuteCall<T> handleHttpCode(ReturnCode returnCode, Function<ApiException, T> function) {
            super.handleException(e -> returnCode.isSameCode(e.getCode()), function);
            return this;
        }

        public ExecuteCall<T> handleHttpCode(ReturnCode returnCode, Supplier<T> supplier) {
            super.handleException(e -> returnCode.isSameCode(e.getCode()), supplier);
            return this;
        }

        @Override
        public ExecuteCall<T> handleException(Supplier<T> suppliers) {
            super.handleException(_ -> true, suppliers);
            return this;
        }
    }
}
