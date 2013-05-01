package de.raidcraft.skills.api.effect.common;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.ProjectileType;
import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.combat.callback.LocationCallback;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.ExpirableEffect;
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
    private ProjectileType type;
    private boolean attacked = false;

    public QueuedProjectile(Skill source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        if (duration == 0) duration = 20 * 10;
    }

    public void addCallback(LocationCallback callback) {

        this.callback = callback;
    }

    public void addCallback(LocationCallback callback, ProjectileType type) {

        addCallback(callback);
        setType(type);
    }

    public void setType(ProjectileType type) {

        this.type = type;
    }

    @TriggerHandler
    public void onProjectileHit(ProjectileHitTrigger trigger) throws CombatException {

        if (type != null && type != ProjectileType.valueOf(trigger.getEvent().getEntity())) {
            return;
        }
        // lets substract the usage cost if the skill is marked as a queued attack
        if (getSource().getSkillProperties().getInformation().queuedAttack()) {
            getSource().substractUsageCost(new SkillAction(getSource()));
        }
        if (callback != null) {
            callback.run(trigger.getEvent().getEntity().getLocation());
            info("Fernkampf Angriff \"" + getSource() + "\" wurde ausgeführt.");
        }
        trigger.getEvent().getEntity().remove();
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
