package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.SubsceneApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.exception.SubsceneException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.model.SubsceneSubtitleDescriptor;
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
import org.lodder.subtools.sublibrary.util.StringUtil;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;

@Getter
@ExtensionMethod({ OptionalExtension.class })
public class JSubsceneAdapter extends AbstractAdapter<SubsceneSubtitleDescriptor, ProviderSerieId, SubsceneException> {

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

        return getProviderSerieId(tvRelease.getOriginalName(), tvRelease.getDisplayName(), tvRelease.getSeason(), tvRelease.getTvdbId())
                .orElseMap(() -> getProviderSerieId(tvRelease.getName(), tvRelease.getDisplayName(), tvRelease.getSeason(), tvRelease.getTvdbId()))
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
    public List<ProviderSerieId> getSortedProviderSerieIds(String serieName, int season) throws SubsceneException {
        ToIntFunction<String> providerTypeFunction = value -> switch (value) {
            case "TV-Serie" -> 1;
            case "Exact" -> 2;
            case "Close" -> 3;
            default -> 4;
        };
        Pattern yearPattern = Pattern.compile("(\\d\\d\\d\\d)");
        Pattern seasonPattern = Pattern.compile(" - (.*?) Season");
        return getApi().getSubSceneSerieNames(serieName).entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> providerTypeFunction.applyAsInt(entry.getKey())))
                .map(Entry::getValue).flatMap(List::stream)
                .sorted(Comparator
                        .comparing((ProviderSerieId serieId) -> serieId.getName().contains(" - %s Season".formatted(getOrdinalName(season))),
                                Comparator.reverseOrder())
                        .thenComparing(serieId -> {
                            Matcher matcher = yearPattern.matcher(serieId.getName());
                            if (matcher.find()) {
                                return Integer.parseInt(matcher.group());
                            }
                            return 0;
                        }, Comparator.reverseOrder())
                        .thenComparing(serieId -> {
                            Matcher matcher = seasonPattern.matcher(serieId.getName());
                            if (matcher.find()) {
                                return OrdinalNumber.optionalFromValue(matcher.group(1)).mapToInt(OrdinalNumber::ordinal).orElse(0);
                            }
                            return 0;
                        }, Comparator.reverseOrder()))
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
    public String providerSerieIdToDisplayString(ProviderSerieId providerSerieId) {
        if (providerSerieId.getId().endsWith("-season")) {
            OptionalInt season = IntStream.rangeClosed(1, 100)
                    .filter(i -> providerSerieId.getId().endsWith("-%s-season".formatted(getOrdinalName(i).toLowerCase()))).findAny();
            if (season.isPresent()) {
                return "%s (%s %s)".formatted(providerSerieId.getName(), Messages.getString("Menu.Season"), season.getAsInt());
            }
        }
        return providerSerieId.getName();
    }

    @Getter
    @RequiredArgsConstructor
    private enum OrdinalNumber {
        ZEROTH("Zeroth"),
        FIRST("First"),
        SECOND("Second"),
        THIRD("Third"),
        FOURTH("Fourth"),
        FIFTH("Fifth"),
        SIXTH("Sixth"),
        SEVENTH("Seventh"),
        EIGHTH("Eighth"),
        NINTH("Ninth"),
        TENTH("Tenth"),
        ELEVENTH("Eleventh"),
        TWELFTH("Twelfth"),
        THIRTEENTH("Thirteenth"),
        FOURTEENTH("Fourteenth"),
        FIFTEENTH("Fifteenth"),
        SIXTEENTH("Sixteenth"),
        SEVENTEENTH("Seventeenth"),
        EIGHTEENTH("Eighteenth"),
        NINETEENTH("Nineteenth"),
        TWENTIETH("Twentieth"),
        TWENTY_FIRST("Twenty-First"),
        TWENTY_SECOND("Twenty-Second"),
        TWENTY_THIRD("Twenty-Third"),
        TWENTY_FOURTH("Twenty-Fourth"),
        TWENTY_FIFTH("Twenty-Fifth"),
        TWENTY_SIXTH("Twenty-Sixth"),
        TWENTY_SEVENTH("Twenty-Seventh"),
        TWENTY_EIGHTH("Twenty-Eighth"),
        TWENTY_NINTH("Twenty-Ninth"),
        THIRTIETH("Thirtieth"),
        THIRTHY_FIRST("Thirty-First"),
        THIRTHY_SECOND("Thirty-Second"),
        THIRTHY_THIRD("Thirty-Third"),
        THIRTHY_FOURTH("Thirty-Fourth"),
        THIRTHY_FIFTH("Thirty-Fifth"),
        THIRTHY_SIXTH("Thirty-Sixth"),
        THIRTHY_SEVENTH("Thirty-Seventh"),
        THIRTHY_EIGHTH("Thirty-Eighth"),
        THIRTHY_NINTH("Thirty-Ninth"),
        FORTIETH("Fortieth"),
        FORTY_FIRST("Forty-First"),
        FORTY_SECOND("Forty-Second"),
        FORTY_THIRD("Forty-Third"),
        FORTY_FOURTH("Forty-Fourth"),
        FORTY_FIFTH("Forty-Fifth"),
        FORTY_SIXTH("Forty-Sixth"),
        FORTY_SEVENTH("Forty-Seventh"),
        FORTY_EIGHTH("Forty-Eighth"),
        FORTY_NINTH("Forty-Ninth"),
        FIFTIETH("Fiftieth"),
        FIFTY_FIRST("Fifty-First"),
        FIFTY_SECOND("Fifty-Second"),
        FIFTY_THIRD("Fifty-Third"),
        FIFTY_FOURTH("Fifty-Fourth"),
        FIFTY_FIFTH("Fifty-Fifth"),
        FIFTY_SIXTH("Fifty-Sixth"),
        FIFTY_SEVENTH("Fifty-Seventh"),
        FIFTY_EIGHTH("Fifty-Eighth"),
        FIFTY_NINTH("Fifty-Ninth"),
        SIXTIETH("Sixtieth"),
        SIXTY_FIRST("Sixty-First"),
        SIXTY_SECOND("Sixty-Second"),
        SIXTY_THIRD("Sixty-Third"),
        SIXTY_FOURTH("Sixty-Fourth"),
        SIXTY_FIFTH("Sixty-Fifth"),
        SIXTY_SIXTH("Sixty-Sixth"),
        SIXTY_SEVENTH("Sixty-Seventh"),
        SIXTY_EIGHTH("Sixty-Eighth"),
        SIXTY_NINTH("Sixty-Ninth"),
        SEVENTIETH("Seventieth"),
        SEVENTY_FIRST("Seventy-First"),
        SEVENTY_SECOND("Seventy-Second"),
        SEVENTY_THIRD("Seventy-Third"),
        SEVENTY_FOURTH("Seventy-Fourth"),
        SEVENTY_FIFTH("Seventy-Fifth"),
        SEVENTY_SIXTH("Seventy-Sixth"),
        SEVENTY_SEVENTH("Seventy-Seventh"),
        SEVENTY_EIGHTH("Seventy-Eighth"),
        SEVENTY_NINTH("Seventy-Ninth"),
        EIGHTIETH("Eightieth"),
        EIGHTY_FIRST("Eighty-First"),
        EIGHTY_SECOND("Eighty-Second"),
        EIGHTY_THIRD("Eighty-Third"),
        EIGHTY_FOURTH("Eighty-Fourth"),
        EIGHTY_FIFTH("Eighty-Fifth"),
        EIGHTY_SIXTH("Eighty-Sixth"),
        EIGHTY_SEVENTH("Eighty-Seventh"),
        EIGHTY_EIGHTH("Eighty-Eighth"),
        EIGHTY_NINTH("Eighty-Ninth"),
        NINETIETH("Ninetieth"),
        NINETY_FIRST("Ninety-First"),
        NINETY_SECOND("Ninety-Second"),
        NINETY_THIRD("Ninety-Third"),
        NINETY_FOURTH("Ninety-Fourth"),
        NINETY_FIFTH("Ninety-Fifth"),
        NINETY_SIXTH("Ninety-Sixth"),
        NINETY_SEVENTH("Ninety-Seventh"),
        NINETY_EIGHTH("Ninety-Eighth"),
        NINETY_NINTH("Ninety-Ninth"),
        HUNDREDTH("Hundredth");

        private final String value;

        public static Optional<OrdinalNumber> optionalFromValue(String value) {
            return Stream.of(OrdinalNumber.values()).filter(ordinalNumber -> StringUtils.equalsIgnoreCase(value, ordinalNumber.getValue()))
                    .findAny();
        }
    }

    private String getOrdinalName(int ordinal) {
        if (ordinal < 0 || ordinal > 100) {
            return "not defined";
        }
        return OrdinalNumber.values()[ordinal].getValue();
    }
}
