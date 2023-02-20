package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

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
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.lodder.subtools.sublibrary.util.StringUtil;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.experimental.ExtensionMethod;

@Getter
@ExtensionMethod({ OptionalExtension.class })
public class JSubsceneAdapter extends AbstractAdapter<SubsceneSubtitleDescriptor, SubSceneSerieId, SubsceneException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSubsceneAdapter.class);
    private static LazySupplier<SubsceneApi> api;

    public JSubsceneAdapter(Manager manager, UserInteractionHandler userInteractionHandler) {
        super(manager, userInteractionHandler);
        if (api == null) {
            api = new LazySupplier<>(() -> {
                try {
                    return new SubsceneApi(manager);
                } catch (Exception e) {
                    throw new SubtitlesProviderInitException(getProviderName(), e);
                }
            });
        }
    }

    private SubsceneApi getApi() {
        return api.get();
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.SUBSCENE;
    }

    @Override
    public String getProviderName() {
        return getSubtitleSource().name();
    }

    @Override
    public List<SubsceneSubtitleDescriptor> searchMovieSubtitlesWithHash(String hash, Language language) throws SubsceneException {
        // TODO implement this
        return List.of();
    }

    @Override
    public List<SubsceneSubtitleDescriptor> searchMovieSubtitlesWithId(int tvdbId, Language language) throws SubsceneException {
        // TODO implement this
        return List.of();
    }

    @Override
    public List<SubsceneSubtitleDescriptor> searchMovieSubtitlesWithName(String name, int year, Language language) throws SubsceneException {
        // TODO implement this
        return List.of();
    }

    @Override
    public Set<Subtitle> convertToSubtitles(MovieRelease movieRelease, Set<SubsceneSubtitleDescriptor> subtitles, Language language) {
        // TODO implement this
        return Set.of();
    }

    @Override
    public Set<SubsceneSubtitleDescriptor> searchSerieSubtitles(TvRelease tvRelease, Language language) throws SubsceneException {
        return getProviderSerieId(tvRelease)
                .map(providerSerieId -> tvRelease.getEpisodeNumbers().stream()
                        .flatMap(episode -> {
                            try {
                                return getApi().getSubtitles(providerSerieId, tvRelease.getSeason(), episode, language).stream();
                            } catch (SubsceneException e) {
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
    public List<SubSceneSerieId> getSortedProviderSerieIds(String serieName, int season) throws SubsceneException {
        ToIntFunction<String> providerTypeFunction = value -> switch (value) {
            case "TV-Serie" -> 1;
            case "Exact" -> 2;
            case "Close" -> 3;
            default -> 4;
        };
        Pattern yearPattern = Pattern.compile("(\\d\\d\\d\\d)");
        return getApi().getSubSceneSerieNames(serieName).entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> providerTypeFunction.applyAsInt(entry.getKey())))
                .map(Entry::getValue).flatMap(List::stream)
                .sorted(Comparator
                        .comparing((SubSceneSerieId serieId) -> serieId.getSeason() == 0)
                        .thenComparing(serieId -> {
                            Matcher matcher = yearPattern.matcher(serieId.getName());
                            if (matcher.find()) {
                                return Integer.parseInt(matcher.group());
                            }
                            return 0;
                        }, Comparator.reverseOrder())
                        .thenComparing((SubSceneSerieId serieId) -> serieId.getSeason(), Comparator.reverseOrder()))
                .distinct()
                .toList();
    }

    @Override
    public Set<Subtitle> convertToSubtitles(TvRelease tvRelease, Collection<SubsceneSubtitleDescriptor> subtitles, Language language) {
        return subtitles.stream()
                .filter(sub -> language == sub.getLanguage())
                .filter(sub -> sub.getName().contains(getSeasonEpisodeString(tvRelease.getSeason(), tvRelease.getEpisodeNumbers().get(0))))
                .map(sub -> Subtitle.downloadSource(sub.getUrlSupplier())
                        .subtitleSource(getSubtitleSource())
                        .fileName(StringUtil.removeIllegalFilenameChars(sub.getName()))
                        .language(sub.getLanguage())
                        .quality(ReleaseParser.getQualityKeyword(sub.getName()))
                        .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                        .releaseGroup(ReleaseParser.extractReleasegroup(sub.getName(), false))
                        .uploader(sub.getUploader())
                        .hearingImpaired(sub.isHearingImpaired()))
                .collect(Collectors.toSet());
    }

    private String getSeasonEpisodeString(int season, int episode) {
        return "S" + org.apache.commons.lang3.StringUtils.leftPad(String.valueOf(season), 2, "0") + "E"
                + org.apache.commons.lang3.StringUtils.leftPad(String.valueOf(episode), 2, "0");
    }

    @Override
    public boolean useSeasonForSerieId() {
        return true;
    }

    @Override
    public String providerSerieIdToDisplayString(SubSceneSerieId providerSerieId) {
        if (providerSerieId.getId().endsWith("-season")) {
            OptionalInt season = IntStream.rangeClosed(1, 100)
                    .filter(i -> providerSerieId.getId().endsWith("-%s-season".formatted(SubsceneApi.getOrdinalName(i).toLowerCase()))).findAny();
            if (season.isPresent()) {
                return "%s (%s %s)".formatted(providerSerieId.getName(), Messages.getString("App.Season"), season.getAsInt());
            }
        }
        return providerSerieId.getName();
    }
}
