package de.raidcraft.skills.api.skill;

import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author mdoering
 */
public class PlayerCastSkillEvent extends Event {

    private final SkillAction skillAction;

    public PlayerCastSkillEvent(SkillAction skillAction) {

        this.skillAction = skillAction;
    }

    public SkillAction getSkillAction() {

        return skillAction;
    }

    public Hero getHero() {

        return getSkill().getHolder();
    }

    public Skill getSkill() {

        return getSkillAction().getSkill();
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
