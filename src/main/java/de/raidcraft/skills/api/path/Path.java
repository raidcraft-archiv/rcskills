package de.raidcraft.skills.api.path;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.inheritance.Parent;

import java.util.List;

/**
 * @author Silthus
 */
public interface Path<T extends Parent> {

    String getName();

    String getFriendlyName();

    int getPriority();

    /**
     * Counts together the current levels of all active professions in the path.
     * Should be used as a total level of the hero, e.g. to adjust difficulty
     *
     * @return sum of levels for all active professions in the path
     */
    int getTotalPathLevel(Hero hero);

    boolean isSelectedInCombat();

    boolean isSelectedOutOfCombat();

    /**
     * Will return a list of the very first parents.
     * The parent/child inheritance will take over from there.
     *
     * @param hero the hero the get the parents for
     *
     * @return root parents from which more childs fork off
     */
    List<T> getParents(Hero hero);

    /**
     * Gets a list of all the parents names.
     *
     * @return list of names defined in the config
     */
    List<String> getParents();
}
