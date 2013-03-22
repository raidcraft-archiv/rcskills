package de.raidcraft.skills.util;

/**
 * @author Silthus
 */
public final class StringUtils {

    public static String formatName(String name) {

        if (name == null) return "";
        return name.toLowerCase().replace(" ", "-").trim();
    }
}
