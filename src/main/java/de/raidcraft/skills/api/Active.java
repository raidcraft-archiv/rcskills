package de.raidcraft.skills.api;

import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;

/**
 * @author Silthus
 */
public interface Active<T> {

    public void run(Hero hero, T trigger) throws CombatException;
}
