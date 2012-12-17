package de.raidcraft.skills.api.combat.attack;

import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;

/**
 * @author Silthus
 */
public interface Attack {

    public CharacterTemplate getAttacker();

    public CharacterTemplate getTarget();

    public int getDamage();

    public void setDamage(int damage);

    public void run() throws CombatException, InvalidTargetException;
}
