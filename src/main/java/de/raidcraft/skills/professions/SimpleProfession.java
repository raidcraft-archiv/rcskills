package de.raidcraft.skills.professions;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.ProfessionProperties;
import de.raidcraft.skills.api.profession.AbstractProfession;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.tables.THeroProfession;
import org.bukkit.ChatColor;

/**
 * Represents a profession instantiated for one {@link Hero}.
 * Each single {@link Hero} can have multiple {@link Profession}s at a time. The
 * {@link Hero} will hold a reference to all {@link Profession}s obtained by the hero.
 *
 * @author Silthus
 */
public class SimpleProfession extends AbstractProfession {


    public SimpleProfession(Hero hero, ProfessionProperties properties, THeroProfession database) {

        super(hero, properties, database);
        attachLevel(new ProfessionLevel(this, database));
    }

    @Override
    public void onExpGain(int exp) {

        getHero().getUserInterface().refresh();
    }

    @Override
    public void onExpLoss(int exp) {

        getHero().getUserInterface().refresh();
    }

    @Override
    public void onLevelGain(int level) {

        // lets reset all stats to max
        getHero().reset();
        getHero().sendMessage(ChatColor.GREEN + "Du bist ein Level aufgestiegen: " +
                ChatColor.AQUA + getProperties().getFriendlyName() +
                ChatColor.ITALIC + ChatColor.YELLOW + " Level " + getLevel().getLevel());
        // check all skills and if we need to unlock any
        for (Skill skill : getSkills()) {
            if (!skill.isUnlocked() && skill.getProperties().getRequiredLevel() <= getLevel().getLevel()) {
                skill.unlock();
            }
        }
    }

    @Override
    public void onLevelLoss(int level) {

        getHero().sendMessage(ChatColor.RED + "Du bist ein Level abgestiegen: " +
                ChatColor.AQUA + getProperties().getFriendlyName() +
                ChatColor.ITALIC + ChatColor.YELLOW + " Level " + getLevel().getLevel());
        // check if we need to lock any skills
        for (Skill skill : getSkills()) {
            if (skill.isUnlocked() && skill.getProperties().getRequiredLevel() > getLevel().getLevel()) {
                skill.lock();
            }
        }
    }
}
