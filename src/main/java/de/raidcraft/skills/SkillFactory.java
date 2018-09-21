package de.raidcraft.skills;

import de.raidcraft.RaidCraft;
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
import de.raidcraft.skills.util.AbstractFactory;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * @author Silthus
 */
public final class SkillFactory extends AbstractFactory<SkillInformation> {

    private final SkillsPlugin plugin;
    private final Class<? extends Skill> sClass;
    // every profession needs its own config instance
    private final Map<String, SkillConfig> skillConfigs = new CaseInsensitiveMap<>();
    private final AliasesConfig aliasConfig;
    private Constructor<? extends Skill> constructor;

    protected SkillFactory(SkillsPlugin plugin, Class<? extends Skill> sClass, String skillName) throws UnknownSkillException {

        this(plugin, sClass, skillName, null);
    }

    protected SkillFactory(SkillsPlugin plugin, Class<? extends Skill> sClass, String skillName, AliasesConfig aliasConfig) throws UnknownSkillException {

        super(plugin, skillName);
        this.plugin = plugin;
        this.sClass = sClass;
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
        SkillConfig skillConfig = new SkillConfig(this);
        skillConfig.setSaveDefaults(true);
        SkillProperties config = plugin.configure(skillConfig);
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

        SkillConfig config = plugin.configure(new SkillConfig(this));
        // we need to set all the overrides to null because they are used multiple times
        if (useAlias()) {
            config.merge(aliasConfig);
        }

        if (!config.isEnabled()) {
            throw new UnknownSkillException("The skill " + (useAlias() ? getAlias() : getName()) + " is disabled!");
        }

        // its reflection time yay!
        try {
            Skill skill = constructor.newInstance(null, config, null, null);
            if (!skill.isEnabled()) {
                throw new UnknownSkillException("The skill " + (useAlias() ? getAlias() : getName()) + " is disabled!");
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

    public boolean useAlias() {

        return aliasConfig != null;
    }

    public String getAlias() {

        return aliasConfig.getName();
    }

    public SkillInformation getInformation() {

        return sClass.getAnnotation(SkillInformation.class);
    }

    protected Skill create(Hero hero, Profession profession, ConfigurationSection... overrides) throws UnknownSkillException {

        SkillConfig config;
        if (!skillConfigs.containsKey(profession.getName())) {
            config = plugin.configure(new SkillConfig(this));
            // we need to set all the overrides to null because they are used multiple times
            if (useAlias()) {
                config.merge(aliasConfig);
            }
            ProfessionFactory factory = plugin.getProfessionManager().getFactory(profession);
            // set the config that overrides the default skill parameters with the profession config
            config.merge(factory.getConfig(), "skills." + (useAlias() ? getAlias() : getName()));

            // also lets merge all aditional override configs
            for (ConfigurationSection section : overrides) {
                config.getOverrideConfig().merge(section);
            }

            skillConfigs.put(profession.getName(), config);
        } else {
            config = skillConfigs.get(profession.getName());
        }

        if (!config.isEnabled()) {
            throw new UnknownSkillException("The skill " + (useAlias() ? getAlias() : getName()) + " is disabled!");
        }

        // also save the profession to generate a db entry if none exists
        // profession.save();

        // lets load the database
        THeroSkill database = loadDatabase(hero, profession);

        // its reflection time yay!
        try {
            Skill skill = constructor.newInstance(hero, config, profession, database);
            if (!skill.isEnabled()) {
                throw new UnknownSkillException("The skill " + (useAlias() ? getAlias() : getName()) + " is disabled!");
            }
            // this is called after the skill is created in order
            // to give local variables of the skill a chance to init
            if (database.getLastCast() != null) {
                skill.setLastCast(database.getLastCast().toInstant());
                plugin.getLogger().info("Loaded last cast of skill " + skill.getName() + "[" + skill.getHolder() + "]: " + skill.getLastCast());
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

    private THeroSkill loadDatabase(Hero hero, Profession profession) {

        THeroSkill database = RaidCraft.getDatabase(SkillsPlugin.class).find(THeroSkill.class).where()
                .eq("hero_id", hero.getId())
                .eq("name", (useAlias() ? getAlias() : getName()))
                .eq("profession_id", profession.getId()).findOne();

        if (database == null) {
            database = new THeroSkill();
            database.setName((useAlias() ? getAlias() : getName()));
            database.setUnlocked(false);
            database.setExp(0);
            database.setLevel(1);
            database.setHero(RaidCraft.getDatabase(SkillsPlugin.class).find(THero.class, hero.getId()));
            database.setProfession(RaidCraft.getDatabase(SkillsPlugin.class).find(THeroProfession.class, profession.getId()));
            RaidCraft.getDatabase(SkillsPlugin.class).save(database);
        }
        return database;
    }

    public SkillConfig getConfig(Profession profession) {

        return skillConfigs.get(profession.getName());
    }

    protected SkillConfig getNewConfig() {

        return plugin.configure(new SkillConfig(this), false);
    }

    protected Class<? extends Skill> getSkillClass() {

        return sClass;
    }
}
