package de.raidcraft.skills.api.group;

import de.raidcraft.skills.api.hero.Hero;

import java.util.Set;

/**
 * @author Silthus
 */
public interface Group {

    public Hero getOwner();

    public void setOwner(Hero owner);

    public void sendMessage(String... msg);

    public Set<Hero> getMembers();

    public void addMember(Hero hero);

    public void removeMember(Hero hero);

    public boolean isInGroup(Hero hero);
}
