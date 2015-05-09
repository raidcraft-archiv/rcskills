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
import de.raidcraft.util.BlockUtil;
import de.raidcraft.util.CustomItemUtil;
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

    public EntityAttack(CharacterTemplate attacker, CharacterTemplate target, Callback<EntityAttack> callback, EffectType... types) {

        this(attacker, target, 0, types);
        this.callback = callback;
    }

    public EntityAttack(CharacterTemplate source, CharacterTemplate target, double damage, EffectType... types) {

        super(source, target, damage, types);
    }

    public EntityAttack(CharacterTemplate attacker, CharacterTemplate target, double damage, Callback<EntityAttack> callback, EffectType... types) {

        this(attacker, target, damage, types);
        this.callback = callback;
    }

    public EntityAttack(EntityDamageByEntityEvent event, double damage) {

        this(RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter((LivingEntity) event.getDamager()),
                RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter((LivingEntity) event.getEntity()),
                damage,
                EffectType.fromEvent(event.getCause()));
        cause = event.getCause();
    }

    public void setLineEffects(List<AmbientEffect> lineEffects) {

        this.lineEffects = lineEffects;
    }

    public EntityDamageEvent.DamageCause getCause() {

        return cause;
    }

    @Override
    public void run() throws CombatException {

        if (getTarget() == null) {
            return;
        }
        EntityDamageByEntityEvent event = CombatManager.fakeDamageEvent(this);
        if (!event.isCancelled() && !getSource().isFriendly(getTarget())) {
            // only add weapon damage if it is a physical attack
            if (!isOfAttackType(EffectType.DEFAULT_ATTACK) && isOfAttackType(EffectType.PHYSICAL)) {
                // if this is a special attack add weapon damage
                setDamage(getDamage() + getSource().getDamage());
                // add the weapon damage
                if (!getWeapons().isEmpty()) {
                    double damage = getDamage();
                    damage += getWeapons().stream()
                            .map(CustomItemUtil::getWeapon)
                            .mapToDouble(customWeapon -> getSource().getWeaponDamage(customWeapon.getEquipmentSlot()))
                            .sum();
                    setDamage(damage);
                }
            }

            if (isCancelled()) {
                throw new CombatException(CombatException.Type.CANCELLED);
            }
            // now actually damage the target
            getTarget().damage(this);
            // if no exceptions was thrown to this point issue the callback
            if (callback != null && callback instanceof EntityAttackCallback) {
                callback.run(this);
            }
            // player some ambient effects
            for (Block block : getSource().getEntity().getLineOfSight(BlockUtil.TRANSPARENT_BLOCKS, 100)) {
                for (AmbientEffect effect : lineEffects) {
                    effect.run(block.getLocation());
                }
            }
            // set the last damage source
            getTarget().getEntity().setLastDamageCause(event);
            getSource().setLastAction(this);
            // play the impact effects
            for (AmbientEffect effect : getImpactEffects()) {
                effect.run(getTarget().getEntity().getLocation());
            }
        } else {
            throw new CombatException("Du kannst dieses Ziel nicht angreifen!");
        }
    }
}
