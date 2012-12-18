package de.raidcraft.skills.api.skill.type;

import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;

/**
 * @author Silthus
 */
public interface TargetedAttack extends Active<CharacterTemplate> {

    @Override
    public void run(Hero hero, CharacterTemplate target) throws CombatException, InvalidTargetException;
}
