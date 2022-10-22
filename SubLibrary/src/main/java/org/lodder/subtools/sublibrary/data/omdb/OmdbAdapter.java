package org.lodder.subtools.sublibrary.data.omdb;

import java.util.Optional;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.omdb.model.OmdbDetails;
import org.lodder.subtools.sublibrary.exception.SubtitlesProviderInitException;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AccessLevel;
import lombok.Getter;

@Getter(value = AccessLevel.PROTECTED)
public class OmdbAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(OmdbAdapter.class);
    private static OmdbAdapter instance;
    private final Manager manager;
    private final LazySupplier<OmdbApi> omdpApi;

    private OmdbAdapter(Manager manager, UserInteractionHandler userInteractionHandler) {
        this.manager = manager;
        this.omdpApi = new LazySupplier<>(() -> {
            try {
                return new OmdbApi(manager);
            } catch (Exception e) {
                throw new SubtitlesProviderInitException("IMDB", e);
            }
        });
    }

    private OmdbApi getApi() {
        return omdpApi.get();
    }

    public Optional<OmdbDetails> getMovieDetails(int imdbId) {
        try {
            return getManager().valueBuilder()
                    .cacheType(CacheType.DISK)
                    .key("OMDB-movieDetails-" + imdbId)
                    .optionalSupplier(() -> getApi().getMovieDetails(imdbId))
                    .storeTempNullValue()
                    .getOptional();
        } catch (Exception e) {
            LOGGER.error("API OMDB getMovieDetails for id [%s] (%s)".formatted(imdbId, e.getMessage()), e);
            return Optional.empty();
        }
    }

    public synchronized static OmdbAdapter getInstance(Manager manager, UserInteractionHandler userInteractionHandler) {
        if (instance == null) {
            instance = new OmdbAdapter(manager, userInteractionHandler);
        }
        return instance;
    }

}
