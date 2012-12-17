package de.raidcraft.skills.api.combat.attack;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.CombatException;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * @author Silthus
 */
public class EnvironmentAttack extends AbstractAttack {

    public EnvironmentAttack(EntityDamageByEntityEvent event, double damageModifier) {

        super(null,
                RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter((LivingEntity) event.getEntity()),
                (int) (event.getDamage() * damageModifier));
    }

    @Override
    public void run() throws CombatException, InvalidTargetException {

        // TODO: check resistance and the fancy stuff
        getTarget().damage(getDamage());
    }
}
