package org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting;

import static manifold.ext.props.rt.api.PropOption.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import manifold.ext.props.rt.api.get;
import manifold.ext.props.rt.api.set;
import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.replacers.GroupReplacer;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.replacers.KeywordReplacer;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.Release;

public class SortWeight {

    private static final List<KeywordReplacer> KEYWORD_REPLACERS = List.of(new GroupReplacer());

    @get Map<String, Integer> weights = new HashMap<>();
    @get @set(Private) int maxScore;

    public SortWeight(Release release, Map<String, Integer> defaultWeights) {
        this.setWeights(release, defaultWeights);
    }

    protected void setWeights(Release release, Map<String, Integer> defaultWeights) {
        this.maxScore = 0;
        this.weights.clear();

        /* make a clone, so we can't mess up the defined weights */
        Map<String, Integer> defaultWeightsNew = new HashMap<>(defaultWeights); // clone

        replaceReservedKeywords(release, defaultWeightsNew);

        /* get a list of tags */
        List<String> tags = new ArrayList<>(ReleaseParser.getQualityKeyWords(release.quality));
        if (StringUtils.isNotBlank(release.releaseGroup)) {
            tags.add(release.releaseGroup.toLowerCase());
        }

        /* store weights for this release */
        tags.forEach(tag ->
            defaultWeightsNew.entrySet().stream()
                /* only store tags for which we have a weight defined */
                .filter(entry -> Pattern.compile(entry.getKey()).matcher(tag).find())
                .map(Map.Entry::getValue)
                .findFirst()
                .ifPresent(weight -> {
                    this.maxScore += weight;
                    this.weights.put(tag, weight);
                }));
    }

    private void replaceReservedKeywords(Release release, Map<String, Integer> weights) {
        SortWeight.KEYWORD_REPLACERS.forEach(replacer -> replacer.replace(release, weights));
    }
}
