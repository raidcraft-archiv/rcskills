package de.raidcraft.skills.skills;

import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillData;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.config.ProfessionConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class SkillManager {

    private final SkillsPlugin plugin;
    private final SkillFactory factory;
    private final Map<String, Class<? extends Skill>> skillClasses = new HashMap<>();
    // holds skills that were already loaded for that player
    private final Map<String, Map<String, Skill>> playerSkills = new HashMap<>();

    public SkillManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        this.factory = new SkillFactory(plugin);
    }

    /**
     * Registers a skill directly with the skill manager making it possible to use that skill.
     * Skills in external files are loaded via our own class loader on loadSkillClasses() call.
     *
     * @param clazz of the skill
     */
    public void registerSkill(Class<? extends Skill> clazz) {

        if (clazz.isAnnotationPresent(SkillInformation.class)) {
            skillClasses.put(clazz.getAnnotation(SkillInformation.class).name(), clazz);
        } else {
            plugin.getLogger().warning("Found skill without SkillInformation: " + clazz.getCanonicalName());
        }
    }

    public void loadSkillClasses() {

    }

    /**
     * Loads the given skill for the given hero from cache or creates a new instance.
     *
     * @param hero to load skill for
     * @param skill to load
     * @return loaded skill
     */
    public Skill loadSkill(Hero hero, String skill, ProfessionConfig config) throws UnknownSkillException {

        String name = hero.getName();
        if (!playerSkills.containsKey(name)) {
            playerSkills.put(name, new HashMap<String, Skill>());
        }
        if (!playerSkills.get(name).containsKey(skill)) {
            // first check if the skill is registered
            if (!skillClasses.containsKey(skill)) {
                throw new UnknownSkillException("There is no registered Skill with the name: " + skill);
            }
            // create a new skill for the player
            playerSkills.get(name).put(skill, loadSkill(hero, skillClasses.get(skill), config));
        }
        return playerSkills.get(name).get(skill);
    }

    public Skill loadSkill(Hero hero, Class<? extends Skill> clazz, ProfessionConfig config) throws UnknownSkillException {

        // its reflection time yay!
        try {
            Constructor<? extends Skill> constructor = clazz.getConstructor(Hero.class, SkillData.class);
            constructor.setAccessible(true);
            return constructor.newInstance(hero, plugin.getSkillConfig(hero, clazz.getAnnotation(SkillInformation.class), config));
        } catch (NoSuchMethodException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        } catch (InstantiationException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
        throw new UnknownSkillException("Error when loading skill for class: " + clazz.getCanonicalName());
    }
}
