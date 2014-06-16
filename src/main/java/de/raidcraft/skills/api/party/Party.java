package de.raidcraft.skills.api.party;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.hero.Hero;

import java.util.Set;

/**
 * @author Silthus
 */
public interface Party {

    public CharacterTemplate getOwner();

    public void setOwner(CharacterTemplate owner);

    public boolean isOwner(CharacterTemplate character);

    public void sendMessage(String... msg);

    public Set<CharacterTemplate> getMembers();

    public Set<Hero> getHeroes();

    public void addMember(CharacterTemplate member);

    public void inviteMember(Hero hero);

    public boolean isInvited(Hero hero);

    public void removeMember(CharacterTemplate member);

    public void kickMember(Hero hero);

    public void dispandParty();

    public boolean contains(CharacterTemplate member);

    public <S> void heal(S source, int amount);
}
