package de.raidcraft.skills.config;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.ambient.AmbientEffect;
import de.raidcraft.api.ambient.AmbientManager;
import de.raidcraft.api.ambient.UnknownAmbientEffect;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.items.WeaponType;
import de.raidcraft.api.requirement.Requirement;
import de.raidcraft.api.requirement.RequirementManager;
import de.raidcraft.skills.SkillFactory;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.forumla.LevelFormula;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.AbilityEffectStage;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.formulas.FormulaType;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

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

        return getOverrideBool("enabled", true);
    }

    @Override
    public boolean isLevelable() {

        return getOverrideBool("levelable", true);
    }

    @Override
    public boolean isCastable() {

        return getOverrideBool("castable", true);
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
    public Map<AbilityEffectStage, List<AmbientEffect>> getAmbientEffects() {

        HashMap<AbilityEffectStage, List<AmbientEffect>> effects = new HashMap<>();
        ConfigurationSection root = getOverrideSection("visual-effects");
        for (AbilityEffectStage stage : AbilityEffectStage.values()) {
            ConfigurationSection section = root.getConfigurationSection(stage.name().toLowerCase());
            if (section == null) {
                continue;
            }
            if (!effects.containsKey(stage)) {
                effects.put(stage, new ArrayList<AmbientEffect>());
            }
            Set<String> keys = section.getKeys(false);
            for (String key : keys) {
                try {
                    effects.get(stage).add(AmbientManager.getEffect(section.getConfigurationSection(key)));
                } catch (UnknownAmbientEffect e) {
                    RaidCraft.LOGGER.warning(e.getMessage());
                }
            }
        }
        return effects;
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
    public Set<EffectType> getTypes() {

        HashSet<EffectType> types = new HashSet<>();
        for (String str : getStringList("types")) {
            EffectType type = EffectType.fromString(str);
            if (type == null) {
                getPlugin().getLogger().warning("Wrong effect type " + str + " defined in config " + getName());
                continue;
            }
            types.add(type);
        }
        return types;
    }

    @Override
    public Set<EffectElement> getElements() {

        HashSet<EffectElement> elements = new HashSet<>();
        for (String str : getStringList("elements")) {
            EffectElement element = EffectElement.fromString(str);
            if (element == null) {
                getPlugin().getLogger().warning("Wrong effect element " + str + " defined in config " + getName());
                continue;
            }
            elements.add(element);
        }
        return elements;
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
    public ConfigurationSection getResourceCost(String resource) {

        return getOverrideSection("resources." + resource);
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
        List<String> weapons = getOverride("weapons", new ArrayList<String>());
        for (String weapon : weapons) {
            WeaponType weaponType = WeaponType.fromString(weapon);
            if (weaponType != null) {
                weaponTypes.add(weaponType);
            }
        }
        return weaponTypes;
    }
}
