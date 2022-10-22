package org.lodder.subtools.multisubdownloader.workers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.exceptions.SearchSetupException;
import org.lodder.subtools.multisubdownloader.gui.dialog.Cancelable;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.ScoreCalculator;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.SortWeight;
import org.lodder.subtools.multisubdownloader.listeners.SearchProgressListener;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public class SearchManager implements Cancelable {

    public interface SearchManagerLanguage {
        SearchManagerProgressListener language(@NonNull Language language);
    }

    public interface SearchManagerProgressListener {
        SearchManagerUserInteractionHandler progressListener(@NonNull SearchProgressListener progressListener);
    }

    public interface SearchManagerUserInteractionHandler {
        SearchManagerOnFound userInteractionHandler(@NonNull UserInteractionHandler userInteractionHandler);
    }

    public interface SearchManagerOnFound {
        SearchManager onFound(@NonNull SearchHandler onFound);
    }

    @Setter
    @Accessors(fluent = true)
    public static class SearchManagerBuilder
            implements SearchManagerOnFound, SearchManagerUserInteractionHandler, SearchManagerProgressListener, SearchManagerLanguage {
        private Settings settings;
        private Language language;
        private SearchProgressListener progressListener;
        private UserInteractionHandler userInteractionHandler;

        @Override
        public SearchManager onFound(SearchHandler onFound) {
            return new SearchManager(settings, onFound, language, progressListener, userInteractionHandler);
        }
    }

    private final Map<SubtitleProvider, Queue<Release>> queue = new HashMap<>();
    private final Map<SubtitleProvider, SearchWorker> workers = new HashMap<>();
    private final Map<Release, ScoreCalculator> scoreCalculators = new HashMap<>();
    private final Settings settings;
    @Getter
    private int progress = 0;
    private int totalJobs;

    private final SearchHandler onFound;
    @Getter
    private final Language language;
    private final SearchProgressListener progressListener;
    @Getter
    private final UserInteractionHandler userInteractionHandler;

    public static SearchManagerLanguage createWithSettings(Settings settings) {
        return new SearchManagerBuilder().settings(settings);
    }

    public void addProvider(SubtitleProvider provider) {
        if (this.workers.containsKey(provider)) {
            return;
        }
        this.workers.put(provider, new SearchWorker(provider, this));
        this.queue.put(provider, new LinkedList<Release>());
    }

    public void addRelease(Release release) {
        this.queue.entrySet().forEach(provider -> queue.get(provider.getKey()).add(release));
        /* Create a scoreCalculator so we can score subtitles for this release */
        // TODO: extract to factory
        SortWeight weights = new SortWeight(release, this.settings.getSortWeights());
        this.scoreCalculators.put(release, new ScoreCalculator(weights));
    }

    public void start() throws SearchSetupException {
        synchronized (this) {
            totalJobs = this.jobsLeft();
            if (totalJobs <= 0) {
                return;
            }
            workers.entrySet().forEach(entry -> entry.getValue().start());
        }

    }

    public void onCompleted(SearchWorker worker) {
        Release release = worker.getRelease();
        List<Subtitle> subtitles = new ArrayList<>(worker.getSubtitles());


        /* set the score of the found subtitles */
        ScoreCalculator calculator = this.scoreCalculators.get(release);
        subtitles.forEach(subtitle -> subtitle.setScore(calculator.calculate(subtitle)));

        synchronized (this) {
            calculateProgress();
            /* Tell the progresslistener our total progress */
            this.progressListener.progress(this.getProgress());
        }

        onFound.onFound(release, subtitles);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        workers.entrySet().forEach(worker -> worker.getValue().interrupt());
        return true;
    }

    public Release getNextRelease(SubtitleProvider provider) {
        synchronized (provider) {
            if (!this.hasNextRelease(provider)) {
                /* Tell the progressListener this provider is finished */
                this.progressListener.progress(provider, queue.get(provider).size(), null);
                return null;
            }

            Release release = queue.get(provider).poll();

            /* Tell the progressListener we are starting on a new Release */
            this.progressListener.progress(provider, queue.get(provider).size(), release);

            return release;
        }
    }

    public boolean hasNextRelease(SubtitleProvider provider) {
        return !queue.get(provider).isEmpty();
    }

    private int jobsLeft() {
        int jobsLeft = 0;

        for (Entry<SubtitleProvider, Queue<Release>> provider : this.queue.entrySet()) {
            jobsLeft += provider.getValue().size();
            SearchWorker worker = this.workers.get(provider.getKey());
            if (worker.isAlive() && worker.isBusy()) {
                jobsLeft++;
            }
        }

        return jobsLeft;
    }

    private void calculateProgress() {
        if (totalJobs <= 0) {
            // No job, means we are completed
            progress = 100;
        } else {
            int jobsDone = this.totalJobs - this.jobsLeft();
            progress = (int) Math.floor((float) jobsDone / this.totalJobs * 100);
        }
    }
}
