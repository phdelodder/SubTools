package org.lodder.subtools.sublibrary.data.imdb;

import static org.lodder.subtools.multisubdownloader.Messages.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.OptionalInt;

import com.pivovarit.function.ThrowingBiFunction;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.data.imdb.exception.ImdbException;
import org.lodder.subtools.sublibrary.data.imdb.exception.ImdbSearchIdException;
import org.lodder.subtools.sublibrary.data.imdb.model.ImdbDetails;
import org.lodder.subtools.sublibrary.exception.SubtitlesProviderInitException;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImdbAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImdbAdapter.class);
    private static final String PROVIDER_NAME = "IMDB";
    private static ImdbAdapter instance;
    private final Manager manager;
    private final UserInteractionHandler userInteractionHandler;
    private final LazySupplier<ImdbApi> imdbApi;
    private final LazySupplier<ImdbSearchIdApi> imdbSearchIdApi;

    private ImdbAdapter(Manager manager, UserInteractionHandler userInteractionHandler) {
        this.manager = manager;
        this.userInteractionHandler = userInteractionHandler;
        this.imdbApi = new LazySupplier<>(() -> {
            try {
                return new ImdbApi(manager);
            } catch (Exception e) {
                throw new SubtitlesProviderInitException(PROVIDER_NAME, e);
            }
        });
        this.imdbSearchIdApi = new LazySupplier<>(() -> {
            try {
                return new ImdbSearchIdApi(manager);
            } catch (Exception e) {
                throw new SubtitlesProviderInitException(PROVIDER_NAME, e);
            }
        });
    }

    public Optional<ImdbDetails> getMovieDetails(int imdbId) {
        return manager.getCache(CacheType.DISK, "%s-MovieDetails:%s".formatted(PROVIDER_NAME, imdbId))
            .getOptional(() -> {
                try {
                    return imdbApi.get().getMovieDetails(imdbId);
                } catch (ImdbException e) {
                    LOGGER.error("API %s getMovieDetails for id [%s] (%s)".formatted(PROVIDER_NAME, imdbId,
                        e.getMessage()), e);
                    return Optional.empty();
                }
            });
    }

    public OptionalInt getImdbId(String title, @Nullable Integer year) {
        try {
            return manager.getCache(CacheType.DISK, "%s-id-%s-%s".formatted(PROVIDER_NAME, title, year))
                .getOptionalInt(
                    () -> getImdbIdOnImdb(title, year)
                        .orElseMap(() -> getImdbIdOnGoogle(title, year))
                        .orElseMap(() -> getImdbIdOnYahoo(title, year))
                        .orElseMap(() -> promptUserToEnterImdbId(title)),
                    storeTempNullValue:true);
        } catch (Exception e) {
            LOGGER.error("API %s getImdbId for title [%s] (%s)".formatted(PROVIDER_NAME, title, e.getMessage()), e);
            return OptionalInt.empty();
        }
    }

    private OptionalInt getImdbIdOnImdb(String title, @Nullable Integer year) {
        return getImdbIdCommon(title, year, imdbSearchIdApi.get()::getImdbIdOnImdb);
    }

    private OptionalInt getImdbIdOnGoogle(String title, @Nullable Integer year) {
        return getImdbIdCommon(title, year, imdbSearchIdApi.get()::getImdbIdOnGoogle);
    }

    private OptionalInt getImdbIdOnYahoo(String title, @Nullable Integer year) {
        return getImdbIdCommon(title, year, imdbSearchIdApi.get()::getImdbIdOnYahoo);
    }

    private OptionalInt getImdbIdCommon(String title, @Nullable Integer year,
        ThrowingBiFunction<String, Integer, Collection<ProviderSerieId>, ImdbSearchIdException> providerSerieIdSupplier) {
        Collection<ProviderSerieId> providerSerieIds;
        try {
            providerSerieIds = providerSerieIdSupplier.apply(title, year);
        } catch (ImdbSearchIdException e) {
            LOGGER.error("API %s getImdbId for title [%s] and year [%s] (%s)".formatted(PROVIDER_NAME, title, year,
                e.getMessage()), e);
            return OptionalInt.empty();
        }
        if (!userInteractionHandler.settings.optionsConfirmProviderMapping && providerSerieIds.size() == 1) {
            // found single exact match
            return OptionalInt.of(Integer.parseInt(providerSerieIds.iterator().next().id));
        }
        String formattedTitle = title.replaceAll("[^A-Za-z]", "");
        return userInteractionHandler
            .selectFromList(
                providerSerieIds.stream().sorted(Comparator
                        .comparing((ProviderSerieId providerSerieId) -> providerSerieId.name.replaceAll(
                                "[^A-Za-z]", "")
                            .equalsIgnoreCase(formattedTitle), Comparator.reverseOrder())
                        .thenComparing(ProviderSerieId::getName))
                    .toList(),
                getText("Prompter.SelectImdbMatchForSerie", title),
                PROVIDER_NAME,
                ProviderSerieId::getName)
            .mapToInt(providerSerieId -> Integer.parseInt(providerSerieId.id));
    }

    private OptionalInt promptUserToEnterImdbId(String title) {
        return userInteractionHandler.enterNumber(PROVIDER_NAME, getText("Prompter.EnterImdbIdForSerie", title));
    }

    public static synchronized ImdbAdapter getInstance(Manager manager, UserInteractionHandler userInteractionHandler) {
        if (instance == null) {
            instance = new ImdbAdapter(manager, userInteractionHandler);
        }
        return instance;
    }

}
