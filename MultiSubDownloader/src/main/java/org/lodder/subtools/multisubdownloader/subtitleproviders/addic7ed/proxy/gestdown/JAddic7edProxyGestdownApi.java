package org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.proxy.gestdown;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gestdown.api.SubtitlesApi;
import org.gestdown.api.TvShowsApi;
import org.gestdown.invoker.ApiException;
import org.gestdown.model.EpisodeDto;
import org.gestdown.model.ShowSearchRequest;
import org.gestdown.model.SubtitleDto;
import org.gestdown.model.SubtitleSearchResponse;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleApi;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.data.Html;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.lodder.subtools.sublibrary.util.StringUtil;

import lombok.experimental.ExtensionMethod;

// see https://www.gestdown.info/Api
@ExtensionMethod({ OptionalExtension.class })
public class JAddic7edProxyGestdownApi extends Html implements SubtitleApi {

    private static final String DOMAIN = "https://api.gestdown.info";
    private final TvShowsApi tvShowsApi;
    private final SubtitlesApi subtitlesApi;

    public JAddic7edProxyGestdownApi(Manager manager) {
        super(manager);
        tvShowsApi = new TvShowsApi();
        subtitlesApi = new SubtitlesApi();
    }

    // public static record Addic7edSerieName(String serieName) implements Serializable {}

    public List<ProviderSerieId> getProviderSerieName(String serieName) throws ApiException {
        try {
            return tvShowsApi.showsSearchPost(new ShowSearchRequest().query(serieName)).getShows().stream()
                    .map(showDto -> new ProviderSerieId(serieName, showDto.getName())).toList();
        } catch (Exception e) {
            throw new ApiException(e);
        }
    }

    // public Optional<Addic7edSerieName> getAddic7edSerieName(String serieName, OptionalInt tvdbIdOptional,
    // ThrowingFunction<List<String>, Optional<String>, ApiException> multipleResultHandler) throws ApiException {
    // Function<Integer, ValueBuilderIsPresentIntf> valueBuilderSupplier = tvdbId -> getManager().valueBuilder().cacheType(CacheType.DISK)
    // .key("%s-serieName-tvdbId:%s".formatted("GESTDOWN", tvdbId));
    //
    // if (tvdbIdOptional.isPresent() && valueBuilderSupplier.apply(tvdbIdOptional.getAsInt()).isPresent()) {
    // return valueBuilderSupplier.apply(tvdbIdOptional.getAsInt()).returnType(Addic7edSerieName.class).getOptional();
    // }
    // if (StringUtils.isBlank(serieName)) {
    // return Optional.empty();
    // }
    // return getManager().valueBuilder()
    // .cacheType(CacheType.DISK)
    // .key("%s-serieName-name:%s".formatted("GESTDOWN", serieName.toLowerCase()))
    // .optionalSupplier(() -> {
    // try {
    // return multipleResultHandler.apply(tvShowsApi.showsSearchPost(new ShowSearchRequest().query(serieName)).getShows().stream()
    // .map(ShowDto::getName).toList()).map(selectedValue -> new SerieMapping(serieName, selectedValue));
    // } catch (Exception e) {
    // throw new ApiException(e);
    // }
    // })
    // .getOptional().map(SerieMapping::getMappedName).map(Addic7edSerieName::new)
    // .ifPresentDo(addic7edSerieName -> tvdbIdOptional
    // .ifPresent(tvdbId -> valueBuilderSupplier.apply(tvdbId).value(addic7edSerieName).store()));
    // }

    // @ToString
    // public static class SerieMapping extends SerieMapping {
    // private static final long serialVersionUID = 537382757186290560L;
    // @Getter
    // private final String mappedName;
    //
    // public SerieMapping(String name, String mappedName) {
    // super(name);
    // this.mappedName = mappedName;
    // }
    //
    // @Override
    // public String getMappingValue() {
    // return mappedName;
    // }
    // }

    public Set<Subtitle> getSubtitles(SerieMapping providerSerieId, int season, int episode, Language language) throws ApiException {
        Set<Subtitle> results = new HashSet<>();
        SubtitleSearchResponse response = getManager().valueBuilder()
                .cacheType(CacheType.MEMORY)
                .key("%s-subtitles-%s-%s-%s-%s".formatted("GESTDOWN", getSubtitleSource().name(), providerSerieId.getProviderName().toLowerCase(),
                        season, episode, language))
                .valueSupplier(() -> subtitlesApi.subtitlesFindLanguageShowSeasonEpisodeGet(language.getName(), providerSerieId.getProviderId(),
                        season, episode))
                .get();
        response.getMatchingSubtitles().stream()
                .filter(SubtitleDto::isCompleted).map(sub -> mapToSubtitle(sub, response.getEpisode(), language))
                .forEach(results::add);
        return results;
    }

    private Subtitle mapToSubtitle(SubtitleDto sub, EpisodeDto episodedto, Language language) {
        return Subtitle.downloadSource(getDownloadUrl(sub.getDownloadUri()))
                .subtitleSource(getSubtitleSource())
                .fileName(StringUtil
                        .removeIllegalFilenameChars("%s - %s - %s".formatted(episodedto.getShow(), episodedto.getTitle(), sub.getVersion())))
                .language(language)
                .quality(ReleaseParser.getQualityKeyword(episodedto.getTitle() + " " + sub.getVersion()))
                .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                .releaseGroup(sub.getVersion())
                .uploader("")
                .hearingImpaired(false);
    }

    public String getDownloadUrl(String subtitleId) {
        return DOMAIN + subtitleId;
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.ADDIC7ED;
    }
}
