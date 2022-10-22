package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.JTVSubtitlesApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.exception.TvSubtiltesException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.model.TVsubtitlesSubtitleDescriptor;
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
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.experimental.ExtensionMethod;

@Getter
@ExtensionMethod({ OptionalExtension.class })
public class JTVsubtitlesAdapter extends AbstractAdapter<TVsubtitlesSubtitleDescriptor, ProviderSerieId, TvSubtiltesException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JTVsubtitlesAdapter.class);
    private static LazySupplier<JTVSubtitlesApi> jtvapi;

    public JTVsubtitlesAdapter(Manager manager, UserInteractionHandler userInteractionHandler) {
        super(manager, userInteractionHandler);
        if (jtvapi == null) {
            jtvapi = new LazySupplier<>(() -> {
                try {
                    return new JTVSubtitlesApi(manager);
                } catch (Exception e) {
                    throw new SubtitlesProviderInitException(getProviderName(), e);
                }
            });
        }
    }

    private JTVSubtitlesApi getApi() {
        return jtvapi.get();
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.TVSUBTITLES;
    }

    @Override
    public String getProviderName() {
        return getSubtitleSource().name();
    }

    @Override
    public List<TVsubtitlesSubtitleDescriptor> searchMovieSubtitlesWithHash(String hash, Language language) throws TvSubtiltesException {
        // TODO implement this
        return List.of();
    }

    @Override
    public List<TVsubtitlesSubtitleDescriptor> searchMovieSubtitlesWithId(int tvdbId, Language language) throws TvSubtiltesException {
        // TODO implement this
        return List.of();
    }

    @Override
    public List<TVsubtitlesSubtitleDescriptor> searchMovieSubtitlesWithName(String name, int year, Language language)
            throws TvSubtiltesException {
        // TODO implement this
        return List.of();
    }

    @Override
    public Set<Subtitle> convertToSubtitles(MovieRelease movieRelease, Set<TVsubtitlesSubtitleDescriptor> subtitles, Language language) {
        // TODO implement this
        return Set.of();
    }

    @Override
    public Set<TVsubtitlesSubtitleDescriptor> searchSerieSubtitles(TvRelease tvRelease, Language language) throws TvSubtiltesException {
        return getProviderSerieId(tvRelease.getOriginalName(), tvRelease.getDisplayName(), tvRelease.getSeason(), tvRelease.getTvdbId())
                .orElseMap(() -> getProviderSerieId(tvRelease.getName(), tvRelease.getDisplayName(), tvRelease.getSeason(), tvRelease.getTvdbId()))
                .map(providerSerieId -> tvRelease.getEpisodeNumbers().stream()
                        .flatMap(episode -> {
                            try {
                                return getApi().getSubtitles(providerSerieId, tvRelease.getSeason(), episode, language).stream();
                            } catch (TvSubtiltesException e) {
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
    public Set<Subtitle> convertToSubtitles(TvRelease tvRelease, Collection<TVsubtitlesSubtitleDescriptor> subtitles, Language language) {
        return subtitles.stream()
                .map(sub -> Subtitle.downloadSource(sub.getUrl())
                        .subtitleSource(getSubtitleSource())
                        .fileName(sub.getFilename())
                        .language(language)
                        .quality(ReleaseParser.getQualityKeyword(sub.getFilename() + " " + sub.getRip()))
                        .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                        .releaseGroup(ReleaseParser.extractReleasegroup(sub.getFilename(), FilenameUtils.isExtension(sub.getFilename(), "srt")))
                        .uploader(sub.getAuthor())
                        .hearingImpaired(false))
                .collect(Collectors.toSet());
    }

    @Override
    public List<ProviderSerieId> getSortedProviderSerieIds(String serieName, int season) throws TvSubtiltesException {
        Pattern yearPatter = Pattern.compile("\\((\\d\\d\\d\\d)-(\\d\\d\\d\\d)\\)");
        return getApi().getUrisForSerieName(serieName).stream()
                .sorted(Comparator.comparing(
                        (ProviderSerieId n) -> !serieName.replaceAll("[^A-Za-z]", "").equalsIgnoreCase(n.getName().replaceAll("[^A-Za-z]", "")))
                        .thenComparing(Comparator.comparing((ProviderSerieId providerSerieId) -> {
                            Matcher matcher = yearPatter.matcher(providerSerieId.getName());
                            if (matcher.find()) {
                                return Integer.parseInt(matcher.group(2));
                            }
                            return 0;
                        }, Comparator.reverseOrder())))
                .toList();
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
