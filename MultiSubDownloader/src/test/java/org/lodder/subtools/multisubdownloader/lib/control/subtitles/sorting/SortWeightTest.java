package org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;

class SortWeightTest {

    @Test
    void test_it_generates_weights_for_release() throws Exception {
        // Arrested.Development.S01E01.DVDRip.XviD-MEDiEVAL
        Release release = mock(TvRelease.class);
        when(release.quality).thenReturn("DVDRip XviD");
        when(release.releaseGroup).thenReturn("MEDiEVAL");

        HashMap<String, Integer> definedWeights = new HashMap<>();
        definedWeights.put("dvdrip", 2);
        definedWeights.put("xvid", 1);
        definedWeights.put("hdtv", 1);
        definedWeights.put("%GROUP%", 5);

        SortWeight sortWeight = new SortWeight(release, definedWeights);
        Map<String, Integer> weights = sortWeight.weights;

        /* check if we have the 3 weights */
        assertThat(weights)
            .hasSize(3)
            .containsEntry("dvdrip", 2)
            .containsEntry("xvid", 1)
            .containsEntry("medieval", 5);

        /* check if the maxScore is correct */
        assertThat(sortWeight.maxScore).isEqualTo(8);
    }
}
