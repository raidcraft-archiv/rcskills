package de.raidcraft.skills.api.combat.action;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.trigger.AttackTrigger;
import de.raidcraft.skills.trigger.DamageTrigger;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * @author Silthus
 */
public class EffectDamage extends AbstractAttack<Effect<Skill>, CharacterTemplate> {

    public EffectDamage(Effect<Skill> skillEffect, int damage) {

        super(skillEffect, skillEffect.getTarget(), damage, skillEffect.getTypes());
    }

    @Override
    public void run() throws CombatException {

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                getSource().getSource().getHero().getEntity(),
                getTarget().getEntity(),
                EntityDamageEvent.DamageCause.CUSTOM,
                0);
        if (!event.isCancelled()) {
            // lets run the triggers first to give the skills a chance to cancel the attack or do what not
            if (getSource() instanceof Hero) {
                TriggerManager.callTrigger(new AttackTrigger((Hero) getSource(), this, EntityDamageEvent.DamageCause.CUSTOM));
            }
            if (getTarget() instanceof Hero) {
                TriggerManager.callTrigger(new DamageTrigger(getTarget(), this, EntityDamageEvent.DamageCause.CUSTOM));
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
