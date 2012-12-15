package de.raidcraft.skills.api.skill.type;

import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;

/**
 * @author Silthus
 */
public interface Active<T> {

    /**
     * Called by the dispatcher after everything else has been checked.
     * That means, resource cost, range, valid target and so on.
     *
     * So when processing this method only focus on executing the spell/skill.
     *
     * @param hero that triggered the skill
     * @param trigger of the skill
     * @throws CombatException is thrown when an error in the skill execution occured
     */
    public void run(Hero hero, T trigger) throws CombatException;
}
