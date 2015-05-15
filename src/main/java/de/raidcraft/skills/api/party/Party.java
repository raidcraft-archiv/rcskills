package de.raidcraft.skills.api.party;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.hero.Hero;

import java.util.Set;

/**
 * @author Silthus
 */
public interface Party {

    CharacterTemplate getOwner();

    void setOwner(CharacterTemplate owner);

    boolean isOwner(CharacterTemplate character);

    void sendMessage(String... msg);

    Set<CharacterTemplate> getMembers();

    Set<Hero> getHeroes();

    void addMember(CharacterTemplate member);

    void inviteMember(Hero hero);

    boolean isInvited(Hero hero);

    void removeMember(CharacterTemplate member);

    void kickMember(Hero hero);

    void dispandParty();

    boolean contains(CharacterTemplate member);

    <S> void heal(S source, int amount);
}
