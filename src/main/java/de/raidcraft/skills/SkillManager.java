package de.raidcraft.skills;

import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.loader.GenericJarFileManager;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.api.skill.type.Passive;
import de.raidcraft.skills.util.StringUtil;

import java.util.*;

/**
 * @author Silthus
 */
public final class SkillManager extends GenericJarFileManager<Skill> {

    private final Map<String, SkillFactory> skillFactories = new HashMap<>();
    // holds skills that were already loaded for that player
    private final Map<String, Map<String, Skill>> playerSkills = new HashMap<>();
    // holds all skills that are triggered every few ticks
    private final List<Skill> passiveSkills = new ArrayList<>();

    protected SkillManager(SkillsPlugin plugin) {

        super(Skill.class, plugin);
    }

    @Override
    protected void loadFactories() {

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
    public void registerClass(Class<? extends Skill> skillClass) {

        if (skillClass.isAnnotationPresent(SkillInformation.class)) {
            String skillName = StringUtil.formatName(skillClass.getAnnotation(SkillInformation.class).name());
            // check for duplicate skills
            if (skillFactories.containsKey(skillName)) {
                plugin.getLogger().warning("Found duplicate Skill: " + skillName);
                return;
            }
            // load the skill factory for the non alias
            SkillFactory factory = plugin.configure(new SkillFactory(plugin, skillClass, configDir));
            skillFactories.put(skillName, factory);
            // lets put a duplicate of the factory into the map as alias
            if (plugin.getAliasesConfig().hasSkillAliasFor(skillName)) {
                String alias = plugin.getAliasesConfig().getSkillAliasFor(skillName);
                if (alias.equalsIgnoreCase(skillName)) {
                    plugin.getLogger().warning("There is already a skill for the alias: " + skillName);
                    return;
                }
                if (skillFactories.containsKey(alias)) {
                    plugin.getLogger().warning("Found duplicate alias: " + alias);
                    return;
                }
                factory = plugin.configure(new SkillFactory(plugin, skillClass, configDir));
                skillFactories.put(alias, factory);
            }
        } else {
            plugin.getLogger().warning("Found skill without SkillInformation: " + skillClass.getCanonicalName());
        }
    }

    public Skill getSkill(Hero hero, String skill) throws UnknownSkillException {

        skill = StringUtil.formatName(skill);
        if (!skillFactories.containsKey(skill)) {
            throw new UnknownSkillException("Es gibt keinen Skill mit dem Namen: " + skill);
        }
        if (!playerSkills.containsKey(hero.getName())) {
            playerSkills.put(hero.getName(), new HashMap<String, Skill>());
        }
        if (!playerSkills.get(hero.getName()).containsKey(skill)) {
            throw new UnknownSkillException("Der Spieler " + skill + " hat den Skill " + skill + " nicht.");
        }
        return playerSkills.get(hero.getName()).get(skill);
    }

    protected Skill getSkill(Hero hero, Profession profession, String skillName) throws UnknownSkillException {

        Skill skill;
        skillName = StringUtil.formatName(skillName);
        if (!skillFactories.containsKey(skillName)) {
            throw new UnknownSkillException("Es gibt keinen Skill mit dem Namen: " + skillName);
        }
        if (!playerSkills.containsKey(hero.getName())) {
            playerSkills.put(hero.getName(), new HashMap<String, Skill>());
        }
        if (!playerSkills.get(hero.getName()).containsKey(skillName)) {
            // lets check the aliases
            if (plugin.getAliasesConfig().hasSkill(skillName)) {
                // invoke the alias method
                skill = skillFactories.get(skillName).create(hero, profession, skillName);
            } else {
                // create a new skill instance for this hero and profession
                skill = skillFactories.get(skillName).create(hero, profession);
            }
            playerSkills.get(hero.getName()).put(skillName, skill);
            // add skill to our passive list if it is a passive skill
            if (skill instanceof Passive) {
                passiveSkills.add(skill);
            }
        } else {
            skill = playerSkills.get(hero.getName()).get(skillName);
        }
        return skill;
    }

    public SkillFactory getFactory(String name) throws UnknownSkillException {

        if (skillFactories.containsKey(name)) {
            return skillFactories.get(name);
        }
        throw new UnknownSkillException("Es gibt keinen Skill mit dem Namen: " + name);
    }

    public SkillFactory getFactory(Skill skill) {

        return skillFactories.get(skill.getName());
    }

    public Collection<? extends Skill> getAllSkills() {

        Collection<Skill> skills = new ArrayList<>();
        for (Map<String, Skill> entry : playerSkills.values()) {
            skills.addAll(entry.values());
        }
        return skills;
    }

    public boolean hasSkill(String skill) {

        skill = StringUtil.formatName(skill);
        return skillFactories.containsKey(skill);
    }
}
