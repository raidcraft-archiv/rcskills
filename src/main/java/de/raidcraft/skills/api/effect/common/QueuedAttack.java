package de.raidcraft.skills.api.effect.common;

import de.raidcraft.api.items.WeaponType;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.combat.callback.Callback;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.AttackTrigger;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "QueuedAttack",
        description = "Löst den gegebenen Skill bei einem Angriff aus.",
        types = {EffectType.PHYSICAL, EffectType.HARMFUL}
)
public class QueuedAttack extends ExpirableEffect<Skill> implements Triggered {

    private Callback<AttackTrigger> callback;
    private boolean attacked = false;
    private WeaponType weapon;
    int damage = 0;

    public QueuedAttack(Skill source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        if (duration == 0) duration = 20 * 5;
        // lets see if the attack is restricted to a weapon type
        weapon = WeaponType.fromString(source.getSkillProperties().getData().getString("weapon"));
        // we need to get the damage now because of the variable resource damage stuff
        damage = getSource().getTotalDamage();
    }

    public void addCallback(Callback<AttackTrigger> callback) {

        this.callback = callback;
    }

    @TriggerHandler
    public void onAttack(AttackTrigger trigger) throws CombatException {

        if (attacked) {
            return;
        }
        // lets substract the usage cost if the skill is marked as a queued attack
        if (getSource().getSkillProperties().getInformation().queuedAttack()) {
            getSource().substractUsageCost(new SkillAction(getSource()));
        }
        // and now do some attack magic :)
        attacked = true;
        trigger.getAttack().setDamage(damage);
        trigger.getAttack().addAttackTypes(getSource().getTypes().toArray(new EffectType[getSource().getTypes().size()]));
        trigger.getAttack().addAttackTypes(getTypes());
        if (callback != null) {
            callback.run(trigger);
        }
        info(getSource().getFriendlyName() + " ausgeführt!");
        remove();
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        info("Du hebst deine Waffe zum Angriff: " + getSource().getFriendlyName());
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
