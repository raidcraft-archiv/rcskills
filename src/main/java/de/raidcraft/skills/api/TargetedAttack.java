package de.raidcraft.skills.api;

import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public interface TargetedAttack extends Active<LivingEntity> {

    @Override
    public void run(Hero hero, LivingEntity target) throws CombatException;
}
