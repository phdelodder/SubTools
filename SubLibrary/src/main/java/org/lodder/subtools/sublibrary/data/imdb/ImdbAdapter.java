package org.lodder.subtools.sublibrary.data.imdb;

import java.util.Optional;
import java.util.OptionalInt;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.imdb.exception.ImdbException;
import org.lodder.subtools.sublibrary.data.imdb.exception.ImdbSearchIdException;
import org.lodder.subtools.sublibrary.data.imdb.model.ImdbDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImdbAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImdbAdapter.class);
    private final ImdbApi imdbApi;
    private final ImdbSearchIdApi imdbSearchIdApi;

    public ImdbAdapter(Manager manager) {
        this.imdbApi = new ImdbApi(manager);
        this.imdbSearchIdApi = new ImdbSearchIdApi(manager);
    }

    public Optional<ImdbDetails> getMovieDetails(String imdbId) {
        try {
            return imdbApi.getMovieDetails(imdbId);
        } catch (ImdbException e) {
            LOGGER.error("API IMDB getMovieDetails for id [%s] (%s)".formatted(imdbId, e.getMessage()), e);
            return Optional.empty();
        }
    }

    public OptionalInt getImdbId(String title, int year) {
        try {
            return imdbSearchIdApi.getImdbId(title, year);
        } catch (ImdbSearchIdException e) {
            LOGGER.error("API IMDB getImdbId for title [%s] (%s)".formatted(title, e.getMessage()), e);
            return OptionalInt.empty();
        }
    }

}
