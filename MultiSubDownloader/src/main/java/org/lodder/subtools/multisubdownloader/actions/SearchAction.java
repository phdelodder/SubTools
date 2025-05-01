package org.lodder.subtools.multisubdownloader.actions;

import static manifold.ext.props.rt.api.PropOption.*;

import java.util.List;

import manifold.ext.props.rt.api.get;
import manifold.ext.props.rt.api.set;
import manifold.ext.props.rt.api.val;
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

public abstract class SearchAction implements Runnable, Cancelable, SearchHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchAction.class);

    @val(Protected) Settings settings;
    @val(Protected) SubtitleProviderStore subtitleProviderStore;

    @get(Protected) @set(Private) StatusListener statusListener;
    @get(Protected) @set(Private) SearchManager searchManager;
    @get(Protected) @set(Private) List<Release> releases;
    @get(Protected) abstract Language language;
    abstract @get(Protected) IndexingProgressListener indexingProgressListener;
    abstract @get(Protected) UserInteractionHandler userInteractionHandler;
    abstract @get(Protected) SearchProgressListener searchProgressListener;

    protected SearchAction(Settings settings, SubtitleProviderStore subtitleProviderStore) {
        this.settings = settings;
        this.subtitleProviderStore = subtitleProviderStore;
    }

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
        this.statusListener = this.indexingProgressListener;
        this.indexingProgressListener.reset();
        this.searchProgressListener.reset();

        validate();

        setStatusMessage(Messages.getText("SearchAction.StatusIndexing"));

        this.releases = createReleases();

        if (Thread.currentThread().isInterrupted()) {
            return;
        }

        if (this.releases.isEmpty()) {
            this.cancel(true);
            return;
        }

        this.indexingProgressListener.completed();

        this.statusListener = this.searchProgressListener;

        /* Create a new SearchManager. */
        this.searchManager =
            new SearchManager(settings,
                /* Tell the manager which language we want */
                language,
                /* Tell the manager where to push progressUpdates */
                searchProgressListener,
                /* Tell the manager how to handle user interactions */
                userInteractionHandler,
                /* Listen for when the manager tells us Subtitles are found */
                this);

        /* Tell the manager which providers to use */
        searchManager.reset();
        this.subtitleProviderStore.getAllProviders().stream()
            .filter(subtitleProvider -> settings.useSerieSource(subtitleProvider.subtitleSource))
            .forEach(searchManager::addProvider);

        /* Tell the manager which releases to search. */
        this.releases.forEach(searchManager::addRelease);

        setStatusMessage(Messages.getText("SearchAction.StatusSearching"));

        /* Tell the manager to start searching */
        this.searchManager.start();
    }

    protected abstract void validate() throws SearchSetupException;

    protected abstract List<Release> createReleases();

    protected void setStatusMessage(String message) {
        this.statusListener.onStatus(message);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (searchManager != null) {
            this.searchManager.cancel(mayInterruptIfRunning);
        }
        Thread.currentThread().interrupt();
        this.indexingProgressListener.completed();
        this.searchProgressListener.completed();
        return true;
    }
}
