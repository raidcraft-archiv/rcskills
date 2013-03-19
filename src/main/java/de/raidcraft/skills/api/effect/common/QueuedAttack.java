package de.raidcraft.skills.api.effect.common;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.callback.Callback;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.items.WeaponType;
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

    public QueuedAttack(Skill source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        if (duration == 0) duration = 20 * 5;
        // lets see if the attack is restricted to a weapon type
        weapon = WeaponType.fromString(source.getProperties().getData().getString("weapon"));
    }

    public void addCallback(Callback<AttackTrigger> callback) {

        this.callback = callback;
    }

    @TriggerHandler
    public void onAttack(AttackTrigger trigger) throws CombatException {

        if (attacked) {
            return;
        }
        attacked = true;
        if (weapon != null && weapon.isOfType(getSource().getHero().getItemTypeInHand())) {
            trigger.setCancelled(true);
            trigger.getAttack().setCancelled(true);
            throw new CombatException(CombatException.Type.INVALID_WEAPON);
        }
        trigger.getAttack().setDamage(getSource().getTotalDamage());
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
