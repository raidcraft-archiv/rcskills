package de.raidcraft.skills.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class ConfigUtil {

    public static Map<String, Object> parseSkillDefaults(String[] defaults) {

        HashMap<String, Object> map = new HashMap<>();
        // lets define some default defaults
        map.put("level", 1);
        map.put("mana", 0);
        return parseDefaults(map, defaults);
    }

    public static Map<String, Object> parseDefaults(Map<String, Object> def, String[] defaults) {

        Map<String, Object> map = new HashMap<>();
        for (String s : defaults) {
            String[] split = s.split(":");
            if (split.length != 2) continue;
            try {
                map.put(split[0], Integer.parseInt(split[1]));
                continue;
            } catch (NumberFormatException e) {
                // lets try doubles
            }
            try {
                map.put(split[0], Double.parseDouble(split[1]));
                continue;
            } catch (NumberFormatException e) {
                // lets try booleans
            }
            try {
                map.put(split[0], Boolean.parseBoolean(split[1]));
                continue;
            } catch (Exception e) {
                // lets add it as a string
            }
            map.put(split[0], split[1]);
        }
        return map;
    }
}
