package de.raidcraft.skills.api.party;

import de.raidcraft.skills.api.character.CharacterTemplate;

import java.util.Set;

/**
 * @author Silthus
 */
public interface Party {

    public CharacterTemplate getOwner();

    public void setOwner(CharacterTemplate owner);

    public void sendMessage(String... msg);

    public Set<CharacterTemplate> getMembers();

    public void addMember(CharacterTemplate member);

    public void removeMember(CharacterTemplate member);

    public boolean isInGroup(CharacterTemplate member);

    public void heal(int amount);
}
