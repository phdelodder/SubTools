package org.lodder.subtools.multisubdownloader.workers;

import static manifold.ext.props.rt.api.PropOption.*;

import java.util.Set;

import manifold.ext.props.rt.api.set;
import manifold.ext.props.rt.api.val;
import manifold.ext.props.rt.api.var;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.exception.SubtitlesProviderInitException;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchWorker extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchWorker.class);

    @val SubtitleProvider provider;
    private final SearchManager scheduler;
    @var @set(Private) boolean busy = false;
    private boolean isInterrupted = false;
    @var @set(Private) Release release;
    @var @set(Private) Set<Subtitle> subtitles;

    public SearchWorker(SubtitleProvider provider, SearchManager scheduler) {
        this.provider = provider;
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        Language language = this.scheduler.language;
        this.busy = false;
        try {
            while (!this.isInterrupted()) {
                this.busy = true;
                Release release = this.scheduler.getNextRelease(provider);
                if (release == null) {
                    provider.clearCache();
                    this.busy = false;
                    break;
                }
                this.release = release;
                LOGGER.debug("[Search] {} searching {} ", this.provider.name, release);

                Set<Subtitle> subtitles = this.provider.search(release, language);

                /* clone to prevent other threads from ever messing with it */
                this.subtitles = Set.copyOf(subtitles);

                this.busy = false;
                LOGGER.debug("[Search] {} found {} subtitles for {} ", this.provider.name, subtitles.size(),
                        release);

                if (!this.isInterrupted()) {
                    this.scheduler.onCompleted(this);
                }
            }
        } catch (SubtitlesProviderInitException e) {
            LOGGER.error("API %s INIT (%s)".formatted(e.providerName, e.getMessage()), e);
        }
    }

    @Override
    public boolean isInterrupted() {
        /* bugfix? interrupt-state isn't being held */
        return super.isInterrupted() || this.isInterrupted;
    }

    @Override
    public void interrupt() {
        this.isInterrupted = true;
        super.interrupt();
    }
}
