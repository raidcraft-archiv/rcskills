package de.raidcraft.skills.config;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.ProfessionFactory;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.Equipment;
import de.raidcraft.skills.api.persistance.ProfessionProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.*;

/**
 * @author Silthus
 */
public class ProfessionConfig extends ConfigurationBase<SkillsPlugin> implements ProfessionProperties {

    private final ProfessionFactory factory;

    public ProfessionConfig(ProfessionFactory factory) {

        super(factory.getPlugin(), new File(
                new File(factory.getPlugin().getDataFolder(), factory.getPlugin().getCommonConfig().profession_config_path),
                factory.getProfessionName() + ".yml"
        ));
        this.factory = factory;
    }

    @Override
    public List<Skill> loadSkills(Hero hero, Profession profession) {

        List<Skill> skills = new ArrayList<>();
        ConfigurationSection section = getSafeConfigSection("skills");
        Set<String> keys = section.getKeys(false);
        if (keys == null) return skills;
        // now load the skills - when a skill does not exist in the database we will insert it
        for (String skill : keys) {
            try {
                Skill profSkill = getPlugin().getSkillManager().getSkill(hero, profession, skill);
                skills.add(profSkill);
            } catch (UnknownSkillException e) {
                getPlugin().getLogger().warning(e.getMessage());
                e.printStackTrace();
            }
        }
        return skills;
    }

    @Override
    public Set<Profession> loadStrongParents(Hero hero, Profession profession) {

        Set<Profession> parents = new LinkedHashSet<>();
        for (String name : getStringList("parents.strong")) {
            try {
                parents.add(getPlugin().getProfessionManager().getProfession(hero, name));
            } catch (UnknownSkillException | UnknownProfessionException e) {
                getPlugin().getLogger().warning(e.getMessage());
                e.printStackTrace();
            }
        }
        return parents;
    }

    @Override
    public Set<Profession> loadWeakParents(Hero hero, Profession profession) {

        Set<Profession> parents = new LinkedHashSet<>();
        for (String name : getStringList("parents.weak")) {
            try {
                parents.add(getPlugin().getProfessionManager().getProfession(hero, name));
            } catch (UnknownSkillException | UnknownProfessionException e) {
                getPlugin().getLogger().warning(e.getMessage());
                e.printStackTrace();
            }
        }
        return parents;
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
    public int getBaseMana() {

        return getOverride("mana.base", 100);
    }

    @Override
    public double getBaseManaModifier() {

        return getOverride("mana.level-modifier", 0.0);
    }

    @Override
    public int getBaseStamina() {

        return getOverride("stamina.base", 20);
    }

    @Override
    public double getBaseStaminaModifier() {

        return getOverride("stamina.level-modifier", 0.0);
    }

    @Override
    public boolean isPrimary() {

        return getOverride("primary", false);
    }

    @Override
    public Set<Equipment> getEquipment() {

        Set<Equipment> equipment = new HashSet<>();
        ConfigurationSection section = getOverrideSection("equipment");
        Set<String> keys = section.getKeys(false);
        if (keys == null) return equipment;
        for (String key : keys) {
            equipment.add(new Equipment(section.getConfigurationSection(key)));
        }
        return equipment;
    }
}
