package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.JTVSubtitlesApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.exception.TvSubtitlesException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.model.TVSubtitlesSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.exception.SubtitlesProviderInitException;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JTVsubtitlesAdapter
    extends AbstractAdapter<TVSubtitlesSubtitleDescriptor, ProviderSerieId, TvSubtitlesException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JTVsubtitlesAdapter.class);

    private static LazySupplier<JTVSubtitlesApi> jtvapi;
    @val @override SubtitleSource subtitleSource = SubtitleSource.TVSUBTITLES;
    @val @override String providerName = subtitleSource.name();
    @val @override boolean useSeasonForSerieId = false;

    public JTVsubtitlesAdapter(Manager manager, UserInteractionHandler userInteractionHandler) {
        super(manager, userInteractionHandler);
        if (jtvapi == null) {
            jtvapi = new LazySupplier<>(() -> {
                try {
                    return new JTVSubtitlesApi(manager);
                } catch (Exception e) {
                    throw new SubtitlesProviderInitException(providerName, e);
                }
            });
        }
    }

    private JTVSubtitlesApi getApi() {
        return jtvapi.get();
    }

    @Override
    public List<TVSubtitlesSubtitleDescriptor> searchMovieSubtitlesWithHash(String hash, Language language) {
        // TODO implement this
        return List.of();
    }

    @Override
    public List<TVSubtitlesSubtitleDescriptor> searchMovieSubtitlesWithId(int tvdbId, Language language) {
        // TODO implement this
        return List.of();
    }

    @Override
    public Collection<TVSubtitlesSubtitleDescriptor> searchMovieSubtitlesWithName(String name, @Nullable Integer year,
        Language language) {
        // TODO implement this
        return List.of();
    }

    @Override
    public Set<Subtitle> convertToSubtitles(MovieRelease movieRelease, Set<TVSubtitlesSubtitleDescriptor> subtitles,
        Language language) {
        // TODO implement this
        return Set.of();
    }

    @Override
    public Set<TVSubtitlesSubtitleDescriptor> searchSerieSubtitles(TvRelease tvRelease, Language language)
        throws TvSubtitlesException {
        return getProviderSerieId(tvRelease).map(
            providerSerieId -> tvRelease.episodes.stream().flatMap(episode -> {
                try {
                    return getApi().getSubtitles(providerSerieId, tvRelease.season, episode, language).stream();
                } catch (TvSubtitlesException e) {
                    LOGGER.error("API %s searchSubtitles for serie [%s] (%s)".formatted(subtitleSource.name,
                        TvRelease.formatName(providerSerieId.providerName, tvRelease.season, episode),
                        e.getMessage()), e);
                    return Stream.empty();
                }
            }).collect(Collectors.toSet())).orElseGet(Set::of);
    }

    @Override
    public Set<Subtitle> convertToSubtitles(TvRelease tvRelease, Collection<TVSubtitlesSubtitleDescriptor> subtitles,
        Language language) {
        return subtitles.stream()
            .map(sub -> new Subtitle(
                downloadSource:Subtitle.DownloadSource.of(sub.url),
                subtitleSource:subtitleSource,
                fileName:sub.filename,
                language:language,
                quality:ReleaseParser.getQualityKeyword(sub.filename + " " + sub.source),
                subtitleMatchType:SubtitleMatchType.EVERYTHING,
                releaseGroup:sub.releaseGroup))
            .collect(Collectors.toSet());
    }

    @Override
    public List<ProviderSerieId> getSortedProviderSerieIds(@Nullable Integer tvdbId, String serieName, int season)
        throws TvSubtitlesException {
        Pattern yearPatter = Pattern.compile("\\((\\d\\d\\d\\d)-(\\d\\d\\d\\d)\\)");
        return getApi().getUrisForSerieName(serieName)
            .stream()
            .sorted(Comparator.comparing((ProviderSerieId n) -> !serieName.replaceAll("[^A-Za-z]", "")
                    .equalsIgnoreCase(n.name.replaceAll("[^A-Za-z]", "")))
                .thenComparing((ProviderSerieId providerSerieId) -> {
                    Matcher matcher = yearPatter.matcher(providerSerieId.name);
                    if (matcher.find()) {
                        return Integer.parseInt(matcher.group(2));
                    }
                    return 0;
                }, Comparator.reverseOrder()))
            .toList();
    }

    @Override
    public String providerSerieIdToDisplayString(ProviderSerieId providerSerieId) {
        return providerSerieId.name;
    }
}
