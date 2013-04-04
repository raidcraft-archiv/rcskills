package de.raidcraft.skills;

import com.avaje.ebean.Ebean;
import de.raidcraft.api.database.Database;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.config.AliasesConfig;
import de.raidcraft.skills.config.SkillConfig;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroProfession;
import de.raidcraft.skills.tables.THeroSkill;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Constructor;
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
    private Constructor<? extends Skill> constructor;

    protected SkillFactory(SkillsPlugin plugin, Class<? extends Skill> sClass, String skillName) throws UnknownSkillException {

        this(plugin, sClass, skillName, null);
    }

    protected SkillFactory(SkillsPlugin plugin, Class<? extends Skill> sClass, String skillName, AliasesConfig aliasConfig) throws UnknownSkillException {

        this.plugin = plugin;
        this.sClass = sClass;
        this.skillName = skillName;
        this.aliasConfig = aliasConfig;
        // lets cache the constructor for faster skill generation
        try {
            this.constructor = sClass.getConstructor(Hero.class, SkillProperties.class, Profession.class, THeroSkill.class);
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new UnknownSkillException("Found no matching constructor for the skill: " + skillName);
        }
    }

    protected void createDefaults() {

        // simply create new config
        SkillProperties config = plugin.configure(new SkillConfig(this), false);
        // calling these methods will create default entries if none exist
        config.getFriendlyName();
        config.getDescription();
        config.getUsage();
        config.getMaxLevel();
        config.getReagents();
        config.getCooldown();
        config.getCastTime();
    }

    protected Skill createDummy() throws UnknownSkillException {

        SkillConfig config = plugin.configure(new SkillConfig(this), false);
        // we need to set all the overrides to null because they are used multiple times
        if (useAlias()) {
            config.merge(aliasConfig);
        }

        if (!config.isEnabled()) {
            throw new UnknownSkillException("The skill " + skillName + " is not enabled!");
        }

        // its reflection time yay!
        try {
            Skill skill = constructor.newInstance(null, config, null, null);
            if (!skill.isEnabled()) {
                throw new UnknownSkillException("The Skill " + skillName + " is disabled!");
            }
            skill.load(config.getData());
            return skill;
        } catch (Throwable e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
            // lets disable the skill so the console wont be spammed
            if (plugin.getCommonConfig().disable_error_skills) config.setEnabled(false);
        }
        throw new UnknownSkillException("Error when loading skill for class: " + sClass.getCanonicalName());
    }

    protected Skill create(Hero hero, Profession profession, ConfigurationSection... overrides) throws UnknownSkillException {

        SkillConfig config;
        if (!skillConfigs.containsKey(profession)) {
            config = plugin.configure(new SkillConfig(this), false);
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

        if (!config.isEnabled()) {
            throw new UnknownSkillException("The skill " + skillName + " is not enabled!");
        }

        // also save the profession to generate a db entry if none exists
        // profession.save();

        // lets load the database
        THeroSkill database = loadDatabase(hero, profession);

        // its reflection time yay!
        try {
            Skill skill = constructor.newInstance(hero, config, profession, database);
            if (!skill.isEnabled()) {
                throw new UnknownSkillException("The Skill " + skillName + " is disabled!");
            }
            // this is called after the skill is created in order
            // to give local variables of the skill a chance to init
            skill.load(config.getData());
            return skill;
        } catch (Throwable e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
            // lets disable the skill so the console wont be spammed
            if (plugin.getCommonConfig().disable_error_skills) config.setEnabled(false);
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
            database.setProfession(Ebean.find(THeroProfession.class, profession.getId()));
            Database.save(database);
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

    protected SkillConfig getNewConfig() {

        return plugin.configure(new SkillConfig(this), false);
    }

    protected Class<? extends Skill> getSkillClass() {

        return sClass;
    }
}
