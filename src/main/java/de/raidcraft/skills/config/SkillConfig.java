package de.raidcraft.skills.config;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.SkillFactory;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.formulas.FormulaType;
import de.raidcraft.skills.api.level.forumla.LevelFormula;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.util.ConfigUtil;
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

        return getOverride("description", getInformation().desc());
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
    public void loadRequirements(Skill skill) {

        ConfigUtil.loadRequirements(this, skill);
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
    public int getDamage() {

        return getOverride("damage.base", 0);
    }

    @Override
    public double getDamageLevelModifier() {

        return getOverride("damage.level-modifier", 0.0);
    }

    @Override
    public double getDamageProfLevelModifier() {

        return getOverride("damage.prof-level-modifier", 0.0);
    }

    @Override
    public double getDamageSkillLevelModifier() {

        return getOverride("damage.skill-level-modifier", 0.0);
    }

    @Override
    public double getDamageResourceModifier(String resouce) {

        return getOverrideDouble("damage." + resouce + "-modifier", 0.0);
    }

    @Override
    public double getCastTime() {

        return getOverride("casttime.base", 0.0);
    }

    @Override
    public double getCastTimeLevelModifier() {

        return getOverride("casttime.level-modifier", 0.0);
    }

    @Override
    public double getCastTimeSkillLevelModifier() {

        return getOverride("casttime.skill-level-modifier", 0.0);
    }

    @Override
    public double getCastTimeProfLevelModifier() {

        return getOverride("casttime.prof-level-modifier", 0.0);
    }

    @Override
    public double getCastTimeResourceModifier(String resouce) {

        return getOverrideDouble("casttime." + resouce + "-modifier", 0.0);
    }

    @Override
    public int getResourceCost(String resource) {

        return getOverrideInt("resources." + resource + ".base", 0);
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
    public double getCooldown() {

        return getOverride("cooldown.base", 0.0);
    }

    @Override
    public double getCooldownLevelModifier() {

        return getOverride("cooldown.level-modifier", 0.0);
    }

    @Override
    public double getCooldownSkillLevelModifier() {

        return getOverride("cooldown.skill-level-modifier", 0.0);
    }

    @Override
    public double getCooldownProfLevelModifier() {

        return getOverride("cooldown.prof-level-modifier", 0.0);
    }

    @Override
    public double getCooldownResourceModifier(String resource) {

        return getOverrideDouble("cooldown." + resource + "-modifier", 0.0);
    }

    @Override
    public int getRange() {

        return getOverride("range.base", 30);
    }

    @Override
    public double getRangeLevelModifier() {

        return getOverride("range.level-modifier", 0.0);
    }

    @Override
    public double getRangeProfLevelModifier() {

        return getOverride("range.prof-level-modifier", 0.0);
    }

    @Override
    public double getRangeSkillLevelModifier() {

        return getOverride("range.skill-level-modifier", 0.0);
    }

    @Override
    public double getRangeResourceModifier(String resource) {

        return getOverrideDouble("range." + resource + "-modifier", 0.0);
    }
}
