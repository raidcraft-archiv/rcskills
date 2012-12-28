package de.raidcraft.skills.util;

/**
 * @author Silthus
 */
public final class TimeUtil {

    private TimeUtil() {}

    public static double ticksToSeconds(double ticks) {

        return ((int)((ticks / 20.0) * 100.0)) / 100.0;
    }

    public static double millisToSeconds(long millis) {

        return ticksToSeconds((millis / 1000.0) * 20.0);
    }

}
