package extensions.manifold.science.measures.Time;

import java.math.BigInteger;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import manifold.science.measures.Time;
import manifold.science.measures.TimeUnit;
import manifold.science.util.Rational;

@Extension
public class TimeExt {

    public static boolean isPositive(@This Time time) {
        return time.value.numerator.compareTo(BigInteger.ZERO) > 0;
    }

    public static boolean isNegative(@This Time time) {
        return time.value.numerator.compareTo(BigInteger.ZERO) < 0;
    }

    public static boolean isBefore(@This Time time, Time other) {
        return time.compareTo(other) < 0;
    }

    public static boolean isAfter(@This Time time, Time other) {
        return time.compareTo(other) > 0;
    }

    @Extension
    public static Time create(Number duration, TimeUnit unit) {
        return new Time(Rational.get(duration), unit);
    }
}
