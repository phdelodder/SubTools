package org.lodder.subtools.multisubdownloader.workers;

import static manifold.ext.props.rt.api.PropOption.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import manifold.ext.props.rt.api.set;
import manifold.ext.props.rt.api.val;
import manifold.ext.props.rt.api.var;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.gui.dialog.Cancelable;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.ScoreCalculator;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.SortWeight;
import org.lodder.subtools.multisubdownloader.listeners.SearchProgressListener;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public class SearchManager implements Cancelable {

    private final Map<SubtitleProvider, Queue<Release>> queue = new HashMap<>();
    private final Map<SubtitleProvider, SearchWorker> workers = new HashMap<>();
    private final Map<Release, ScoreCalculator> scoreCalculators = new HashMap<>();
    private final Settings settings;
    @var @set(Private) int progress = 0;
    private int totalJobs;

    private final SearchHandler onFound;
    @val Language language;
    private final SearchProgressListener progressListener;
    @val UserInteractionHandler userInteractionHandler;

    public SearchManager(Settings settings, Language language, SearchProgressListener progressListener,
        UserInteractionHandler userInteractionHandler, SearchHandler onFound) {
        this.settings = settings;
        this.language = language;
        this.progressListener = progressListener;
        this.userInteractionHandler = userInteractionHandler;
        this.onFound = onFound;
    }

    public void reset() {
        queue.clear();
        workers.clear();
        scoreCalculators.clear();
    }

    public void addProvider(SubtitleProvider provider) {
        if (this.workers.containsKey(provider)) {
            return;
        }
        this.workers.put(provider, new SearchWorker(provider, this));
        this.queue.put(provider, new LinkedList<>());
    }

    public void addRelease(Release release) {
        this.queue.forEach((key, _) -> queue.get(key).add(release));
        /* Create a scoreCalculator so we can score subtitles for this release */
        // TODO: extract to factory
        SortWeight weights = new SortWeight(release, this.settings.sortWeights);
        this.scoreCalculators.put(release, new ScoreCalculator(weights));
    }

    public void start() {
        synchronized (this) {
            totalJobs = this.jobsLeft();
            if (totalJobs <= 0) {
                return;
            }
            workers.forEach((key, value) -> value.start());
        }

    }

    public void onCompleted(SearchWorker worker) {
        Release release = worker.release;
        List<Subtitle> subtitles = new ArrayList<>(worker.subtitles);

        /* set the score of the found subtitles */
        ScoreCalculator calculator = this.scoreCalculators.get(release);
        subtitles.forEach(subtitle -> subtitle.score = calculator.calculate(subtitle));

        synchronized (this) {
            calculateProgress();
            /* Tell the progress listener our total progress */
            this.progressListener.progress(this.progress);
        }

        onFound.onFound(release, subtitles);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        workers.forEach((key, value) -> value.interrupt());
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
            if (worker.isAlive() && worker.busy) {
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
