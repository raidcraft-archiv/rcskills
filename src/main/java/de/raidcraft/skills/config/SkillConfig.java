package de.raidcraft.skills.config;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.requirement.Requirement;
import de.raidcraft.api.requirement.RequirementManager;
import de.raidcraft.skills.SkillFactory;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.level.forumla.LevelFormula;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.formulas.FormulaType;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public class SkillConfig extends ConfigurationBase<SkillsPlugin> implements SkillProperties {

    private final SkillFactory factory;

    public SkillConfig(SkillFactory factory) {

        super(factory.getPlugin(), new File(
                new File(factory.getPlugin().getDataFolder(), factory.getPlugin().getCommonConfig().skill_config_path),
                factory.getSkillName() + ".yml"));
        this.factory = factory;
    }

    @Override
    public String getName() {

        if (factory.useAlias()) {
            return factory.getAlias();
        } else {
            return factory.getSkillName();
        }
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

        return factory.getInformation();
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
    public boolean isHidden() {

        return getOverride("hidden", false);
    }

    @Override
    public List<Requirement<Skill>> loadRequirements(Skill skill) {

        return RequirementManager.createRequirements(skill, getOverrideSection("requirements"));
    }

    @Override
    public List<Requirement<Skill>> loadUseRequirements(Skill skill) {

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
}
