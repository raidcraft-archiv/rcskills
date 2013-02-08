package de.raidcraft.skills.api.path;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.inheritance.Parent;

import java.util.List;

/**
 * @author Silthus
 */
public interface Path<T extends Parent> {

    public String getName();

    public String getFriendlyName();

    /**
     * Will return a list of the very first parents.
     * The parent/child inheritance will take over from there.
     *
     * @param hero the hero the get the parents for
     * @return root parents from which more childs fork off
     */
    public List<T> getParents(Hero hero);

    /**
     * Gets a list of all the parents names.
     *
     * @return list of names defined in the config
     */
    public List<String> getParents();
}
