package de.raidcraft.skills.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.requirement.*;
import de.raidcraft.skills.api.skill.LevelableSkill;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

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

    public static void loadRequirements(ConfigurationBase<SkillsPlugin> config, Unlockable unlockable) {

        ConfigurationSection skills = config.getOverrideSection("requirements.skills");
        // lets load some skill requirements first
        for (String key : skills.getKeys(false)) {
            try {
                ConfigurationSection section = config.getOverrideSection("requirements.skills." + key);
                Profession profession = config.getPlugin().getProfessionManager().getProfession(unlockable.getHero(),
                        section.getString("profession",
                                (unlockable instanceof Skill ?
                                        ((Skill) unlockable).getProfession().getName() :
                                        unlockable.getHero().getVirtualProfession().getName())));
                int level = section.getInt("level", 0);
                Skill reqSkill = config.getPlugin().getSkillManager().getSkill(unlockable.getHero(), profession, key);
                if (level == 0) {
                    unlockable.addRequirement(new SkillRequirement(reqSkill));
                } else {
                    if (reqSkill instanceof LevelableSkill) {
                        unlockable.addRequirement(new SkillLevelRequirement(((LevelableSkill) reqSkill).getLevel(), level));
                    } else {
                        throw new UnknownSkillException("The skill must be a levelable skill: " + reqSkill);
                    }
                }
            } catch (UnknownSkillException | UnknownProfessionException e) {
                unlockable.getHero().sendMessage("See Console: " + ChatColor.RED + e.getMessage());
                e.printStackTrace();
            }
        }

        ConfigurationSection professions = config.getOverrideSection("requirements.professions");
        // lets get on some hot action with the profession requirements
        for (String key : professions.getKeys(false)) {
            try {
                Profession profession = config.getPlugin().getProfessionManager().getProfession(unlockable.getHero(), key);
                int level = config.getOverride("requirements.professions." + key, 1);
                unlockable.addRequirement(new ProfessionLevelRequirement(profession.getLevel(), level));
            } catch (UnknownSkillException | UnknownProfessionException e) {
                unlockable.getHero().sendMessage("See Console: " + ChatColor.RED + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
