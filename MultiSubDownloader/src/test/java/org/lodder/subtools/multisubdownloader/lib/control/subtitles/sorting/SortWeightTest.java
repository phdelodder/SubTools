package org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.lodder.subtools.sublibrary.model.Release;

public class SortWeightTest {

    protected SortWeight sortWeight;

    @Before
    public void setUp() throws Exception {
        sortWeight = null;
    }

    @Test
    public void test_it_generates_weights_for_release() throws Exception {
        // Arrested.Development.S01E01.DVDRip.XviD-MEDiEVAL
        Release release = mock(Release.class);
        when(release.getQuality()).thenReturn("DVDRip XviD");
        when(release.getReleaseGroup()).thenReturn("MEDiEVAL");

        HashMap<String, Integer> definedWeights = new HashMap<>();
        definedWeights.put("dvdrip", 2);
        definedWeights.put("xvid", 1);
        definedWeights.put("hdtv", 1);
        definedWeights.put("%GROUP%", 5);

        sortWeight = new SortWeight(release, definedWeights);
        Map<String, Integer> weights = sortWeight.getWeights();

        /* check if we have the 3 weights */
        assertEquals(3, weights.size());
        assertTrue(weights.containsKey("dvdrip"));
        assertTrue(weights.containsKey("xvid"));
        assertTrue(weights.containsKey("medieval"));

        /* check if the weights are correct */
        assertEquals((Integer) 2, weights.get("dvdrip"));
        assertEquals((Integer) 1, weights.get("xvid"));
        assertEquals((Integer) 5, weights.get("medieval"));

        /* check if the maxScore is correct */
        assertEquals(8, sortWeight.getMaxScore());
    }
}
