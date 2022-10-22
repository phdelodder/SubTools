package org.lodder.subtools.sublibrary.data.imdb;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.data.imdb.exception.ImdbException;
import org.lodder.subtools.sublibrary.data.imdb.exception.ImdbSearchIdException;
import org.lodder.subtools.sublibrary.data.imdb.model.ImdbDetails;
import org.lodder.subtools.sublibrary.exception.SubtitlesProviderInitException;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pivovarit.function.ThrowingBiFunction;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
public class ImdbAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImdbAdapter.class);
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
                throw new SubtitlesProviderInitException("IMDB", e);
            }
        });
        this.imdbSearchIdApi = new LazySupplier<>(() -> {
            try {
                return new ImdbSearchIdApi(manager);
            } catch (Exception e) {
                throw new SubtitlesProviderInitException("IMDB", e);
            }
        });
    }

    public Optional<ImdbDetails> getMovieDetails(int imdbId) {
        return manager.valueBuilder()
                .cacheType(CacheType.DISK)
                .key("IMDB-MovieDetails:" + imdbId)
                .optionalSupplier(() -> {
                    try {
                        return imdbApi.get().getMovieDetails(imdbId);
                    } catch (ImdbException e) {
                        LOGGER.error("API IMDB getMovieDetails for id [%s] (%s)".formatted(imdbId, e.getMessage()), e);
                        return Optional.empty();
                    }
                }).getOptional();
    }

    public OptionalInt getImdbId(String title, Integer year) {
        try {
            return getImdbIdOnImdb(title, year)
                    .orElseMap(() -> getImdbIdOnGoogle(title, year))
                    .orElseMap(() -> getImdbIdOnYahoo(title, year))
                    .orElseMap(() -> promtUserToEnterImdbId(title, year));
        } catch (ImdbSearchIdException e) {
            LOGGER.error("API IMDB getImdbId for title [%s] (%s)".formatted(title, e.getMessage()), e);
            return OptionalInt.empty();
        }
    }

    private OptionalInt getImdbIdOnImdb(String title, Integer year) throws ImdbSearchIdException {
        return getImdbIdCommon(title, year, imdbSearchIdApi.get()::getImdbIdOnImdb);
    }

    private OptionalInt getImdbIdOnGoogle(String title, Integer year) throws ImdbSearchIdException {
        return getImdbIdCommon(title, year, imdbSearchIdApi.get()::getImdbIdOnGoogle);
    }

    private OptionalInt getImdbIdOnYahoo(String title, Integer year) throws ImdbSearchIdException {
        return getImdbIdCommon(title, year, imdbSearchIdApi.get()::getImdbIdOnYahoo);
    }

    private OptionalInt getImdbIdCommon(String title, Integer year,
            ThrowingBiFunction<String, Integer, List<ProviderSerieId>, ImdbSearchIdException> providerSerieIdSupplier) {
        return manager.valueBuilder()
                .cacheType(CacheType.DISK)
                .key("IMDB-id-%s-%s".formatted(title, year))
                .optionalIntSupplier(() -> {
                    List<ProviderSerieId> providerSerieIds;
                    try {
                        providerSerieIds = providerSerieIdSupplier.apply(title, year);
                    } catch (ImdbSearchIdException e) {
                        LOGGER.error("API IMDB getImdbId for title [%s] and year [%s] (%s)".formatted(title, year, e.getMessage()), e);
                        return OptionalInt.empty();
                    }
                    if (!userInteractionHandler.getSettings().isOptionsConfirmProviderMapping() && providerSerieIds.size() == 1) {
                        // found single exact match
                        return OptionalInt.of(Integer.parseInt(providerSerieIds.get(0).getId()));
                    }
                    String formattedTitle = title.replaceAll("[^A-Za-z]", "");
                    return userInteractionHandler
                            .selectFromList(
                                    providerSerieIds.stream().sorted(Comparator
                                            .comparing((ProviderSerieId providerSerieId) -> providerSerieId.getName().replaceAll("[^A-Za-z]", "")
                                                    .equalsIgnoreCase(formattedTitle), Comparator.reverseOrder())
                                            .thenComparing(ProviderSerieId::getName))
                                            .toList(),
                                    Messages.getString("SelectImdbMatchForSerie").formatted(title),
                                    "IMDB",
                                    ProviderSerieId::getName)
                            .mapToInt(providerSerieId -> Integer.parseInt(providerSerieId.getId()));
                }).storeTempNullValue().getOptionalInt();
    }

    private OptionalInt promtUserToEnterImdbId(String title, int year) {
        return userInteractionHandler.enter("IMDB", Messages.getString("Prompter.EnterImdbMatchForSerie").formatted(title),
                Messages.getString("Prompter.ValueIsNotValid"), StringUtils::isNumeric).mapToInt(Integer::parseInt);
    }

    public synchronized static ImdbAdapter getInstance(Manager manager, UserInteractionHandler userInteractionHandler) {
        if (instance == null) {
            instance = new ImdbAdapter(manager, userInteractionHandler);
        }
        return instance;
    }

}
