package de.raidcraft.skills.api.effect.common;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.callback.Callback;
import de.raidcraft.skills.api.effect.AbstractEffect;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.TriggerPriority;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.PotionSplashTrigger;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Queued-Splash-Potion",
        description = "Calls the callback when a potion splashs.",
        types = {EffectType.SYSTEM},
        global = true
)
public class QueuedSplashPotion extends AbstractEffect<Skill> implements Triggered {

    private Callback<PotionSplashTrigger> callback;

    public QueuedSplashPotion(Skill source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    public void addCallback(Callback<PotionSplashTrigger> callback) {

        this.callback = callback;
    }

    @TriggerHandler(ignoreCancelled = true, priority = TriggerPriority.LOWEST)
    public void onPotionSplash(PotionSplashTrigger trigger) throws CombatException {

        if (callback != null) {
            callback.run(trigger);
        }
        remove();
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {


    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {


    }
}
