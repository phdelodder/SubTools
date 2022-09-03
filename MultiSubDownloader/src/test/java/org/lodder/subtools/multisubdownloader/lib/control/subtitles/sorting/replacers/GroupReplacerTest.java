package org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.replacers;

import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.lodder.subtools.sublibrary.model.Release;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GroupReplacerTest {

    protected GroupReplacer replacer;

    @Before
    public void setUp() throws Exception {
        replacer = new GroupReplacer();
    }

    @Test
    public void test_it_replaces_the_keyword_group_to_a_releasename() throws Exception {
        Release release = mock(Release.class);
        when(release.getReleasegroup()).thenReturn("Acme");

        HashMap<String, Integer> definedWeights = new HashMap<>();
        definedWeights.put("%GROUP%", 5);

        replacer.replace(release, definedWeights);

        /* check if the weight there is only one weight */
        assertEquals(1, definedWeights.size());

        /* check if the weight is acme */
        assertTrue(definedWeights.containsKey("acme"));
    }

    @Test
    public void testEmptyWeights() throws Exception {
        Release release = mock(Release.class);
        when(release.getReleasegroup()).thenReturn("Acme");

        HashMap<String, Integer> definedWeights = new HashMap<>();

        replacer.replace(release, definedWeights);

        /* check if the weight there is only one weight */
        assertEquals(0, definedWeights.size());

        /* check if the weight is acme */
        assertFalse(definedWeights.containsKey("acme"));
    }
}
