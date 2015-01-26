package org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.replacers;

import java.util.Map;
import org.lodder.subtools.sublibrary.model.Release;

public class GroupReplacer implements KeywordReplacer {
  @Override
  public void replace(Release release, Map<String, Integer> weights) {
    String reservedKey = "%GROUP%";
    if (!weights.containsKey(reservedKey))
      return;

    int weight = weights.get(reservedKey);

    /* remove reserved name from weights */
    weights.remove(reservedKey);

    /* add replaced value */
    String group = release.getReleasegroup();
    weights.put(group, weight);
  }
}
