package de.raidcraft.skills.config;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.items.WeaponType;
import de.raidcraft.api.requirement.Requirement;
import de.raidcraft.api.requirement.RequirementManager;
import de.raidcraft.skills.SkillFactory;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.forumla.LevelFormula;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.formulas.FormulaType;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public class SkillConfig extends ConfigurationBase<SkillsPlugin> implements SkillProperties {

    private final String name;
    private final SkillInformation information;

    public SkillConfig(SkillFactory factory) {

        super(factory.getPlugin(), new File(
                new File(factory.getPlugin().getDataFolder(), factory.getPlugin().getCommonConfig().skill_config_path),
                factory.getName() + ".yml"));
        this.name = (factory.useAlias() ? factory.getAlias() : factory.getName());
        this.information = factory.getInformation();
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public LevelFormula getLevelFormula() {

        ConfigurationSection config = getPlugin().getLevelConfig().getConfigFor(
                LevelConfig.Type.SKILLS, getOverrideString("formula", "default"));
        FormulaType formulaType = FormulaType.fromName(config.getString("type", "static"));
        return formulaType.create(config);
    }

    @Override
    public SkillInformation getInformation() {

        return information;
    }

    @Override
    public String getFriendlyName() {

        return getOverride("name", getName());
    }

    @Override
    public String getDescription() {

        return getOverride("description", getInformation().description());
    }

    @Override
    public String[] getUsage() {

        List<String> usage = getStringList("usage");
        return usage.toArray(new String[usage.size()]);
    }

    @Override
    public boolean isEnabled() {

        return getBoolean("enabled", true);
    }

    @Override
    public void setEnabled(boolean enabled) {

        set("enabled", enabled);
        save();
    }

    @Override
    public boolean canUseInCombat() {

        return getOverrideBool("use-in-combat", true);
    }

    @Override
    public boolean canUseOutOfCombat() {

        return getOverrideBool("use-out-of-combat", true);
    }

    @Override
    public ItemStack[] getReagents() {

        ConfigurationSection section = getOverrideSection("reagents");
        Set<String> keys = section.getKeys(false);
        ItemStack[] reagents = new ItemStack[keys.size()];
        int i = 0;
        for (String key : keys) {
            Material material;
            try {
                material = Material.getMaterial(Integer.parseInt(key));
            } catch (NumberFormatException e) {
                material = Material.getMaterial(key);
            }
            if (material == null) {
                getPlugin().getLogger().warning("Item " + key + " is non existant in bukkit! Skill: " + getName());
            }
            reagents[i] = new ItemStack(material, section.getInt(key));
        }
        return reagents;
    }

    @Override
    public Set<Skill> getLinkedSkills(Hero hero) {

        Set<Skill> skills = new HashSet<>();
        ConfigurationSection section = getSafeConfigSection("linked-skills");
        Profession profession;
        for (String key : section.getKeys(false)) {
            try {
                profession = getPlugin().getProfessionManager().getProfession(hero, section.getString(key));
                skills.add(getPlugin().getSkillManager().getSkill(hero, profession, key));
            } catch (UnknownSkillException | UnknownProfessionException e) {
                getPlugin().getLogger().warning(e.getMessage());
            }
        }
        return skills;
    }

    @Override
    public boolean isHidden() {

        return getOverride("hidden", false);
    }

    @Override
    public List<Requirement<Hero>> loadRequirements(Skill skill) {

        return RequirementManager.createRequirements(skill, getOverrideSection("requirements"));
    }

    @Override
    public List<Requirement<Hero>> loadUseRequirements(Skill skill) {

        return RequirementManager.createRequirements(skill, getOverrideSection("use-requirements"));
    }

    @Override
    public ConfigurationSection getData() {

        return getOverrideSection("custom");
    }

    @Override
    public int getRequiredLevel() {

        return getOverride("level", 1);
    }

    @Override
    public int getMaxLevel() {

        return getOverride("max-level", 10);
    }

    @Override
    public ConfigurationSection getDamage() {

        return getOverrideSection("damage");
    }

    @Override
    public ConfigurationSection getCastTime() {

        return getOverrideSection("casttime");
    }
    @Override
    public Resource.Type getResourceType(String resource) {

        return Resource.Type.fromString(getOverrideString("resources." + resource + ".type", "flat"));
    }

    public boolean isVariableResourceCost(String resource) {

        return getOverrideBool("resources." + resource + ".variable", false);
    }

    @Override
    public double getResourceCost(String resource) {

        return getOverrideDouble("resources." + resource + ".base", 0);
    }

    @Override
    public double getResourceCostLevelModifier(String resource) {

        return getOverrideDouble("resources." + resource + ".level-modifier", 0.0);
    }

    @Override
    public double getResourceCostSkillLevelModifier(String resource) {

        return getOverrideDouble("resources." + resource + ".skill-level-modifier", 0.0);
    }

    @Override
    public double getResourceCostProfLevelModifier(String resource) {

        return getOverrideDouble("resources." + resource + ".prof-level-modifier", 0.0);
    }

    @Override
    public ConfigurationSection getCooldown() {

        return getOverrideSection("cooldown");
    }

    @Override
    public ConfigurationSection getRange() {

        return getOverrideSection("range");
    }

    @Override
    public ConfigurationSection getUseExp() {

        return getOverrideSection("exp");
    }

    @Override
    public Set<WeaponType> getRequiredWeapons() {

        HashSet<WeaponType> weaponTypes = new HashSet<>();
        List<String> weapons = getStringList("weapons");
        for (String weapon : weapons) {
            WeaponType weaponType = WeaponType.fromString(weapon);
            if (weaponType != null) {
                weaponTypes.add(weaponType);
            }
        }
        return weaponTypes;
    }
}
