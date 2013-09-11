package de.raidcraft.skills.api.combat.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.ambient.AmbientEffect;
import de.raidcraft.skills.CombatManager;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.callback.Callback;
import de.raidcraft.skills.api.combat.callback.EntityAttackCallback;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.trigger.AttackTrigger;
import de.raidcraft.skills.trigger.DamageTrigger;
import de.raidcraft.util.BlockUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * This attack is issued by a skill or the damage event itself.
 *
 * @author Silthus
 */
public class EntityAttack extends AbstractAttack<CharacterTemplate, CharacterTemplate> {

    private Callback<EntityAttack> callback;
    private EntityDamageEvent.DamageCause cause = null;
    private List<AmbientEffect> lineEffects = new ArrayList<>();

    public EntityAttack(CharacterTemplate source, CharacterTemplate target, int damage, EffectType... types) {

        super(source, target, damage, types);
    }

    public EntityAttack(CharacterTemplate attacker, CharacterTemplate target, Callback<EntityAttack> callback, EffectType... types) {

        this(attacker, target, 0, types);
        this.callback = callback;
    }

    public EntityAttack(CharacterTemplate attacker, CharacterTemplate target, int damage, Callback<EntityAttack> callback, EffectType... types) {

        this(attacker, target, damage, types);
        this.callback = callback;
    }

    public EntityAttack(EntityDamageByEntityEvent event, int damage) {

        this(RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter((LivingEntity) event.getDamager()),
                RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter((LivingEntity) event.getEntity()),
                damage,
                EffectType.fromEvent(event.getCause()));
        cause = event.getCause();
    }

    public void setLineEffects(List<AmbientEffect> lineEffects) {

        this.lineEffects = lineEffects;
    }

    @Override
    public void run() throws CombatException {

        EntityDamageByEntityEvent event = CombatManager.fakeDamageEvent(this);
        if (!event.isCancelled() && !getSource().isFriendly(getTarget())) {
            // lets run the triggers first to give the skills a chance to cancel the attack or do what not
            if (getSource() instanceof Hero) {
                AttackTrigger trigger = new AttackTrigger(getSource(), this, cause);
                TriggerManager.callTrigger(trigger);
                if (trigger.isCancelled()) setCancelled(true);
            }
            if (getTarget() instanceof Hero) {
                DamageTrigger trigger = new DamageTrigger(getTarget(), this, cause);
                TriggerManager.callTrigger(trigger);
                if (trigger.isCancelled()) setCancelled(true);
            }
            if (isCancelled()) {
                throw new CombatException(CombatException.Type.CANCELLED);
            }
            // if no exceptions was thrown to this point issue the callback
            if (callback != null && callback instanceof EntityAttackCallback) {
                callback.run(this);
            }
            // only add weapon damage if it is a physical attack
            if (!isOfAttackType(EffectType.DEFAULT_ATTACK) && isOfAttackType(EffectType.PHYSICAL)) {
                // if this is a special attack add weapon damage
                setDamage(getDamage() + getSource().getDamage());
            }
            // player some ambient effects
            for (Block block : getSource().getEntity().getLineOfSight(BlockUtil.TRANSPARENT_BLOCKS, 100)) {
                for (AmbientEffect effect : lineEffects) {
                    effect.run(block.getLocation());
                }
            }
            // now actually damage the target
            getTarget().damage(this);
            // set the last damage source
            getTarget().getEntity().setLastDamageCause(event);
            // play the impact effects
            for (AmbientEffect effect : getImpactEffects()) {
                effect.run(getTarget().getEntity().getLocation());
            }
        } else {
            throw new CombatException("Du kannst dieses Ziel nicht angreifen!");
        }
    }
}
