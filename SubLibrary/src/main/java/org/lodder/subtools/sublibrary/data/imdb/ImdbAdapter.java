package org.lodder.subtools.sublibrary.data.imdb;

import java.util.Optional;
import java.util.OptionalInt;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.imdb.exception.ImdbException;
import org.lodder.subtools.sublibrary.data.imdb.exception.ImdbSearchIdException;
import org.lodder.subtools.sublibrary.data.imdb.model.ImdbDetails;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImdbAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImdbAdapter.class);
    private static ImdbAdapter instance;
    private final LazySupplier<ImdbApi> imdbApi;
    private final LazySupplier<ImdbSearchIdApi> imdbSearchIdApi;

    private ImdbAdapter(Manager manager) {
        this.imdbApi = new LazySupplier<>(() -> {
            try {
                return new ImdbApi(manager);
            } catch (Exception e) {
                LOGGER.error("API IMDB INIT (%s)".formatted(e.getMessage()), e);
            }
            return null;
        });
        this.imdbSearchIdApi = new LazySupplier<>(() -> {
            try {
                return new ImdbSearchIdApi(manager);
            } catch (Exception e) {
                LOGGER.error("API IMDB INIT (%s)".formatted(e.getMessage()), e);
            }
            return null;
        });
    }

    public Optional<ImdbDetails> getMovieDetails(String imdbId) {
        try {
            return imdbApi.get().getMovieDetails(imdbId);
        } catch (ImdbException e) {
            LOGGER.error("API IMDB getMovieDetails for id [%s] (%s)".formatted(imdbId, e.getMessage()), e);
            return Optional.empty();
        }
    }

    public OptionalInt getImdbId(String title, int year) {
        try {
            return imdbSearchIdApi.get().getImdbId(title, year);
        } catch (ImdbSearchIdException e) {
            LOGGER.error("API IMDB getImdbId for title [%s] (%s)".formatted(title, e.getMessage()), e);
            return OptionalInt.empty();
        }
    }

    public synchronized static ImdbAdapter getInstance(Manager manager) {
        if (instance == null) {
            instance = new ImdbAdapter(manager);
        }
        return instance;
    }

}
