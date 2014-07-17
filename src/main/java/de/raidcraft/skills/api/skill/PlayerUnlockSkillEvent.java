package de.raidcraft.skills.api.skill;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author mdoering
 */
public class PlayerUnlockSkillEvent extends Event {

    private final Skill skill;

    public PlayerUnlockSkillEvent(Skill skill) {

        this.skill = skill;
    }

    public Hero getHero() {

        return getSkill().getHolder();
    }

    public Skill getSkill() {

        return skill;
    }

    public Profession getProfession() {

        return getSkill().getProfession();
    }

    public Player getPlayer() {

        return getHero().getPlayer();
    }

    //<-- Handler -->//
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}
