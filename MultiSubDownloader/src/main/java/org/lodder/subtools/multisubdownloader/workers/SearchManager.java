package org.lodder.subtools.multisubdownloader.workers;

import java.util.*;

import org.lodder.subtools.multisubdownloader.exceptions.SearchSetupException;
import org.lodder.subtools.multisubdownloader.gui.dialog.Cancelable;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.ScoreCalculator;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.SortWeight;
import org.lodder.subtools.multisubdownloader.listeners.SearchProgressListener;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public class SearchManager implements Cancelable {

  protected Map<SubtitleProvider, Queue<Release>> queue = new HashMap<>();
  protected Map<SubtitleProvider, SearchWorker> workers = new HashMap<>();
  protected Map<Release, ScoreCalculator> scoreCalculators = new HashMap<>();
  protected Settings settings;
  protected int progress = 0;
  protected int totalJobs;
  protected SearchHandler onFound;
  protected String language;
  private SearchProgressListener progressListener;

  public SearchManager(Settings settings) {
    this.settings = settings;
  }

  public void onFound(SearchHandler onFound) {
    this.onFound = onFound;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public void addProvider(SubtitleProvider provider) {
    if (this.workers.containsKey(provider)) return;

    this.workers.put(provider, new SearchWorker(provider, this));
    this.queue.put(provider, new LinkedList<Release>());
  }

  public void addRelease(Release release) {
    for (SubtitleProvider provider : this.queue.keySet())
      queue.get(provider).add(release);

    /* Create a scoreCalculator so we can score subtitles for this release */
    // TODO: extract to factory
    SortWeight weights = new SortWeight(release, this.settings.getSortWeights());
    this.scoreCalculators.put(release, new ScoreCalculator(weights));
  }

  public void setProgressListener(SearchProgressListener listener) {
    synchronized (this) {
      this.progressListener = listener;
    }
  }

  public void start() throws SearchSetupException {
    synchronized (this) {
      if (this.progressListener == null)
        throw new SearchSetupException("ProgressListener cannot be null");
    }
    if (this.onFound == null) throw new SearchSetupException("SearchHandler cannot be null");
    if (this.language == null) throw new SearchSetupException("Language cannot be null");

    totalJobs = this.jobsLeft();

    if (totalJobs <= 0) {
      return;
    }


    for (SubtitleProvider provider : workers.keySet())
      workers.get(provider).start();
  }

  public synchronized void onCompleted(SearchWorker worker) {
    Release release = worker.getRelease();
    List<Subtitle> subtitles = new ArrayList<>(worker.getSubtitles());

    calculateProgress();

    /* set the score of the found subtitles */
    ScoreCalculator calculator = this.scoreCalculators.get(release);
    for (Subtitle subtitle : subtitles) {
      subtitle.setScore(calculator.calculate(subtitle));
    }

    /* Tell the progresslistener our total progress */
    this.progressListener.progress(this.getProgress());

    onFound.onFound(release, subtitles);
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    for (SubtitleProvider provider : workers.keySet())
      workers.get(provider).interrupt();

    return true;
  }

  public synchronized Release getNextRelease(SubtitleProvider provider) {
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

  public boolean hasNextRelease(SubtitleProvider provider) {
    return !queue.get(provider).isEmpty();
  }

  public String getLanguage() {
    return this.language;
  }

  public int getProgress() {
    return this.progress;
  }

  private int jobsLeft() {
    int jobsLeft = 0;
    for (SubtitleProvider provider : this.queue.keySet()) {
      jobsLeft += this.queue.get(provider).size();
      SearchWorker worker = this.workers.get(provider);
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
