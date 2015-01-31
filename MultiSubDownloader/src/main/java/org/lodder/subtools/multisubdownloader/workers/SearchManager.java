package org.lodder.subtools.multisubdownloader.workers;

import java.util.*;
import org.lodder.subtools.multisubdownloader.gui.dialog.Cancelable;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.ScoreCalculator;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.SortWeight;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public class SearchManager implements Cancelable {

  protected Map<String, Queue<Release>> queue = new HashMap<>();
  protected Map<String, SearchWorker> workers = new HashMap<>();
  protected Map<Release, ScoreCalculator> scoreCalculators = new HashMap<>();
  protected Settings settings;
  protected int progress = 0;
  protected int totalJobs;
  protected SearchHandler onFound;
  protected String language;

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
    if (this.workers.containsKey(provider.getName()))
      return;

    this.workers.put(provider.getName(), new SearchWorker(provider, this));
    this.queue.put(provider.getName(), new LinkedList<Release>());
  }

  public void addRelease(Release release) {
    for (String providerName : this.queue.keySet())
      queue.get(providerName).add(release);

    /* Create a scoreCalculator so we can score subtitles for this release */
    // TODO: extract to factory
    SortWeight weights = new SortWeight(release, this.settings.getSortWeights());
    this.scoreCalculators.put(release, new ScoreCalculator(weights));
  }

  public void start() {
    totalJobs = this.jobsLeft();

    if (totalJobs <= 0) {
      return;
    }


    for (String providerName : workers.keySet())
      workers.get(providerName).start();
  }

  public synchronized void onCompleted(Release release, List<Subtitle> subtitles) {
    calculateProgress();

    /* set the score of the found subtitles */
    ScoreCalculator calculator = this.scoreCalculators.get(release);
    for(Subtitle subtitle : subtitles) {
      subtitle.setScore(calculator.calculate(subtitle));
    }

    onFound.onFound(release, subtitles);
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    for (String providerName : workers.keySet())
      workers.get(providerName).interrupt();

    return true;
  }

  public synchronized Release getNextRelease(String providerName) {
    if (!this.hasNextRelease(providerName))
      return null;

    return queue.get(providerName).poll();
  }

  public boolean hasNextRelease(String providerName) {
    return !queue.get(providerName).isEmpty();
  }

  public String getLanguage() {
    return this.language;
  }

  public int getProgress() {
    return this.progress;
  }

  private int jobsLeft() {
    int jobsLeft = 0;
    for (String providerName : this.queue.keySet()) {
      jobsLeft += this.queue.get(providerName).size();
      SearchWorker worker = this.workers.get(providerName);
      if(worker.isAlive() && worker.isBusy()) {
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
      progress = (int) Math.ceil((float) jobsDone / this.totalJobs * 100);
    }
  }
}
