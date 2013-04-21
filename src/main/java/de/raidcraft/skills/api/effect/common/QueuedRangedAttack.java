package de.raidcraft.skills.api.effect.common;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.ProjectileType;
import de.raidcraft.skills.api.combat.action.RangedAttack;
import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.combat.callback.ProjectileCallback;
import de.raidcraft.skills.api.combat.callback.SourcedRangeCallback;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.TriggerPriority;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.BowFireTrigger;
import org.bukkit.entity.Projectile;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Queued-Range-Attack",
        description = "Calls back a range attack when projectile hits."
)
public class QueuedRangedAttack<T extends ProjectileCallback> extends ExpirableEffect<Skill> implements Triggered {

    private T callback;
    private boolean fired = false;

    public QueuedRangedAttack(Skill source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        if (duration == 0) duration = 20 * 5;
    }

    public void addCallback(T callback) {

        this.callback = callback;
    }

    @TriggerHandler(ignoreCancelled = true, priority = TriggerPriority.LOWEST)
    public void onBowFire(BowFireTrigger trigger) throws CombatException {

        // lets substract the usage cost if the skill is marked as a queued attack
        if (getSource().getProperties().getInformation().queuedAttack()) {
            getSource().substractUsageCost(new SkillAction(getSource()));
        }
        // lets replace the fired projectile with ours so we can track the impact and callback
        RangedAttack<T> attack =
                new RangedAttack<>(getSource().getHero(), ProjectileType.ARROW, getSource().getTotalDamage(), callback);
        attack.setProjectile((Projectile) trigger.getEvent().getProjectile());
        // since we dont "run" the attack we need to queue for callbacks
        new SourcedRangeCallback<>(attack).queueCallback();
        fired = true;
        remove();
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        info("Du belegst deinen Bogen mit einem Zauber.");
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        if (!fired) {
            info("Der Zauber auf deinem Bogen ist verblasst.");
        }
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

    }
}
