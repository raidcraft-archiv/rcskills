package de.raidcraft.skills.api.group;

import de.raidcraft.skills.api.hero.Hero;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Silthus
 */
public class SimpleGroup implements Group {

    private Hero owner;
    private final Set<Hero> members = new HashSet<>();

    public SimpleGroup(Hero owner) {

        this.owner = owner;
        members.add(owner);
    }

    @Override
    public Hero getOwner() {

        return owner;
    }

    @Override
    public void setOwner(Hero owner) {

        this.owner = owner;
        addMember(owner);
    }

    @Override
    public void sendMessage(String... msg) {

        for (Hero hero : members) {
            hero.sendMessage(msg);
        }
    }

    @Override
    public Set<Hero> getMembers() {

        return members;
    }

    @Override
    public void addMember(Hero hero) {

        members.add(hero);
        hero.joinGroup(this);
    }

    @Override
    public void removeMember(Hero hero) {

        members.remove(hero);
        hero.leaveGroup(this);
    }

    @Override
    public boolean isInGroup(Hero hero) {

        return members.contains(hero);
    }
}
