package org.lodder.subtools.sublibrary.control;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class RomanTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testDecode() {
        assertEquals(Roman.decode("M"), 1000);
        assertEquals(Roman.decode("D"), 500);
        assertEquals(Roman.decode("C"), 100);
        assertEquals(Roman.decode("L"), 50);
        assertEquals(Roman.decode("X"), 10);
        assertEquals(Roman.decode("I"), 1);

        assertEquals(Roman.decode("MI"), 1001);
        assertEquals(Roman.decode("VI"), 6);
        assertEquals(Roman.decode("IV"), 4);
    }

}
