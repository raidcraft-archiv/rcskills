package de.raidcraft.skills.api.combat.effect;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.skill.Skill;

/**
 * An effect is something that can be applied to entities around in the world.
 * An effect can also be scheduled to apply periodically or after a set time.
 *
 * @author Silthus
 */
public interface Effect {

    public enum Type {


    }

    public void apply(CharacterTemplate source, CharacterTemplate target) throws CombatException;

    public String getName();

    public String getDescription();

    public Type[] getTypes();

    public boolean hasType(Type type);

    public Skill getSkill();

    public double getPriority();

    public void setTaskId(int taskId);

    public int getTaskId();

    public int getDamage();

    public void setDamage(int damage);

    public Effect setTotalDuration(int duration);

    public int getTotalDuration();

    public int getDuration();

    public void increaseDuration();

    public Effect setDelay(int delay);

    public int getDelay();

    public Effect setInterval(int interval);

    public int getInterval();
}
