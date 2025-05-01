package org.lodder.subtools.sublibrary.util;

import static java.time.temporal.ChronoUnit.*;

import lombok.experimental.UtilityClass;
import manifold.science.measures.Time;

@UtilityClass
public class Sleep {

    public static void sleep(Time time) {
        try {
            Thread.sleep(time.get(SECONDS) * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
