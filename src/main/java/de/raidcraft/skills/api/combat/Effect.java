package de.raidcraft.skills.api.combat;

import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.entity.LivingEntity;

/**
 * An effect is something that can be applied to entities around in the world.
 * An effect can also be scheduled to apply periodically or after a set time.
 *
 * @author Silthus
 */
public interface Effect {

    public void apply(Hero source, LivingEntity target) throws CombatException;

    public String getName();

    public String getDescription();

    public Skill getSkill();

    public void setTaskId(int taskId);

    public int getTaskId();

    public Effect setDuration(int duration);

    public int getDuration();

    public Effect setDelay(int delay);

    public int getDelay();

    public Effect setInterval(int interval);

    public int getInterval();
}
