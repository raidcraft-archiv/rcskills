package de.raidcraft.skills;

import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.loader.GenericJarFileManager;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.IgnoredSkill;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.config.AliasesConfig;
import de.raidcraft.skills.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public final class SkillManager extends GenericJarFileManager<Skill> {

    private final SkillsPlugin plugin;
    private final Map<String, SkillFactory> skillFactories = new HashMap<>();
    private final Map<String, Class<? extends Skill>> skillClasses = new HashMap<>();
    // holds skills that were already loaded for that player
    private final Map<String, Set<Skill>> playerSkills = new HashMap<>();

    protected SkillManager(SkillsPlugin plugin) {

        super(Skill.class, new File(plugin.getDataFolder(), plugin.getCommonConfig().skill_jar_path));
        this.plugin = plugin;
        // create the config path
        new File(plugin.getDataFolder(), plugin.getCommonConfig().skill_config_path).mkdirs();
    }

    public void reload() {

        skillFactories.clear();
        playerSkills.clear();
        loadFactories();
    }

    @Override
    public void loadFactories() {

        for (Class<? extends Skill> clazz : loadClasses()) {
            try {
                registerClass(clazz);
            } catch (UnknownSkillException e) {
                plugin.getLogger().warning(e.getMessage());
            }
        }
    }

    /**
     * Registers a skill directly with the skill manager making it possible to use that skill.
     * Skills in external files are loaded via our own class loader on loadSkillClasses() call.
     *
     * @param skillClass of the skill
     */
    public void registerClass(Class<? extends Skill> skillClass) throws UnknownSkillException {

        if (skillClass.isAnnotationPresent(IgnoredSkill.class)) {
            return;
        }

        if (skillClass.isAnnotationPresent(SkillInformation.class)) {
            String skillName = StringUtils.formatName(skillClass.getAnnotation(SkillInformation.class).name());
            // check for duplicate skills
            if (skillFactories.containsKey(skillName)) {
                plugin.getLogger().warning("Found duplicate Skill: " + skillName);
            }
            // load the skill factory
            SkillFactory factory = new SkillFactory(plugin, skillClass, skillName);
            skillFactories.put(skillName, factory);
            skillClasses.put(skillName, skillClass);
            // lets create the skill once to make a default config
            factory.createDefaults();
            plugin.getLogger().info("Loaded Skill: " + factory.getSkillName());
        } else {
            plugin.getLogger().warning("Found skill without SkillInformation: " + skillClass.getCanonicalName());
        }
    }

    protected void createAliasFactory(String alias, String skill, AliasesConfig config) {

        try {
            SkillFactory factory = new SkillFactory(plugin, skillClasses.get(skill), skill, config);
            skillFactories.put(alias, factory);
            factory.createDefaults();
            plugin.getLogger().info("Loaded Alias: " + alias + " -> " + skill);
        } catch (UnknownSkillException e) {
            plugin.getLogger().warning(e.getMessage());
        }
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
        skill.getProperties().loadRequirements(skill);
        playerSkills.get(hero.getName()).add(skill);
        // lets add the skill as a trigger handler
        if (skill instanceof Triggered) {
            TriggerManager.registerListeners((Triggered) skill);
        }
        return skill;
    }

    public SkillFactory getFactory(Skill skill) {

        return getFactory(skill.getName());
    }

    protected SkillFactory getFactory(String name) {

        return skillFactories.get(name);
    }

    public Collection<? extends Skill> getAllSkills(Hero hero) {

        List<Skill> skills = new ArrayList<>();
        for (Profession profession : plugin.getProfessionManager().getAllProfessions(hero)) {
            skills.addAll(profession.getSkills());
        }
        return skills;
    }

    public Collection<? extends Skill> getAllVirtualSkills(Hero hero) {

        List<Skill> skills = new ArrayList<>();
        // also add a virtual skill of all
        for (String skillName : skillFactories.keySet()) {
            try {
                Skill skill = getSkill(hero, plugin.getProfessionManager().getVirtualProfession(hero), skillName);
                skills.add(skill);
            } catch (UnknownSkillException e) {
                hero.sendMessage(ChatColor.RED + e.getMessage());
                plugin.getLogger().warning(e.getMessage());
            }
        }
        return skills;
    }

    protected Collection<String> getSkillsFor(Class<? extends Skill> sClass) {

        Set<String> skills = new HashSet<>();
        for (Map.Entry<String, Class<? extends Skill>> entry : skillClasses.entrySet()) {
            if (entry.getValue() == sClass) {
                skills.add(entry.getKey());
            }
        }
        return skills;
    }

    public boolean hasSkill(String skill) {

        skill = StringUtils.formatName(skill);
        return skillFactories.containsKey(skill);
    }

    public void clearCacheOf(String player) {

        playerSkills.remove(player);
    }
}
