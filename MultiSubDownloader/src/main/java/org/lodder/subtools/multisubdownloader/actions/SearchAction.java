package org.lodder.subtools.multisubdownloader.actions;

import java.util.List;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.exceptions.SearchSetupException;
import org.lodder.subtools.multisubdownloader.gui.dialog.Cancelable;
import org.lodder.subtools.multisubdownloader.listeners.IndexingProgressListener;
import org.lodder.subtools.multisubdownloader.listeners.SearchProgressListener;
import org.lodder.subtools.multisubdownloader.listeners.StatusListener;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.multisubdownloader.workers.SearchHandler;
import org.lodder.subtools.multisubdownloader.workers.SearchManager;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.model.Release;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter(value = AccessLevel.PROTECTED)
public abstract class SearchAction implements Runnable, Cancelable, SearchHandler {

    private final @NonNull Settings settings;
    private final @NonNull SubtitleProviderStore subtitleProviderStore;
    private StatusListener statusListener;
    private SearchManager searchManager;
    private List<Release> releases;

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchAction.class);

    @Override
    public void run() {
        LOGGER.trace("SearchAction is being executed");
        try {
            this.search();
        } catch (ActionException e) {
            LOGGER.trace(e.getMessage(), e);
            if (this.statusListener != null) {
                this.statusListener.onError(e);
            }
        }
    }

    private void search() throws ActionException {
        this.statusListener = this.getIndexingProgressListener();
        this.getIndexingProgressListener().reset();
        this.getSearchProgressListener().reset();

        validate();

        Language language = this.getLanguage();

        setStatusMessage(Messages.getString("SearchAction.StatusIndexing"));

        this.releases = createReleases();

        if (Thread.currentThread().isInterrupted()) {
            return;
        }

        if (this.releases.size() <= 0) {
            this.cancel(true);
            return;
        }

        this.getIndexingProgressListener().completed();

        this.statusListener = this.getSearchProgressListener();

        /* Create a new SearchManager. */
        this.searchManager =
                SearchManager.createWithSettings(this.settings)
                        /* Tell the manager which language we want */
                        .language(language)
                        /* Tell the manager where to push progressUpdates */
                        .progressListener(getSearchProgressListener())
                        /* Tell the manager how to handle user interactions */
                        .userInteractionHandler(getUserInteractionHandler())
                        /* Listen for when the manager tells us Subtitles are found */
                        .onFound(this);

        /* Tell the manager which providers to use */
        this.subtitleProviderStore.getAllProviders().stream()
                .filter(subtitleProvider -> settings.isSerieSource(subtitleProvider.getSubtitleSource()))
                .forEach(searchManager::addProvider);

        /* Tell the manager which releases to search. */
        this.releases.forEach(searchManager::addRelease);

        setStatusMessage(Messages.getString("SearchAction.StatusSearching"));

        /* Tell the manager to start searching */
        this.searchManager.start();
    }

    protected abstract void validate() throws SearchSetupException;

    protected abstract List<Release> createReleases() throws ActionException;

    protected void setStatusMessage(String message) {
        this.statusListener.onStatus(message);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (searchManager != null) {
            this.searchManager.cancel(mayInterruptIfRunning);
        }
        Thread.currentThread().interrupt();
        this.getIndexingProgressListener().completed();
        this.getSearchProgressListener().completed();
        return true;
    }

    protected abstract Language getLanguage();

    protected abstract UserInteractionHandler getUserInteractionHandler();

    protected abstract IndexingProgressListener getIndexingProgressListener();

    protected abstract SearchProgressListener getSearchProgressListener();

}
