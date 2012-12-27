package de.raidcraft.skills.config;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.SkillFactory;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.skill.SkillInformation;
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
    public ConfigurationSection getData() {

        return getOverrideSection("custom");
    }

    @Override
    public int getManaCost() {

        return getOverride("mana.base-cost", 0);
    }

    @Override
    public double getManaLevelModifier() {

        return getOverride("mana.level-modifier", 0.0);
    }

    @Override
    public int getStaminaCost() {

        return getOverride("stamina.base-cost", 0);
    }

    @Override
    public double getStaminaLevelModifier() {

        return getOverride("stamina.level-modifier", 0.0);
    }

    @Override
    public int getHealthCost() {

        return getOverride("health.base-cost", 0);
    }

    @Override
    public double getHealthLevelModifier() {

        return getOverride("health.level-modifier", 0.0);
    }

    @Override
    public int getRequiredLevel() {

        return getOverride("level", 1);
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
    public int getCastTime() {

        return getOverride("casttime.base", 0);
    }

    @Override
    public double getCastTimeLevelModifier() {

        return getOverride("casttime.level-modifier", 0.0);
    }

    @Override
    public int getMaxLevel() {

        return getOverride("max-level", 10);
    }

    @Override
    public double getSkillLevelDamageModifier() {

        return getOverride("damage.skill-level-modifier", 0.0);
    }

    @Override
    public double getSkillLevelManaCostModifier() {

        return getOverride("mana.skill-level-modifier", 0.0);
    }

    @Override
    public double getSkillLevelStaminaCostModifier() {

        return getOverride("stamina.skill-level-modifier", 0.0);
    }

    @Override
    public double getSkillLevelHealthCostModifier() {

        return getOverride("health.skill-level-modifier", 0.0);
    }

    @Override
    public double getSkillLevelCastTimeModifier() {

        return getOverride("casttime.skill-level-modifier", 0.0);
    }

    @Override
    public double getProfLevelDamageModifier() {

        return getOverride("damage.prof-level-modifier", 0.0);
    }

    @Override
    public double getProfLevelManaCostModifier() {

        return getOverride("mana.prof-level-modifier", 0.0);
    }

    @Override
    public double getProfLevelStaminaCostModifier() {

        return getOverride("stamina.prof-level-modifier", 0.0);
    }

    @Override
    public double getProfLevelHealthCostModifier() {

        return getOverride("health.prof-level-modifier", 0.0);
    }

    @Override
    public double getProfLevelCastTimeModifier() {

        return getOverride("casttime.prof-level-modifier", 0.0);
    }
}
