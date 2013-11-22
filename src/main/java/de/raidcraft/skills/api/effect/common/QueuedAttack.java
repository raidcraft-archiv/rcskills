package de.raidcraft.skills.api.effect.common;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.combat.callback.Callback;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.AbilityEffectStage;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.TriggerPriority;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.AttackTrigger;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "QueuedAttack",
        description = "Löst den gegebenen Skill bei einem Angriff aus.",
        types = {EffectType.SYSTEM}
)
public class QueuedAttack extends ExpirableEffect<Skill> implements Triggered {

    private Callback<AttackTrigger> callback;
    private boolean attacked = false;

    public QueuedAttack(Skill source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        if (duration == 0) duration = 20 * 5;
    }

    public void addCallback(Callback<AttackTrigger> callback) {

        this.callback = callback;
    }

    @TriggerHandler(ignoreCancelled = true, priority = TriggerPriority.LOWEST)
    public void onAttack(AttackTrigger trigger) throws CombatException {

        if (attacked || !getSource().canUseAbility()) {
            remove();
            return;
        }
        // lets substract the usage cost if the skill is marked as a queued attack
        if (getSource().getSkillProperties().getInformation().queuedAttack()) {
            getSource().substractUsageCost(new SkillAction(getSource()));
        }
        // and now do some attack magic :)
        attacked = true;
        trigger.getAttack().setDamage(getSource().getTotalDamage());
        trigger.getAttack().addAttackTypes(getSource().getTypes().toArray(new EffectType[getSource().getTypes().size()]));
        trigger.getAttack().addAttackTypes(getTypes());
        if (callback != null) {
            callback.run(trigger);
            getSource().executeAmbientEffects(AbilityEffectStage.HIT, trigger.getAttack().getTarget().getEntity().getLocation());
        }
        info(getSource().getFriendlyName() + " ausgeführt!");
        remove();
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {


    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        if (!attacked) {
            info("Du senkst deine Waffe.");
        }
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

    }
}
