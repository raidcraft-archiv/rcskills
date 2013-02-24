package de.raidcraft.skills.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.api.skill.LevelableSkill;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.configuration.ConfigurationSection;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public final class ConfigUtil {

    public static Map<String, Object> parseSkillDefaults(String... defaults) {

        HashMap<String, Object> map = new HashMap<>();
        return parseDefaults(map, defaults);
    }

    public static Map<String, Object> parseDefaults(Map<String, Object> def, String... defaults) {

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

    @SuppressWarnings("unchecked")
    public static <K extends Enum<K>, V> Map<K, V> loadEnumMap(ConfigurationSection section, Class<K> enumType, V def) {

        Map<K, V> map = new EnumMap<>(enumType);
        if (section != null) {
            Set<String> keys = section.getKeys(false);
            if (keys != null) {
                for (String key : keys) {
                    try {
                        K type = K.valueOf(enumType, key.toUpperCase());
                        if (type == null) {
                            throw new Exception("Invalid key type (" + key + ") found in damages.yml.");
                        }
                        V value = (V) section.get(key, def);
                        map.put(type, value);
                    } catch (ClassCastException e) {
                        RaidCraft.LOGGER.warning("Invalid value type (" + key + ") in damages config!");
                    } catch (Exception e) {
                        RaidCraft.LOGGER.warning(e.getMessage());
                    }
                }
            }
        }
        return map;
    }


    public static double getTotalValue(Skill skill, ConfigurationSection section) {

        if (section == null) {
            return 0;
        }
        double value = section.getDouble("base", 0);
        if (skill != null) {
            value += section.getDouble("level-modifier", 0.0) * skill.getHero().getLevel().getLevel();
            value += section.getDouble("prof-level-modifier", 0.0) * skill.getProfession().getLevel().getLevel();
            if (skill instanceof LevelableSkill) {
                value += section.getDouble("skill-level-modifier", 0.0) * ((LevelableSkill) skill).getLevel().getLevel();
            }
        }
        return value;
    }
}
