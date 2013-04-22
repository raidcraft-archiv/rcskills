package de.raidcraft.skills.util;

/**
 * @author Silthus
 */
public final class TimeUtil {

    private TimeUtil() {

    }

    public static double ticksToSeconds(long ticks) {

        return ((int) (((double)ticks / 20.0) * 100.0)) / 100.0;
    }

    public static double millisToSeconds(long millis) {

        return ((int) (((double)millis / 1000.0) * 100.0)) / 100.0;
    }

    public static long secondsToTicks(double seconds) {

        return Math.round(seconds * 20);
    }

    public static long secondsToMillis(double seconds) {

        return (long) (seconds * 1000);
    }

    public static long ticksToMillis(long ticks) {

        return secondsToMillis(ticksToSeconds(ticks));
    }
}
