package de.raidcraft.skills.config;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.ProfessionFactory;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.persistance.ProfessionProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.util.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    public List<Skill> loadSkills(Profession profession) {

        List<Skill> skills = new ArrayList<>();
        ConfigurationSection section = getSafeConfigSection("skills");
        Set<String> keys = section.getKeys(false);
        if (keys == null) return skills;
        // now load the skills - when a skill does not exist in the database we will insert it
        for (String skill : keys) {
            try {
                // lets check if the skill has a different profession written on it
                String profName = section.getString(skill + ".profession");
                if (profName != null && !profName.equals("")) {
                    profession = getPlugin().getProfessionManager().getProfession(profession.getHero(), profName);
                }
                Skill profSkill = getPlugin().getSkillManager().getSkill(profession.getHero(), profession, skill);
                skills.add(profSkill);
            } catch (UnknownSkillException | UnknownProfessionException e) {
                getPlugin().getLogger().warning(e.getMessage());
                e.printStackTrace();
            }
        }
        return skills;
    }

    @Override
    public void loadRequirements(Profession profession) {

        ConfigUtil.loadRequirements(this, profession);
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
}
