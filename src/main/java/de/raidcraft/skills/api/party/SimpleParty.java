package de.raidcraft.skills.api.party;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.hero.Hero;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Silthus
 */
public class SimpleParty implements Party {

    private CharacterTemplate owner;
    private final Set<CharacterTemplate> members = new HashSet<>();

    public SimpleParty(CharacterTemplate owner) {

        this.owner = owner;
        members.add(owner);
    }

    @Override
    public CharacterTemplate getOwner() {

        return owner;
    }

    @Override
    public void setOwner(CharacterTemplate owner) {

        this.owner = owner;
        addMember(owner);
    }

    @Override
    public void sendMessage(String... msg) {

        for (CharacterTemplate member : members) {
            if (member instanceof Hero) {
                ((Hero) member).sendMessage(msg);
            }
        }
    }

    @Override
    public Set<CharacterTemplate> getMembers() {

        return members;
    }

    @Override
    public void addMember(CharacterTemplate member) {

        members.add(member);
        member.joinParty(this);
    }

    @Override
    public void removeMember(CharacterTemplate member) {

        members.remove(member);
        member.leaveParty(this);
    }

    @Override
    public boolean isInGroup(CharacterTemplate member) {

        return members.contains(member);
    }

    @Override
    public void heal(int amount) {

        for (CharacterTemplate hero : getMembers()) {
            hero.heal(amount);
        }
    }
}
