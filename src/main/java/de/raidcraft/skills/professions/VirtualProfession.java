package de.raidcraft.skills.professions;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillManager;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.VirtualProfessionAttachedLevel;
import de.raidcraft.skills.api.path.Path;
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


    public VirtualProfession(Hero hero, ProfessionProperties data, Path<Profession> path, THeroProfession database) {

        super(hero, data, path, null, database);
        attachLevel(new VirtualProfessionAttachedLevel(this, database));
        // lets save the virtual profession when its loaded
        save();
    }

    @Override
    public void loadSkills() {

        super.loadSkills();
        // also load all skills that are only added in the db
        SkillManager skillManager = RaidCraft.getComponent(SkillsPlugin.class).getSkillManager();
        List<THeroSkill> dbSkills = RaidCraft.getDatabase(SkillsPlugin.class).find(THeroProfession.class, getId()).getSkills();
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

    @Override
    public void addSkill(Skill skill) {

        this.skills.put(skill.getName(), skill);
        skill.unlock();
        skill.save();
    }

    @Override
    public void removeSkill(Skill skill) {

        this.skills.remove(skill.getName());
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
    public void onExpGain(int exp) {

    }

    @Override
    public void onExpLoss(int exp) {

    }

    @Override
    public void onLevelGain() {

        checkSkillsForUnlock();
        getHero().sendMessage(ChatColor.GREEN + "Dein Server Rang hat sich erhöht: " + ChatColor.AQUA + "Rang " + getAttachedLevel().getLevel());
    }

    @Override
    public void onLevelLoss() {

        checkSkillsForUnlock();
        getHero().sendMessage(ChatColor.RED + "Dein Server Rang hat sich verringert: " + ChatColor.AQUA + "Rang " + getAttachedLevel().getLevel());
    }
}
