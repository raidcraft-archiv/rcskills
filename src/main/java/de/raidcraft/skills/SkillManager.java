package de.raidcraft.skills;

import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.loader.JarFilesSkillLoader;

import java.io.File;
import java.util.*;

/**
 * @author Silthus
 */
public final class SkillManager extends JarFilesSkillLoader {

    private final SkillsPlugin plugin;
    private final Map<String, SkillFactory> skillFactories = new HashMap<>();
    // holds skills that were already loaded for that player
    private final Map<String, Map<String, Skill>> playerSkills = new HashMap<>();
    // holds all skills that are triggered every few ticks
    private final List<Skill> passiveSkills = new ArrayList<>();

    public SkillManager(SkillsPlugin plugin) {

        super(plugin.getLogger(), new File(plugin.getDataFolder(), "/skills/"));
        this.plugin = plugin;

        for (Class<? extends Skill> clazz : loadSkillClasses()) {
            skillFactories.put(clazz.getAnnotation(SkillInformation.class).name().toLowerCase(), new SkillFactory(plugin, clazz));
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
            skillFactories.put(clazz.getAnnotation(SkillInformation.class).name().toLowerCase(), new SkillFactory(plugin, clazz));
        } else {
            plugin.getLogger().warning("Found skill without SkillInformation: " + clazz.getCanonicalName());
        }
    }

    public Skill getSkill(Hero hero, String skill) throws UnknownSkillException {

        skill = skill.toLowerCase();
        if (!skillFactories.containsKey(skill)) {
            throw new UnknownSkillException("Es gibt keinen Skill mit dem Namen: " + skill);
        }
        if (!playerSkills.containsKey(hero.getUserName())) {
            playerSkills.put(hero.getUserName(), new HashMap<String, Skill>());
        }
        if (!playerSkills.get(hero.getUserName()).containsKey(skill)) {
            throw new UnknownSkillException("Der Spieler " + hero.getUserName() + " hat den Skill " + skill + " nicht.");
        }
        return playerSkills.get(hero.getUserName()).get(skill);
    }

    protected Skill getSkill(Hero hero, ProfessionFactory professionFactory, String skill) throws UnknownSkillException {

        skill = skill.toLowerCase();
        if (!skillFactories.containsKey(skill)) {
            throw new UnknownSkillException("Es gibt keinen Skill mit dem Namen: " + skill);
        }
        if (!playerSkills.containsKey(hero.getUserName())) {
            playerSkills.put(hero.getUserName(), new HashMap<String, Skill>());
        }
        if (!playerSkills.get(hero.getUserName()).containsKey(skill)) {
            // create a new skill instance for this hero and profession
            playerSkills.get(hero.getUserName()).put(skill, skillFactories.get(skill).create(hero, professionFactory));
        }
        return playerSkills.get(hero.getUserName()).get(skill);
    }

    public Collection<? extends Skill> getAllSkills() {

        Collection<Skill> skills = new ArrayList<>();
        for (Map<String, Skill> entry : playerSkills.values()) {
            skills.addAll(entry.values());
        }
        return skills;
    }
}
