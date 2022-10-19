package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * @param <T> type of the subtitle objects returned by the api
 * @param <X> type of the exception thrown by the api
 */
@Getter
@RequiredArgsConstructor
abstract class AbstractAdapter<T, S extends ProviderSerieId, X extends Exception> implements Adapter<T, S, X>, SubtitleProvider
{
    Logger LOGGER = LoggerFactory.getLogger(AbstractAdapter.class);
    private final Manager manager;
    private final UserInteractionHandler userInteractionHandler;

}
