package de.raidcraft.skills.professions;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillManager;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.persistance.ProfessionProperties;
import de.raidcraft.skills.api.profession.AbstractProfession;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.tables.THeroProfession;
import de.raidcraft.skills.tables.THeroSkill;
import org.bukkit.ChatColor;

import java.util.List;

/**
 * @author Silthus
 */
public final class VirtualProfession extends AbstractProfession {

    private final Level<Profession> level;

    public VirtualProfession(Hero hero, ProfessionProperties data, THeroProfession database) {

        super(hero, data, database);
        this.level = new NullLevel(this, database);
        // lets save the virtual profession when its loaded
        save();
    }

    @Override
    public List<Skill> getSkills() {

        if (skills == null || skills.size() < 1) {
            this.skills = getProperties().loadSkills(getHero(), this);
            // also load all skills that are only added in the db
            SkillManager skillManager = RaidCraft.getComponent(SkillsPlugin.class).getSkillManager();
            List<THeroSkill> dbSkills = database.getSkills();
            if (dbSkills != null) {
                for (THeroSkill tHeroSkill : dbSkills) {
                    try {
                        this.skills.add(skillManager.getSkill(getHero(), this, tHeroSkill.getName()));
                    } catch (UnknownSkillException e) {
                        getHero().sendMessage(ChatColor.RED + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
        return skills;
    }

    @Override
    public void addSkill(Skill skill) {

        this.skills.add(skill);
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
    public boolean isActive() {

        return true;
    }

    @Override
    public boolean isMastered() {

        return true;
    }

    @Override
    public Level<Profession> getLevel() {

        return level;
    }

    @Override
    public void saveLevelProgress(Level<Profession> level) {
        // we dont want to save this stuff
    }

    @Override
    public void onExpGain(int exp) {}

    @Override
    public void onExpLoss(int exp) {}

    @Override
    public void onLevelGain(int level) {}

    @Override
    public void onLevelLoss(int level) {}
}
