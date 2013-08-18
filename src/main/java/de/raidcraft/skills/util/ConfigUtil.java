package de.raidcraft.skills.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.api.ability.Ability;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Attribute;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.resource.Resource;
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

    private static double getSkillLevelModifier(Ability ability, ConfigurationSection section) {

        if (ability == null) {
            return 0.0;
        }
        double value = 0.0;
        if (ability instanceof LevelableSkill) {
            value += section.getDouble("skill-level-modifier", 0.0) * ((LevelableSkill) ability).getAttachedLevel().getLevel();
        }
        return value;
    }

    private static double getProfessionValue(Profession profession, ConfigurationSection section) {

        if (profession == null) {
            return 0.0;
        }
        return section.getDouble("prof-level-modifier", 0.0) * profession.getAttachedLevel().getLevel();
    }

    private static double getTotalPathValue(Profession profession, ConfigurationSection section) {

        if (profession == null) {
            return 0.0;
        }
        return section.getDouble("path-level-modifier", 0.0) * profession.getPath().getTotalPathLevel(profession.getHero());
    }

    private static double getResourceValues(CharacterTemplate holder, ConfigurationSection section, Set<String> availableModifiers) {

        if (!(holder instanceof Hero)) {
            return 0.0;
        }
        double value = 0.0;
        boolean fromMax = section.getBoolean("from-max", false);
        for (Resource resource : ((Hero) holder).getResources()) {
            if (resource.isEnabled()) {
                if (section.isSet(resource.getName() + "-base-modifier")) {
                    int base = resource.getBaseValue();
                    value += section.getDouble(resource.getName() + "-base-modifier") * base;
                    availableModifiers.remove(resource.getName() + "-base-modifier");
                }
                if (section.isSet(resource.getName() + "-modifier")) {
                    int base = fromMax ? resource.getMax() : resource.getCurrent();
                    value += section.getDouble(resource.getName() + "-modifier") * base;
                    availableModifiers.remove(resource.getName() + "-modifier");
                }
                if (section.isSet(resource.getName() + "-percent-modifier")) {
                    value += ((double) resource.getCurrent() / (double) resource.getMax())
                            * section.getDouble(resource.getName() + "-percent-modifier", 1.0);
                    availableModifiers.remove(resource.getName() + "-percent-modifier");
                }
            }
        }
        return value;
    }

    private static double getAttributeValues(CharacterTemplate holder, ConfigurationSection section, Set<String> availableModifiers) {

        if (!(holder instanceof Hero)) {
            return 0.0;
        }
        double value = 0.0;
        for (Attribute attribute : ((Hero) holder).getAttributes()) {
            if (section.isSet(attribute.getName() + "-attr-modifier")) {
                value += section.getDouble(attribute.getName() + "-attr-modifier") * attribute.getCurrentValue();
                availableModifiers.remove(attribute.getName() + "-attr-modifier");
            }
        }
        return value;
    }

    private static double getExtraValues(CharacterTemplate holder, ConfigurationSection section, Set<String> availableModifier) {

        if (!(holder instanceof Hero)) {
            return 0.0;
        }
        double value = 0.0;
        Hero hero = (Hero) holder;
        for (String key : availableModifier) {
            if (key.endsWith("-skill-modifier")) {
                key = key.replace("-skill-modifier", "").trim();
                try {
                    Skill extraSkill = hero.getSkill(key);
                    if (extraSkill instanceof LevelableSkill) {
                        value += section.getDouble(key, 0.0) * ((LevelableSkill) extraSkill).getAttachedLevel().getLevel();
                    }
                } catch (UnknownSkillException e) {
                    RaidCraft.LOGGER.warning(e.getMessage() + " - in " + section.getParent().getName());
                }
            } else if (key.endsWith("-prof-modifier")) {
                key = key.replace("-prof-modifier", "").trim();
                try {
                    Profession extraProf = hero.getProfession(key);
                    value += section.getDouble(key, 0.0) * extraProf.getAttachedLevel().getLevel();
                } catch (UnknownSkillException | UnknownProfessionException e) {
                    RaidCraft.LOGGER.warning(e.getMessage() + " - in " + section.getParent().getName());
                }
            }
        }
        return value;
    }

    public static double getTotalValue(CharacterTemplate holder, Ability ability, Profession profession, ConfigurationSection section, double defautValue) {

        if (section == null) {
            return 0.0;
        }
        Set<String> availableModifier = section.getKeys(false);
        double value = section.getDouble("base", 0.0);
        double cap = section.getDouble("cap", 0);
        double low = section.getDouble("low", 0);
        boolean addWeaponDamage = section.getBoolean("weapon-damage", false);

        if (ability != null && profession == null && ability instanceof Skill) {
            profession = ((Skill) ability).getProfession();
        }

        value += section.getDouble("level-modifier", 0.0) * holder.getAttachedLevel().getLevel();
        availableModifier.remove("level-modifier");
        // profession level
        value += getProfessionValue(profession, section);
        availableModifier.remove("prof-level-modifier");
        // path level
        value += getTotalPathValue(profession, section);
        availableModifier.remove("path-level-modifier");
        // skill level
        value += getSkillLevelModifier(ability, section);
        availableModifier.remove("skill-level-modifier");
        // uses resources as value modifiers
        value += getResourceValues(holder, section, availableModifier);
        // also add attributes values
        value += getAttributeValues(holder, section, availableModifier);
        // makes it possible to to use skills dynamically as config values
        getExtraValues(holder, section, availableModifier);

        if (addWeaponDamage) {
            value += holder.getDamage();
        }

        if (cap > 0.0 && value > cap) {
            value = cap;
        }
        if (low != 0.0 && value < low) {
            value = low;
        }
        if (value == 0.0) {
            value = defautValue;
        }
        return value;
    }

    public static double getTotalValue(Ability ability, ConfigurationSection section) {

        return getTotalValue(ability.getHolder(), ability, null, section, 0.0);
    }

    public static double getTotalValue(Profession profession, ConfigurationSection section) {

        return getTotalValue(profession.getHero(), null, profession, section, 0.0);
    }
}
