package de.raidcraft.skills.api.effect.common;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.action.RangedAttack;
import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.combat.callback.RangedCallback;
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
        description = "Calls back a range attack when projectile hits.",
        types = {EffectType.SYSTEM}
)
public class QueuedRangedAttack extends ExpirableEffect<Skill> implements Triggered {

    private RangedCallback callback;
    private boolean fired = false;

    public QueuedRangedAttack(Skill source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        if (duration == 0) duration = 20 * 5;
    }

    public void addCallback(RangedCallback callback) {

        this.callback = callback;
    }

    @TriggerHandler(ignoreCancelled = true, priority = TriggerPriority.LOWEST)
    public void onBowFire(BowFireTrigger trigger) throws CombatException {

        // lets substract the usage cost if the skill is marked as a queued attack
        if (getSource().getSkillProperties().getInformation().queuedAttack()) {
            getSource().substractUsageCost(new SkillAction(getSource()));
        }
        // lets replace the fired projectile with ours so we can track the impact and callback
        RangedAttack<RangedCallback> attack = new RangedAttack<>(getSource().getHolder(), trigger.getEvent(), callback);
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
