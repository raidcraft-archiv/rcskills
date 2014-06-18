package de.raidcraft.skills.api.effect.common;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.action.RangedAttack;
import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.combat.callback.BowFireCallback;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.AbilityEffectStage;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.TriggerPriority;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.BowFireTrigger;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Queued-Range-Attack",
        description = "Calls back a range attack when projectile hits.",
        types = {EffectType.SYSTEM},
        global = true
)
public class QueuedBowFire extends ExpirableEffect<Skill> implements Triggered {

    private BowFireCallback callback;
    private boolean fired = false;

    public QueuedBowFire(Skill source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        if (duration == 0) duration = 20 * 5;
    }

    public void addCallback(BowFireCallback callback) {

        this.callback = callback;
    }

    @TriggerHandler(ignoreCancelled = true, priority = TriggerPriority.LOWEST)
    public void onBowFire(BowFireTrigger trigger) throws CombatException {

        // lets call the bow fire callback
        if (callback != null) {
            callback.run(trigger);
            getSource().executeAmbientEffects(AbilityEffectStage.BOW_FIRE, trigger.getSource().getEntity().getLocation());
        }
        // lets substract the usage cost if the skill is marked as a queued attack
        if (getSource().getSkillProperties().getInformation().queuedAttack()) {
            getSource().substractUsageCost(new SkillAction(getSource()));
        }
        // lets replace the fired projectile with ours so we can track the impact and callback
        new RangedAttack(getSource().getHolder(), trigger.getEvent()).run();
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
