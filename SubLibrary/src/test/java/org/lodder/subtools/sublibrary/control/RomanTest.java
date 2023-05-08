package org.lodder.subtools.sublibrary.control;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RomanTest {

    @Test
    void testDecode() {
        assertThat(Roman.decode("M")).isEqualTo(1000);
        assertThat(Roman.decode("D")).isEqualTo(500);
        assertThat(Roman.decode("C")).isEqualTo(100);
        assertThat(Roman.decode("L")).isEqualTo(50);
        assertThat(Roman.decode("X")).isEqualTo(10);
        assertThat(Roman.decode("I")).isEqualTo(1);

        assertThat(Roman.decode("MI")).isEqualTo(1001);
        assertThat(Roman.decode("VI")).isEqualTo(6);
        assertThat(Roman.decode("IV")).isEqualTo(4);
    }

}
