package de.raidcraft.skills.api.combat.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.trigger.DamageTrigger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * @author Silthus
 */
public class EnvironmentAttack extends AbstractAttack<EntityDamageByEntityEvent.DamageCause, CharacterTemplate> {

    private final EntityDamageEvent event;

    public EnvironmentAttack(EntityDamageEvent event, int damage) {

        super(event.getCause(),
                RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter((LivingEntity) event.getEntity()),
                damage,
                EffectType.fromEvent(event.getCause()));
        this.event = event;
    }

    @Override
    public void run() throws CombatException {

        // respect the no damage ticks
        if (getTarget().getEntity().getNoDamageTicks() > 0) {
            return;
        }


        // lets run the triggers first to give the skills a chance to cancel the attack or do what not
        if (getTarget() instanceof Hero) {
            DamageTrigger trigger = new DamageTrigger(getTarget(), this, event.getCause());
            TriggerManager.callTrigger(trigger);
            if (trigger.isCancelled()) setCancelled(true);
        }
        if (isCancelled()) {
            throw new CombatException(CombatException.Type.CANCELLED);
        }
        getTarget().damage(this);
        // set the last damage source
        getTarget().getEntity().setLastDamageCause(event);
        // set the no damage ticks
        getTarget().getEntity().setNoDamageTicks(getTarget().getEntity().getMaximumNoDamageTicks());
    }
}
