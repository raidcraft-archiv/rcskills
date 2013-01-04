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

    public QueuedAttack(Skill source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        if (duration == 0) duration = 20 * 10;
    }

    public void addCallback(Callback<AttackTrigger> callback) {

        this.callback = callback;
    }

    @TriggerHandler
    public void onAttack(AttackTrigger trigger) throws CombatException {

        trigger.getAttack().setDamage(getSource().getTotalDamage());
        if (callback != null) {
            callback.run(trigger);
        }
        remove();
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

    }
}
