package de.raidcraft.skills.config;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.requirement.Requirement;
import de.raidcraft.api.requirement.RequirementManager;
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
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public class ProfessionConfig extends ConfigurationBase<SkillsPlugin> implements ProfessionProperties {

    private final Set<String> undefinedSkills = new HashSet<>();
    private final ProfessionFactory factory;

    public ProfessionConfig(ProfessionFactory factory) {

        super(factory.getPlugin(), new File(
                new File(factory.getPlugin().getDataFolder(), factory.getPlugin().getCommonConfig().profession_config_path),
                factory.getProfessionName() + ".yml"
        ));
        this.factory = factory;
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
    public List<Profession> loadChildren(Profession profession) {

        List<Profession> professions = new ArrayList<>();
        List<String> childs = getStringList("childs");
        if (childs == null) return professions;
        for (String prof : childs) {
            try {
                professions.add(getPlugin().getProfessionManager().getProfession(profession, prof));
            } catch (UnknownSkillException | UnknownProfessionException e) {
                getPlugin().getLogger().severe(e.getMessage());
            }
        }
        return professions;
    }

    public List<String> getChildren() {

        return getStringList("childs");
    }

    @Override
    public List<Requirement> loadRequirements(Profession profession) {

        return RequirementManager.createRequirements(profession, getOverrideSection("requirements"));
    }

    @Override
    public LevelFormula getLevelFormula() {

        ConfigurationSection config = getPlugin().getLevelConfig().getConfigFor(
                LevelConfig.Type.PROFESSIONS, getOverrideString("formula", "default"));
        FormulaType formulaType = FormulaType.fromName(config.getString("type", "static"));
        return formulaType.create(config);
    }

    @Override
    public String getName() {

        return factory.getProfessionName();
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
    public int getMaxLevel() {

        return getOverride("max-level", 60);
    }

    @Override
    public int getBaseHealth() {

        return getOverride("health.base", 20);
    }

    @Override
    public double getBaseHealthModifier() {

        return getOverride("health.level-modifier", 0.0);
    }

    @Override
    public Set<String> getResources() {

        return getOverrideSection("resources").getKeys(false);
    }

    @Override
    public ConfigurationSection getResourceConfig(String type) {

        return getOverrideSection("resources." + type);
    }
}
