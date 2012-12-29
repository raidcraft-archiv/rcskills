package de.raidcraft.skills.api.combat.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.AttackType;
import de.raidcraft.skills.api.combat.callback.Callback;
import de.raidcraft.skills.api.combat.callback.RangedCallback;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.trigger.AttackTrigger;
import de.raidcraft.skills.trigger.DamageTrigger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * This attack is issued by a skill or the damage event itself.
 *
 * @author Silthus
 */
public class EntityAttack extends AbstractAttack<CharacterTemplate, CharacterTemplate> {

    private Callback callback;

    public EntityAttack(CharacterTemplate source, CharacterTemplate target, int damage, AttackType... types) {

        super(source, target, damage, types);
    }

    public EntityAttack(CharacterTemplate attacker, CharacterTemplate target, Callback callback, AttackType... types) {

        this(attacker, target, 0, types);
        this.callback = callback;
    }

    public EntityAttack(CharacterTemplate attacker, CharacterTemplate target, int damage, Callback callback, AttackType... types) {

        this(attacker, target, damage, types);
        this.callback = callback;
    }

    public EntityAttack(EntityDamageByEntityEvent event, int damage) {

        this(RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter((LivingEntity) event.getDamager()),
                RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter((LivingEntity) event.getEntity()),
                damage,
                AttackType.fromEvent(event.getCause()));
    }

    @Override
    public void run() throws CombatException {

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                getSource().getEntity(),
                getTarget().getEntity(),
                EntityDamageEvent.DamageCause.CUSTOM,
                0);
        if (!event.isCancelled()) {
            // lets run the triggers first to give the skills a chance to cancel the attack or do what not
            if (getSource() instanceof Hero) {
                TriggerManager.callTrigger(new AttackTrigger((Hero) getSource(), this));
            }
            if (getTarget() instanceof Hero) {
                TriggerManager.callTrigger(new DamageTrigger((Hero) getTarget(), this));
            }
            if (isCancelled()) {
                throw new CombatException(CombatException.Type.CANCELLED);
            }
            // TODO: add fancy resitence checks and so on
            getTarget().damage(this);
            // if no exceptions was thrown to this point issue the callback
            if (callback != null && !(callback instanceof RangedCallback)) {
                try {
                    callback.run(getTarget());
                } catch (InvalidTargetException e) {
                    throw new CombatException(e.getMessage());
                }
            }
        } else {
            setCancelled(true);
        }
    }
}
