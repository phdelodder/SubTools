package org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.lodder.subtools.sublibrary.model.Release;

class SortWeightTest {

    @Test
    void test_it_generates_weights_for_release() throws Exception {
        // Arrested.Development.S01E01.DVDRip.XviD-MEDiEVAL
        Release release = mock(Release.class);
        when(release.getQuality()).thenReturn("DVDRip XviD");
        when(release.getReleaseGroup()).thenReturn("MEDiEVAL");

        HashMap<String, Integer> definedWeights = new HashMap<>();
        definedWeights.put("dvdrip", 2);
        definedWeights.put("xvid", 1);
        definedWeights.put("hdtv", 1);
        definedWeights.put("%GROUP%", 5);

        SortWeight sortWeight = new SortWeight(release, definedWeights);
        Map<String, Integer> weights = sortWeight.getWeights();

        /* check if we have the 3 weights */
        assertThat(weights).hasSize(3).containsKeys("dvdrip", "xvid", "medieval");

        /* check if the weights are correct */
        assertThat(weights.get("dvdrip")).isEqualTo(2);
        assertThat(weights.get("xvid")).isEqualTo(1);
        assertThat(weights.get("medieval")).isEqualTo(5);

        /* check if the maxScore is correct */
        assertThat(sortWeight.getMaxScore()).isEqualTo(8);
    }
}
