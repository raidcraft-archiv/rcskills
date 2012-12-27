package de.raidcraft.skills;

import com.avaje.ebean.Ebean;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.AbstractProfession;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.config.SkillConfig;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroSkill;

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
    private final Map<String, SkillConfig> skillConfigs = new HashMap<>();
    private final String skillName;
    private final String alias;

    protected SkillFactory(SkillsPlugin plugin, Class<? extends Skill> sClass, String skillName) {

        this(plugin, sClass, skillName, null);
    }

    protected SkillFactory(SkillsPlugin plugin, Class<? extends Skill> sClass, String skillName, String alias) {

        this.plugin = plugin;
        this.sClass = sClass;
        this.skillName = skillName;
        this.alias = alias;
    }

    protected Skill create(Hero hero, Profession profession) throws UnknownSkillException {

        ProfessionFactory factory = plugin.getProfessionManager().getFactory(profession);
        SkillConfig config;
        if (!skillConfigs.containsKey(factory.getName())) {
            config = plugin.configure(new SkillConfig(this));

            // we need to set all the overrides to null because they are used multiple times
            if (useAlias()) {
                config.getOverrideConfig().merge(plugin.getAliasesConfig().getSkillConfig(alias));
            }
            // set the config that overrides the default skill parameters with the profession config
            config.merge(factory, "skills." + (useAlias() ? alias : skillName));

            skillConfigs.put(factory.getName(), config);
        } else {
            config = skillConfigs.get(factory.getName());
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
            return constructor.newInstance(hero, config, profession, database);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
        throw new UnknownSkillException("Error when loading skill for class: " + sClass.getCanonicalName());
    }

    private THeroSkill loadDatabase(Hero hero, Profession profession) {

        THeroSkill database = Ebean.find(THeroSkill.class).where()
                .eq("hero_id", hero.getId())
                .eq("name", skillName)
                .eq("profession_id", profession.getId()).findUnique();

        if (database == null) {
            database = new THeroSkill();
            database.setName(getSkillName());
            database.setUnlocked(false);
            database.setExp(0);
            database.setLevel(0);
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

        return alias;
    }

    public boolean useAlias() {

        return alias != null && plugin.getAliasesConfig().hasSkill(alias, skillName);
    }

    public SkillConfig getConfig(Profession profession) {

        return skillConfigs.get(profession.getName());
    }
}
