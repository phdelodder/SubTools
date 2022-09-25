package org.lodder.subtools.sublibrary.data.omdb;

import java.util.Optional;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.omdb.exception.OmdbException;
import org.lodder.subtools.sublibrary.data.omdb.model.OmdbDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OmdbAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(OmdbAdapter.class);
    private final OmdbApi omdpApi;

    public OmdbAdapter(Manager manager) {
        this.omdpApi = new OmdbApi(manager);
    }

    public Optional<OmdbDetails> getMovieDetails(String imdbId) {
        try {
            return omdpApi.getMovieDetails(imdbId);
        } catch (OmdbException e) {
            LOGGER.error("API OMDB getMovieDetails for id [%s] (%s)".formatted(imdbId, e.getMessage()), e);
            return Optional.empty();
        }
    }

}
