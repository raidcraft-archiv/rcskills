package de.raidcraft.skills.professions;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillManager;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.AttachedLevel;
import de.raidcraft.skills.api.level.NullAttachedLevel;
import de.raidcraft.skills.api.path.Path;
import de.raidcraft.skills.api.persistance.ProfessionProperties;
import de.raidcraft.skills.api.profession.AbstractProfession;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.tables.THeroProfession;
import de.raidcraft.skills.tables.THeroSkill;
import org.bukkit.ChatColor;

import java.util.Collection;
import java.util.List;

/**
 * @author Silthus
 */
public final class VirtualProfession extends AbstractProfession {


    public VirtualProfession(Hero hero, ProfessionProperties data, Path<Profession> path, THeroProfession database) {

        super(hero, data, path, null, database);
        attachLevel(new NullAttachedLevel(this, database));
        // lets save the virtual profession when its loaded
        save();
    }

    @Override
    public Collection<Skill> getSkills() {

        if (skills.size() < 1) {
            // at this point we normally load the skills from the config
            // but virtual skills can only be given manually

            // also load all skills that are only added in the db
            SkillManager skillManager = RaidCraft.getComponent(SkillsPlugin.class).getSkillManager();
            List<THeroSkill> dbSkills = database.getSkills();
            if (dbSkills != null) {
                for (THeroSkill tHeroSkill : dbSkills) {
                    try {
                        Skill skill = skillManager.getSkill(getHero(), this, tHeroSkill.getName());
                        this.skills.put(skill.getName(), skill);
                    } catch (UnknownSkillException e) {
                        getHero().sendMessage(ChatColor.RED + e.getMessage());
                    }
                }
            }
        }
        return skills.values();
    }

    @Override
    public void addSkill(Skill skill) {

        this.skills.put(skill.getName(), skill);
        skill.unlock();
        skill.save();
    }

    @Override
    public void removeSkill(Skill skill) {

        this.skills.remove(skill);
        skill.lock();
        skill.save();
    }

    @Override
    public void checkSkillsForUnlock() {
        // do nothing
    }

    @Override
    public boolean isActive() {

        return true;
    }

    @Override
    public boolean isMastered() {

        return true;
    }

    @Override
    public void saveLevelProgress(AttachedLevel<Profession> attachedLevel) {
        // we dont want to save this stuff
    }

    @Override
    public void onExpGain(int exp) {

    }

    @Override
    public void onExpLoss(int exp) {

    }

    @Override
    public void onLevelGain() {

    }

    @Override
    public void onLevelLoss() {

    }
}
