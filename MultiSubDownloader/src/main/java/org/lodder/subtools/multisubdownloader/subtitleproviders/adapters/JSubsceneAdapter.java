package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import static org.lodder.subtools.sublibrary.model.Subtitle.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.SubsceneApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.exception.SubsceneException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.model.SubSceneSerieId;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.model.SubsceneSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.exception.SubtitlesProviderInitException;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JSubsceneAdapter
    extends AbstractAdapter<SubsceneSubtitleDescriptor, SubSceneSerieId, SubsceneException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSubsceneAdapter.class);

    private static LazySupplier<SubsceneApi> api;
    @val @override SubtitleSource subtitleSource = SubtitleSource.SUBSCENE;
    @val @override String providerName = subtitleSource.name();
    @val @override boolean useSeasonForSerieId = true;

    public JSubsceneAdapter(Manager manager, UserInteractionHandler userInteractionHandler) {
        super(manager, userInteractionHandler);
        if (api == null) {
            api = new LazySupplier<>(() -> {
                try {
                    return new SubsceneApi(manager);
                } catch (Exception e) {
                    throw new SubtitlesProviderInitException(providerName, e);
                }
            });
        }
    }

    private SubsceneApi getApi() {
        return api.get();
    }

    @Override
    public List<SubsceneSubtitleDescriptor> searchMovieSubtitlesWithHash(String hash, Language language) {
        // TODO implement this
        return List.of();
    }

    @Override
    public List<SubsceneSubtitleDescriptor> searchMovieSubtitlesWithId(int tvdbId, Language language) {
        // TODO implement this
        return List.of();
    }

    @Override
    public Collection<SubsceneSubtitleDescriptor> searchMovieSubtitlesWithName(String name, @Nullable Integer year,
        Language language) {
        // TODO implement this
        return List.of();
    }

    @Override
    public Set<Subtitle> convertToSubtitles(MovieRelease movieRelease, Set<SubsceneSubtitleDescriptor> subtitles,
        Language language) {
        // TODO implement this
        return Set.of();
    }

    @Override
    public Set<SubsceneSubtitleDescriptor> searchSerieSubtitles(TvRelease tvRelease, Language language)
        throws SubsceneException {
        return getProviderSerieId(tvRelease).map(
            providerSerieId -> tvRelease.episodes.stream().flatMap(episode -> {
                try {
                    return getApi().getSubtitles(providerSerieId, tvRelease.season, episode, language).stream();
                } catch (SubsceneException e) {
                    LOGGER.error("API %s searchSubtitles for serie [%s] (%s)".formatted(subtitleSource.name,
                        TvRelease.formatName(providerSerieId.providerName, tvRelease.season, episode),
                        e.getMessage()), e);
                    return Stream.empty();
                }
            }).collect(Collectors.toSet())).orElseGet(Set::of);
    }

    @Override
    public List<SubSceneSerieId> getSortedProviderSerieIds(@Nullable Integer tvdbId, String serieName, int season)
        throws SubsceneException {
        ToIntFunction<String> providerTypeFunction = value -> switch (value) {
            case "TV-Serie" -> 1;
            case "Exact" -> 2;
            case "Close" -> 3;
            default -> 4;
        };
        Pattern yearPattern = Pattern.compile("(\\d\\d\\d\\d)");
        return getApi().getSubSceneSerieNames(serieName)
            .entrySet()
            .stream()
            .sorted(Comparator.comparingInt(entry -> providerTypeFunction.applyAsInt(entry.getKey())))
            .map(Entry::getValue)
            .flatMap(List::stream)
            .sorted(Comparator.comparing((SubSceneSerieId serieId) -> serieId.season == 0)
                .thenComparing(serieId -> {
                    Matcher matcher = yearPattern.matcher(serieId.name);
                    return matcher.find() ? Integer.parseInt(matcher.group()) : 0;
                }, Comparator.reverseOrder())
                .thenComparing(SubSceneSerieId::getSeason, Comparator.reverseOrder()))
            .distinct()
            .toList();
    }

    @Override
    public Set<Subtitle> convertToSubtitles(TvRelease tvRelease, Collection<SubsceneSubtitleDescriptor> subtitles,
        Language language) {
        return subtitles.stream()
            .filter(sub -> language == sub.language)
            .filter(sub -> sub.name
                .contains(getSeasonEpisodeString(tvRelease.season, tvRelease.firstEpisode)))
            .map(sub -> new Subtitle(
                downloadSource:DownloadSource.of(sub.urlSupplier),
                subtitleSource:subtitleSource,
                fileName:sub.name.removeIllegalFilenameChars(),
                language:sub.language,
                quality:ReleaseParser.getQualityKeyword(sub.name),
                subtitleMatchType:SubtitleMatchType.EVERYTHING,
                releaseGroup:ReleaseParser.extractReleaseGroup(sub.name, false),
                uploader:sub.uploader,
                hearingImpaired:sub.hearingImpaired))
            .collect(Collectors.toSet());
    }

    private String getSeasonEpisodeString(int season, int episode) {
        return "S%02dE%02d".formatted(season, episode);
    }

    @Override
    public String providerSerieIdToDisplayString(SubSceneSerieId providerSerieId) {
        if (providerSerieId.id.endsWith("-season")) {
            OptionalInt season = IntStream.rangeClosed(1, 100)
                .filter(i -> providerSerieId.id.endsWith("-${SubsceneApi.getOrdinalName(i).toLowerCase()}-season"))
                .findAny();
            if (season.isPresent()) {
                return "%s %s %s".formatted(providerSerieId.name, Messages.getText("App.Season"), season.getAsInt());
            }
        }
        return providerSerieId.name;
    }
}
