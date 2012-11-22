package de.raidcraft.skills.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class ConfigUtil {

    public static Map<String, Object> parseSkillDefaults(String[] defaults) {

        HashMap<String, Object> map = new HashMap<>();
        return parseDefaults(map, defaults);
    }

    public static Map<String, Object> parseDefaults(Map<String, Object> def, String[] defaults) {

        for (String s : defaults) {
            String[] split = s.split(":");
            if (split.length != 2) continue;
            try {
                def.put(split[0], Integer.parseInt(split[1]));
                continue;
            } catch (NumberFormatException e) {
                // lets try doubles
            }
            try {
                def.put(split[0], Double.parseDouble(split[1]));
                continue;
            } catch (NumberFormatException e) {
                // lets try booleans
            }
            def.put(split[0], split[1]);
        }
        return def;
    }
}
