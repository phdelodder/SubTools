package org.lodder.subtools.sublibrary.data.imdb;

import java.util.Collection;
import java.util.Comparator;
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
                throw new SubtitlesProviderInitException(getProviderName(), e);
            }
        });
        this.imdbSearchIdApi = new LazySupplier<>(() -> {
            try {
                return new ImdbSearchIdApi(manager);
            } catch (Exception e) {
                throw new SubtitlesProviderInitException(getProviderName(), e);
            }
        });
    }

    public String getProviderName() {
        return "IMDB";
    }

    public Optional<ImdbDetails> getMovieDetails(int imdbId) {
        return manager.valueBuilder()
                .cacheType(CacheType.DISK)
                .key("%s-MovieDetails:%s".formatted(getProviderName(), imdbId))
                .optionalSupplier(() -> {
                    try {
                        return imdbApi.get().getMovieDetails(imdbId);
                    } catch (ImdbException e) {
                        LOGGER.error("API %s getMovieDetails for id [%s] (%s)".formatted(getProviderName(), imdbId, e.getMessage()), e);
                        return Optional.empty();
                    }
                }).getOptional();
    }

    public OptionalInt getImdbId(String title, Integer year) {
        try {
            return manager.valueBuilder()
                    .cacheType(CacheType.DISK)
                    .key("%s-id-%s-%s".formatted(getProviderName(), title, year))
                    .optionalIntSupplier(() -> getImdbIdOnImdb(title, year)
                            .orElseMap(() -> getImdbIdOnGoogle(title, year))
                            .orElseMap(() -> getImdbIdOnYahoo(title, year))
                            .orElseMap(() -> promptUserToEnterImdbId(title, year)))
                    .storeTempNullValue().getOptionalInt();
        } catch (Exception e) {
            LOGGER.error("API %s getImdbId for title [%s] (%s)".formatted(getProviderName(), title, e.getMessage()), e);
            return OptionalInt.empty();
        }
    }

    private OptionalInt getImdbIdOnImdb(String title, Integer year) {
        return getImdbIdCommon(title, year, imdbSearchIdApi.get()::getImdbIdOnImdb);
    }

    private OptionalInt getImdbIdOnGoogle(String title, Integer year) {
        return getImdbIdCommon(title, year, imdbSearchIdApi.get()::getImdbIdOnGoogle);
    }

    private OptionalInt getImdbIdOnYahoo(String title, Integer year) {
        return getImdbIdCommon(title, year, imdbSearchIdApi.get()::getImdbIdOnYahoo);
    }

    private OptionalInt getImdbIdCommon(String title, Integer year,
            ThrowingBiFunction<String, Integer, Collection<ProviderSerieId>, ImdbSearchIdException> providerSerieIdSupplier) {
        Collection<ProviderSerieId> providerSerieIds;
        try {
            providerSerieIds = providerSerieIdSupplier.apply(title, year);
        } catch (ImdbSearchIdException e) {
            LOGGER.error("API %s getImdbId for title [%s] and year [%s] (%s)".formatted(getProviderName(), title, year, e.getMessage()), e);
            return OptionalInt.empty();
        }
        if (!userInteractionHandler.getSettings().isOptionsConfirmProviderMapping() && providerSerieIds.size() == 1) {
            // found single exact match
            return OptionalInt.of(Integer.parseInt(providerSerieIds.iterator().next().getId()));
        }
        String formattedTitle = title.replaceAll("[^A-Za-z]", "");
        return userInteractionHandler
                .selectFromList(
                        providerSerieIds.stream().sorted(Comparator
                                .comparing((ProviderSerieId providerSerieId) -> providerSerieId.getName().replaceAll("[^A-Za-z]", "")
                                        .equalsIgnoreCase(formattedTitle), Comparator.reverseOrder())
                                .thenComparing(ProviderSerieId::getName))
                                .toList(),
                        Messages.getString("Prompter.SelectImdbMatchForSerie").formatted(title),
                        getProviderName(),
                        ProviderSerieId::getName)
                .mapToInt(providerSerieId -> Integer.parseInt(providerSerieId.getId()));
    }

    private OptionalInt promptUserToEnterImdbId(String title, int year) {
        return userInteractionHandler.enter(getProviderName(), Messages.getString("Prompter.EnterImdbMatchForSerie").formatted(title),
                Messages.getString("Prompter.ValueIsNotValid"), StringUtils::isNumeric).mapToInt(Integer::parseInt);
    }

    public synchronized static ImdbAdapter getInstance(Manager manager, UserInteractionHandler userInteractionHandler) {
        if (instance == null) {
            instance = new ImdbAdapter(manager, userInteractionHandler);
        }
        return instance;
    }

}
