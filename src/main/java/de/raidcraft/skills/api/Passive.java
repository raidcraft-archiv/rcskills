package de.raidcraft.skills.api;

import de.raidcraft.skills.api.hero.Hero;

/**
 * @author Silthus
 */
public interface Passive {

    /**
     * Applies the effect to the hero. Is called every few ticks automatically.
     * It will be called after checking if the effect applies. So no extra
     * checking is needed.
     *
     * @param hero to apply effect to.
     */
    public void apply(Hero hero);
}
