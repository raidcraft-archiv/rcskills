package de.raidcraft.skills.api.combat.action;

import de.raidcraft.skills.CombatManager;
import de.raidcraft.skills.api.ability.Ability;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.trigger.AttackTrigger;
import de.raidcraft.skills.trigger.DamageTrigger;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * @author Silthus
 */
public class EffectDamage extends AbstractAttack<Effect<Ability>, CharacterTemplate> {

    public EffectDamage(Effect<Ability> skillEffect, double damage) {

        super(skillEffect, skillEffect.getTarget(), damage, skillEffect.getTypes());
    }

    @Override
    public void run() throws CombatException {

        EntityDamageByEntityEvent event = CombatManager.fakeDamageEvent(getSource().getSource().getHolder(), this);
        if (!event.isCancelled()) {
            // lets run the triggers first to give the skills a chance to cancel the attack or do what not
            if (getSource() instanceof Hero) {
                AttackTrigger trigger = new AttackTrigger((Hero) getSource(), this, EntityDamageEvent.DamageCause.CUSTOM);
                TriggerManager.callTrigger(trigger);
                if (trigger.isCancelled()) setCancelled(true);
            }
            if (getTarget() instanceof Hero) {
                DamageTrigger trigger = new DamageTrigger(getTarget(), this, EntityDamageEvent.DamageCause.CUSTOM);
                TriggerManager.callTrigger(trigger);
                if (trigger.isCancelled()) setCancelled(true);
            }
            if (isCancelled()) {
                throw new CombatException(CombatException.Type.CANCELLED);
            }
            getTarget().damage(this);
        } else {
            setCancelled(true);
        }
    }
}
