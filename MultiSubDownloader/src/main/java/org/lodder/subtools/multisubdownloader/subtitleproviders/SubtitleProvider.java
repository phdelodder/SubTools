package org.lodder.subtools.multisubdownloader.subtitleproviders;

import java.util.Set;

import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.slf4j.LoggerFactory;

public interface SubtitleProvider {

    Set<Subtitle> searchSubtitles(TvRelease tvRelease, Language language);

    Set<Subtitle> searchSubtitles(MovieRelease movieRelease, Language language);

    SubtitleSource getSubtitleSource();

    /**
     * @return The name of the SubtitleProvider
     */
    default String getName() {
        return getSubtitleSource().getName();
    }

    /**
     * Starts a search for subtitles
     *
     * @param release The release being searched for
     * @param language The language of the desired subtitles
     * @return The found subtitles
     */
    default Set<Subtitle> search(Release release, Language language) {
        try {
            if (release instanceof MovieRelease movieRelease) {
                return this.searchSubtitles(movieRelease, language);
            } else if (release instanceof TvRelease tvRelease) {
                return this.searchSubtitles(tvRelease, language);
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(SubtitleProvider.class).error("Error in %s API: %s".formatted(getName(), e.getMessage()), e);
        }
        return Set.of();
    }


    // Manager getManager();

    // boolean isConfirmProviderMapping();

    // default <T extends SerieMappingIntf, S extends ProviderSerieId> Optional<T> getProviderSerieId(String serieName, String displayName,
    // OptionalInt tvdbIdOptional, Function<String, List<S>> providerSerieIdsSupplier,
    // Class<T> returnType, UserInteractionHandler userInteractionHandler) {
    // Function<Integer, ValueBuilderIsPresentIntf> valueBuilderSupplier =
    // tvdbId -> getManager().valueBuilder().cacheType(CacheType.DISK)
    // .key("%s-serieName-tvdbId:%s".formatted(getSubtitleSource().name(), tvdbId));
    // if (tvdbIdOptional.isPresent() && valueBuilderSupplier.apply(tvdbIdOptional.getAsInt()).isPresent()) {
    // return valueBuilderSupplier.apply(tvdbIdOptional.getAsInt()).returnType(returnType).getOptional();
    // }
    // if (StringUtils.isBlank(serieName)) {
    // return Optional.empty();
    // }
    // ValueBuilderIsPresentIntf valueBuilder = getManager().valueBuilder()
    // .cacheType(CacheType.DISK)
    // .key("%s-serieName-name:%s".formatted(getSubtitleSource().name(), serieName.toLowerCase()));
    //
    // if (valueBuilder.isPresent()) {
    // Optional<T> value = valueBuilder.returnType(returnType).getOptional();
    // if (value.isPresent() && tvdbIdOptional.isPresent()) {
    // valueBuilderSupplier.apply(tvdbIdOptional.getAsInt()).value(value.get()).store();
    // }
    // return value;
    // } else {
    // List<S> providerSerieIds = providerSerieIdsSupplier.apply(serieName);
    //
    // if (providerSerieIds.isEmpty()) {
    // return Optional.empty(); // TODO add temporary 0
    // } else if (!isConfirmProviderMapping() && providerSerieIds.size() == 1) {
    // return Optional.of(new SerieMapping(serieName, providerSerieIds.get(0)));
    // }
    //
    // providerSerieIds = Stream
    // .concat(getApi().getUrisForSerieName(serieName).stream().sorted(USER_INTERACTION_SERIE_COMPARATOR.apply(serieName)),
    // Arrays.stream(UserInteractionExtraValue.values()).map(UriForServiceExtended::new))
    // .toList();
    // Optional<UriForSerie> uriForSerie = userInteractionHandler.selectFromList(providerSerieIds,
    // Messages.getString("SelectDialog.SelectSerieNameForName").formatted(displayName), serieName, UriForSerie::getName);
    //
    // if (uriForSerie.isEmpty()) {
    // return Optional.empty();
    // } else if (uriForSerie.get() instanceof UriForServiceExtended uriForServiceExtended) {
    // valueBuilder.value(null).timeToLiveSeconds(uriForServiceExtended.getTimeToLive()).store();
    // return Optional.empty();
    // } else {
    // return uriForSerie.map(urlForSerie -> new SerieMapping(serieName, urlForSerie.getUri(), urlForSerie.getName()))
    // .ifPresentDo(subsceneSerieName -> tvdbIdOptional
    // .ifPresent(tvdbId -> valueBuilderSupplier.apply(tvdbId).value(subsceneSerieName).store()));
    // }
    // }
    // }

}
