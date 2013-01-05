package de.raidcraft.skills;

import com.avaje.ebean.Ebean;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.AbstractProfession;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.config.AliasesConfig;
import de.raidcraft.skills.config.SkillConfig;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroSkill;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class SkillFactory {

    private final SkillsPlugin plugin;
    private final Class<? extends Skill> sClass;
    // every profession needs its own config instance
    private final Map<Profession, SkillConfig> skillConfigs = new HashMap<>();
    private final String skillName;
    private final AliasesConfig aliasConfig;

    protected SkillFactory(SkillsPlugin plugin, Class<? extends Skill> sClass, String skillName) {

        this(plugin, sClass, skillName, null);
    }

    protected SkillFactory(SkillsPlugin plugin, Class<? extends Skill> sClass, String skillName, AliasesConfig aliasConfig) {

        this.plugin = plugin;
        this.sClass = sClass;
        this.skillName = skillName;
        this.aliasConfig = aliasConfig;
    }

    protected void createDefaults() {

        // simply create new config
        SkillProperties config = plugin.configure(new SkillConfig(this));
        // calling these methods will create default entries if none exist
        config.getFriendlyName();
        config.getDescription();
        config.getUsage();
        config.getMaxLevel();
        config.getReagents();
        config.getManaCost();
        config.getManaCostLevelModifier();
        config.getManaCostProfLevelModifier();
        config.getManaCostSkillLevelModifier();
        config.getCooldown();
        config.getCooldownLevelModifier();
        config.getCooldownProfLevelModifier();
        config.getCooldownSkillLevelModifier();
        config.getHealthCost();
        config.getHealthCostLevelModifier();
        config.getHealthCostProfLevelModifier();
        config.getHealthCostSkillLevelModifier();
        config.getStaminaCost();
        config.getStaminaCostLevelModifier();
        config.getStaminaCostProfLevelModifier();
        config.getStaminaCostSkillLevelModifier();
        config.getCastTime();
        config.getCastTimeLevelModifier();
        config.getCastTimeProfLevelModifier();
        config.getCastTimeSkillLevelModifier();
    }

    protected Skill create(Hero hero, Profession profession, ConfigurationSection... overrides) throws UnknownSkillException {

        SkillConfig config;
        if (!skillConfigs.containsKey(profession)) {
            config = plugin.configure(new SkillConfig(this));
            // we need to set all the overrides to null because they are used multiple times
            if (useAlias()) {
                config.merge(aliasConfig);
            }
            ProfessionFactory factory = plugin.getProfessionManager().getFactory(profession);
            // set the config that overrides the default skill parameters with the profession config
            config.merge(factory.getConfig(), "skills." + (useAlias() ? getAlias() : getSkillName()));

            // also lets merge all aditional override configs
            for (ConfigurationSection section : overrides) {
                config.getOverrideConfig().merge(section);
            }

            skillConfigs.put(profession, config);
        } else {
            config = skillConfigs.get(profession);
        }

        // also save the profession to generate a db entry if none exists
        profession.save();

        // lets load the database
        THeroSkill database = loadDatabase(hero, profession);

        // its reflection time yay!
        try {
            Constructor<? extends Skill> constructor =
                    sClass.getConstructor(Hero.class, SkillProperties.class, Profession.class, THeroSkill.class);
            constructor.setAccessible(true);
            Skill skill = constructor.newInstance(hero, config, profession, database);
            if (!skill.isEnabled()) {
                throw new UnknownSkillException("The Skill " + skillName + " is disabled!");
            }
            skill.load(config.getData());
            return skill;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
        throw new UnknownSkillException("Error when loading skill for class: " + sClass.getCanonicalName());
    }

    private THeroSkill loadDatabase(Hero hero, Profession profession) {

        THeroSkill database = Ebean.find(THeroSkill.class).where()
                .eq("hero_id", hero.getId())
                .eq("name", (useAlias() ? getAlias() : getSkillName()))
                .eq("profession_id", profession.getId()).findUnique();

        if (database == null) {
            database = new THeroSkill();
            database.setName((useAlias() ? getAlias() : getSkillName()));
            database.setUnlocked(false);
            database.setExp(0);
            database.setLevel(1);
            database.setHero(Ebean.find(THero.class, hero.getId()));
            database.setProfession((((AbstractProfession) profession).getDatabase()));
        }
        return database;
    }

    public SkillsPlugin getPlugin() {

        return plugin;
    }

    public SkillInformation getInformation() {

        return sClass.getAnnotation(SkillInformation.class);
    }

    public String getSkillName() {

        return skillName;
    }

    public String getAlias() {

        return aliasConfig.getName();
    }

    public boolean useAlias() {

        return aliasConfig != null;
    }

    public SkillConfig getConfig(Profession profession) {

        return skillConfigs.get(profession);
    }
}
