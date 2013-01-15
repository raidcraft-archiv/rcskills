package de.raidcraft.skills.api.effect.common;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.callback.LocationCallback;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.ProjectileHitTrigger;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "QueuedProjectile",
        description = "Löst den Effekt aus wenn das Projektil den Boden trifft."
)
public class QueuedProjectile extends ExpirableEffect<Skill> implements Triggered {

    private LocationCallback callback;
    private boolean attacked = false;

    public QueuedProjectile(Skill source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    public void addCallback(LocationCallback callback) {

        this.callback = callback;
    }

    @TriggerHandler
    public void onProjectileHit(ProjectileHitTrigger trigger) throws CombatException {

        if (callback != null) {
            callback.run(trigger.getEvent().getEntity().getLocation());
        }
        attacked = true;
        remove();
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        info("Du belegst deine Fernkampf Waffe mit einem Zauber.");
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        if (!attacked) {
            info("Du senkst deine Fernkampf Waffe.");
        }
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

    }
}
