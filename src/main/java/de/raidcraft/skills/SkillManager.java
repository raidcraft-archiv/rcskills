package de.raidcraft.skills;

import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillData;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.loader.JarFilesSkillLoader;
import de.raidcraft.skills.tables.THeroProfession;
import de.raidcraft.skills.tables.THeroSkill;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class SkillManager extends JarFilesSkillLoader {

    private final SkillsPlugin plugin;
    private final Map<String, Class<? extends Skill>> skillClasses = new HashMap<>();
    // holds skills that were already loaded for that player
    private final Map<String, Map<String, Skill>> playerSkills = new HashMap<>();

    public SkillManager(SkillsPlugin plugin) {

        super(plugin.getLogger(), new File(plugin.getDataFolder(), "/skills/"));
        this.plugin = plugin;

        for (Class<? extends Skill> clazz : loadSkillClasses()) {
            skillClasses.put(clazz.getAnnotation(SkillInformation.class).name(), clazz);
        }
    }

    public void createDefaults() {

        // simply create a factory of every skill that will trigger the default creation
        for (Map.Entry<String, Class<? extends Skill>> entry : skillClasses.entrySet()) {
            new SkillFactory(plugin, entry.getValue().getAnnotation(SkillInformation.class));
        }
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

    public Skill getSkill(Hero hero, String skill) throws UnknownSkillException {

        if (!playerSkills.containsKey(hero.getUserName())) {
            playerSkills.put(hero.getUserName(), new HashMap<String, Skill>());
        }
        if (!playerSkills.get(hero.getUserName()).containsKey(skill) && !hero.hasSkill(skill)) {
            throw new UnknownSkillException("Der Spieler " + hero.getUserName() + " hat den Skill " + skill + " nicht.");
        }
        return playerSkills.get(hero.getUserName()).get(skill);
    }

    /**
     * Loads the given skill for the given hero from cache or creates a new instance.
     *
     * @param hero to load skill for
     * @param skill to load
     * @return loaded skill
     */
    public Skill loadSkill(Hero hero, String skill, ProfessionFactory factory) throws UnknownSkillException {

        String name = hero.getUserName();
        if (!playerSkills.containsKey(name)) {
            playerSkills.put(name, new HashMap<String, Skill>());
        }
        if (!playerSkills.get(name).containsKey(skill)) {
            // first check if the skill is registered
            if (!skillClasses.containsKey(skill)) {
                throw new UnknownSkillException("There is no registered Skill with the name: " + skill);
            }
            // create a new skill for the player
            Class<? extends Skill> aClass = skillClasses.get(skill);
            playerSkills.get(name).put(skill,
                    createSkill(hero, aClass, new SkillFactory(plugin, hero, aClass.getAnnotation(SkillInformation.class), factory)));
        }
        return playerSkills.get(name).get(skill);
    }

    private Skill createSkill(Hero hero, Class<? extends Skill> clazz, SkillFactory factory) throws UnknownSkillException {

        // its reflection time yay!
        try {
            Constructor<? extends Skill> constructor = clazz.getConstructor(Hero.class, SkillData.class);
            constructor.setAccessible(true);
            return constructor.newInstance(hero, factory);
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

    public Skill loadSkill(Hero hero, THeroSkill skill, THeroProfession profession) throws UnknownSkillException, UnknownProfessionException {

        return loadSkill(hero, skill.getName(), new ProfessionFactory(plugin, hero, profession));
    }

    protected Skill loadSkill(Hero hero, SkillInformation information, SkillFactory skillFactory) {

        if (!playerSkills.containsKey(hero.getUserName())) {
            playerSkills.put(hero.getUserName(), new HashMap<String, Skill>());
        }
        if (playerSkills.get(hero.getUserName()).containsKey(information.name())) {
            return playerSkills.get(hero.getUserName()).get(information.name());
        }
        try {
            // create the skill
            return createSkill(hero, skillClasses.get(information.name()), skillFactory);
        } catch (UnknownSkillException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Collection<? extends Skill> getAllSkills() {

        Collection<Skill> skills = new ArrayList<>();
        for (Map<String, Skill> entry : playerSkills.values()) {
            skills.addAll(entry.values());
        }
        return skills;
    }
}
