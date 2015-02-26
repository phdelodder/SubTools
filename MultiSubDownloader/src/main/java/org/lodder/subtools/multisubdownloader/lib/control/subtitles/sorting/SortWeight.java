package org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.replacers.GroupReplacer;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.replacers.KeywordReplacer;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.Release;

public class SortWeight {

  protected static final List<KeywordReplacer> keywordReplacers;

  static {
    keywordReplacers = new ArrayList<>();
    keywordReplacers.add(new GroupReplacer());
  }

  protected Map<String, Integer> weights;
  protected int maxScore;

  public SortWeight(Release release, Map<String, Integer> defaultWeights) {
    this.setWeights(release, defaultWeights);
  }

  protected void setWeights(Release release, Map<String, Integer> defaultWeights) {
    this.maxScore = 0;
    this.weights = new HashMap<>();

    /* make a clone so we can't mess up the defined weights */
    defaultWeights = new HashMap<>(defaultWeights); // clone

    replaceReservedKeywords(release, defaultWeights);

    /* get a list of tags */
    List<String> tags = ReleaseParser.getQualityKeyWords(release.getQuality());
    tags.add(release.getReleasegroup().toLowerCase());

    /* only store tags for which we have a weight defined */
    tags.retainAll(defaultWeights.keySet());

    /* store weights for this release */
    for (String tag : tags) {
      int weight = defaultWeights.get(tag);
      this.maxScore += weight;
      this.weights.put(tag, weight);
    }
  }

  private void replaceReservedKeywords(Release release, Map<String, Integer> weights) {
    for (KeywordReplacer replacer : SortWeight.keywordReplacers)
      replacer.replace(release, weights);
  }

  public Map<String, Integer> getWeights() {
    return this.weights;
  }

  public int getMaxScore() {
    return this.maxScore;
  }

}
