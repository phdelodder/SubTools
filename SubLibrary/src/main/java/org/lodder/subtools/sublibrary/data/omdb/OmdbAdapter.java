package org.lodder.subtools.sublibrary.data.omdb;

import java.util.Optional;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.UserInteractionHandler;
import org.lodder.subtools.sublibrary.data.omdb.exception.OmdbException;
import org.lodder.subtools.sublibrary.data.omdb.model.OmdbDetails;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OmdbAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(OmdbAdapter.class);
    private static OmdbAdapter instance;
    private final LazySupplier<OmdbApi> omdpApi;

    private OmdbAdapter(Manager manager, UserInteractionHandler userInteractionHandler) {
        omdpApi = new LazySupplier<>(() -> {
            try {
                return new OmdbApi(manager, userInteractionHandler);
            } catch (Exception e) {
                LOGGER.error("API OMDB INIT (%s)".formatted(e.getMessage()), e);
            }
            return null;
        });
    }

    public Optional<OmdbDetails> getMovieDetails(String imdbId) {
        try {
            return omdpApi.get().getMovieDetails(imdbId);
        } catch (OmdbException e) {
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
