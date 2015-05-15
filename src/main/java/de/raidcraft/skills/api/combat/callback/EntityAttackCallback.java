package de.raidcraft.skills.api.combat.callback;

import de.raidcraft.skills.api.combat.action.EntityAttack;
import de.raidcraft.skills.api.exceptions.CombatException;

/**
 * @author Silthus
 */
public interface EntityAttackCallback extends Callback<EntityAttack> {

    @Override
    void run(EntityAttack attack) throws CombatException;
}
