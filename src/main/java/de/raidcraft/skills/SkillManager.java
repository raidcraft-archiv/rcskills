package de.raidcraft.skills;

import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.loader.GenericJarFileManager;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Passive;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.config.AliasesConfig;
import de.raidcraft.skills.util.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.*;

/**
 * @author Silthus
 */
public final class SkillManager extends GenericJarFileManager<Skill> {

    private final SkillsPlugin plugin;
    private final Map<String, SkillFactory> skillFactories = new HashMap<>();
    private final Map<String, Class<? extends Skill>> skillClasses = new HashMap<>();
    // holds skills that were already loaded for that player
    private final Map<String, Set<Skill>> playerSkills = new HashMap<>();
    // holds all skills that are triggered every few ticks
    private final List<Skill> passiveSkills = new ArrayList<>();

    protected SkillManager(SkillsPlugin plugin) {

        super(Skill.class, new File(plugin.getDataFolder(), plugin.getCommonConfig().skill_jar_path));
        this.plugin = plugin;
        // create the config path
        new File(plugin.getDataFolder(), plugin.getCommonConfig().skill_config_path).mkdirs();
    }

    public void reload() {

        skillFactories.clear();
        playerSkills.clear();
        passiveSkills.clear();
        loadFactories();
    }

    @Override
    public void loadFactories() {

        for (Class<? extends Skill> clazz : loadClasses()) {
            registerClass(clazz);
        }
    }

    /**
     * Registers a skill directly with the skill manager making it possible to use that skill.
     * Skills in external files are loaded via our own class loader on loadSkillClasses() call.
     *
     * @param skillClass of the skill
     */
    public SkillFactory registerClass(Class<? extends Skill> skillClass) {

        if (skillClass.isAnnotationPresent(SkillInformation.class)) {
            String skillName = StringUtils.formatName(skillClass.getAnnotation(SkillInformation.class).name());
            // check for duplicate skills
            if (skillFactories.containsKey(skillName)) {
                plugin.getLogger().warning("Found duplicate Skill: " + skillName);
                return skillFactories.get(skillName);
            }
            // load the skill factory for the non alias
            SkillFactory factory = new SkillFactory(plugin, skillClass, skillName);
            skillFactories.put(skillName, factory);
            skillClasses.put(skillName, skillClass);
            // lets create the skill once to make a default config
            factory.createDefaults();
            plugin.getLogger().info("Loaded Skill: " + factory.getSkillName());
            return factory;
        } else {
            plugin.getLogger().warning("Found skill without SkillInformation: " + skillClass.getCanonicalName());
        }
        return null;
    }

    protected void createAliasFactory(String alias, String skill, AliasesConfig config) {

        SkillFactory factory = new SkillFactory(plugin, skillClasses.get(skill), skill, config);
        skillFactories.put(alias, factory);
        factory.createDefaults();
        plugin.getLogger().info("Loaded Alias: " + alias + " -> " + skill);
    }

    public Skill getSkill(Hero hero, Profession profession, String skillName, ConfigurationSection... overrides) throws UnknownSkillException {

        Skill skill;
        skillName = StringUtils.formatName(skillName);
        if (!skillFactories.containsKey(skillName)) {
            throw new UnknownSkillException("Es gibt keinen Skill mit dem Namen: " + skillName);
        }
        if (!playerSkills.containsKey(hero.getName())) {
            playerSkills.put(hero.getName(), new HashSet<Skill>());
        }
        // always create a new skill instance if there are additional configs
        if (overrides.length < 1) {
            for (Skill playerSkill : playerSkills.get(hero.getName())) {
                if (playerSkill.getName().equals(skillName) && playerSkill.getProfession().equals(profession)) {
                    return playerSkill;
                }
            }
        }
        // lets create a new skill for this name
        skill = skillFactories.get(skillName).create(hero, profession, overrides);
        playerSkills.get(hero.getName()).add(skill);
        // add skill to our passive list if it is a passive skill
        if (skill instanceof Passive) {
            passiveSkills.add(skill);
        }
        // lets add the skill as a trigger handler
        if (skill instanceof Triggered) {
            TriggerManager.registerListeners((Triggered) skill);
        }
        return skill;
    }

    public SkillFactory getFactory(Skill skill) {

        return skillFactories.get(skill.getName());
    }

    public Collection<? extends Skill> getAllSkills(Hero hero) {

        List<Skill> skills = new ArrayList<>();
        for (Profession profession : plugin.getProfessionManager().getAllProfessions(hero)) {
            skills.addAll(profession.getSkills());
        }
        return skills;
    }

    public boolean hasSkill(String skill) {

        skill = StringUtils.formatName(skill);
        return skillFactories.containsKey(skill);
    }
}
