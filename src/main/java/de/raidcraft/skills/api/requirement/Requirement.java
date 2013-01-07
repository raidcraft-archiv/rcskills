package de.raidcraft.skills.api.requirement;

import de.raidcraft.skills.api.hero.Hero;

/**
 * @author Silthus
 */
public interface Requirement<T> {

    public T getType();

    public boolean isMet(Hero hero);

    public String getLongReason(Hero hero);

    public String getShortReason(Hero hero);
}
