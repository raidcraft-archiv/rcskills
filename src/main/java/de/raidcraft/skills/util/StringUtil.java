package de.raidcraft.skills.util;

/**
 * @author Silthus
 */
public final class StringUtil {

    public static String formatName(String name) {

        return name.toLowerCase().replace(" ", "-").trim();
    }
}
