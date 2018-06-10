package de.raidcraft.skills.config;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.items.ArmorType;
import de.raidcraft.api.items.WeaponType;
import de.raidcraft.skills.ProfessionFactory;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.forumla.LevelFormula;
import de.raidcraft.skills.api.persistance.ProfessionProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.formulas.FormulaType;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

/**
 * @author Silthus
 */
public class ProfessionConfig extends ConfigurationBase<SkillsPlugin> implements ProfessionProperties {

    private final Set<String> undefinedSkills = new HashSet<>();
    private final ProfessionFactory factory;

    public ProfessionConfig(ProfessionFactory factory, File file) {

        super(factory.getPlugin(), file);
        this.factory = factory;
    }

    public List<String> getChildren() {

        return getStringList("childs");
    }

    @Override
    public String getTag() {

        return getOverride("tag", factory.getProfessionName().substring(0, 3).toUpperCase().trim());
    }

    @Override
    public String getFriendlyName() {

        return getOverride("name", factory.getProfessionName());
    }

    @Override
    public String getDescription() {

        return getOverride("description", "Default description");
    }

    @Override
    public ChatColor getColor() {

        return ChatColor.valueOf(getOverride("color", "GRAY"));
    }

    @Override
    public boolean isEnabled() {

        return getOverrideBool("enabled", true);
    }

    @Override
    public Profession getParentProfession(Hero hero) {

        try {
            String parent = getOverrideString("parent", null);
            if (parent != null && !parent.equals("")) {
                return getPlugin().getProfessionManager().getProfession(hero, parent);
            }
        } catch (UnknownSkillException | UnknownProfessionException e) {
            getPlugin().getLogger().severe(e.getMessage());
        }
        return null;
    }

    @Override
    public LevelFormula getLevelFormula() {

        ConfigurationSection config = getPlugin().getLevelConfig().getConfigFor(
                LevelConfig.Type.PROFESSIONS, getOverrideString("formula", "default")
        );
        FormulaType formulaType = FormulaType.fromName(config.getString("type", "static"));

        return formulaType.create(config);
    }

    @Override
    public int getMaxLevel() {

        return getOverride("max-level", 60);
    }

    @Override
    public ConfigurationSection getBaseHealth() {

        return getOverrideSection("health");
    }

    @Override
    public Set<String> getResources() {

        return getOverrideSection("resources").getKeys(false);
    }

    @Override
    public ConfigurationSection getResourceConfig(String type) {

        return getOverrideSection("resources." + type);
    }

    @Override
    public ConfigurationSection getExpMoneyConversionRate() {

        return getOverrideSection("exp-money-conversion-rate");
    }

    @Override
    public Map<String, Skill> loadSkills(Profession profession) {

        Map<String, Skill> skills = new HashMap<>();
        ConfigurationSection section = getSafeConfigSection("skills");
        Set<String> keys = section.getKeys(false);
        if (keys == null) return skills;
        // now load the skills - when a skill does not exist in the database we will insert it
        for (String skill : keys) {
            if (undefinedSkills.contains(skill)) {
                continue;
            }
            try {
                Skill profSkill = getPlugin().getSkillManager().getSkill(profession.getHero(), profession, skill);
                skills.put(profSkill.getName(), profSkill);
            } catch (UnknownSkillException e) {
                getPlugin().getLogger().warning(e.getMessage() + " in " + getName() + ".yml");
                undefinedSkills.add(skill);
            }
        }
        return skills;
    }

    @Override
    public List<Requirement<Player>> loadRequirements(Profession profession) {

        return ActionAPI.createRequirements(profession.getName(), getConfigurationSection("requirements"), Player.class);
    }

    @Override
    public List<Profession> loadChildren(Profession profession) {

        List<Profession> professions = new ArrayList<>();
        List<String> childs = getStringList("childs");
        if (childs == null) return professions;
        for (String prof : childs) {
            try {
                Profession childProf = getPlugin().getProfessionManager().getProfession(profession, prof);
                professions.add(childProf);
                childProf.setParent(profession);
            } catch (UnknownSkillException | UnknownProfessionException e) {
                getPlugin().getLogger().severe("Error while loading child professions: " + e.getMessage());
            }
        }
        return professions;
    }

    @Override
    public Map<WeaponType, Integer> getAllowedWeapons() {

        Map<WeaponType, Integer> weapons = new EnumMap<>(WeaponType.class);
        ConfigurationSection section = getSafeConfigSection("allowed-weapons");
        for (String key : section.getKeys(false)) {
            WeaponType type = WeaponType.fromString(key);
            if (type != null) {
                weapons.put(type, section.getInt(key));
            }
        }
        return weapons;
    }

    @Override
    public Map<ArmorType, Integer> getAllowedArmor() {

        Map<ArmorType, Integer> weapons = new EnumMap<>(ArmorType.class);
        ConfigurationSection section = getSafeConfigSection("allowed-armor");
        for (String key : section.getKeys(false)) {
            ArmorType type = ArmorType.fromString(key);
            if (type != null) {
                weapons.put(type, section.getInt(key));
            }
        }
        return weapons;
    }

    @Override
    public String getName() {

        return factory.getProfessionName();
    }
}