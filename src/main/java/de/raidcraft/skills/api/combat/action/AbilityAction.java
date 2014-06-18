package de.raidcraft.skills.api.combat.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.ambient.AmbientEffect;
import de.raidcraft.skills.api.ability.Ability;
import de.raidcraft.skills.api.ability.Useable;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.common.CastTime;
import de.raidcraft.skills.api.effect.common.Combat;
import de.raidcraft.skills.api.effect.common.GlobalCooldown;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.skill.AbilityEffectStage;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.trigger.EntityCastAbilityTrigger;

/**
 * @author Silthus
 */
public class AbilityAction<T extends CharacterTemplate> extends AbstractAction<T> {

    private final Ability<T> ability;
    private final EntityCastAbilityTrigger trigger;
    private double castTime;
    private double cooldown;

    public AbilityAction(Ability<T> ability) {

        super(ability.getHolder());
        this.ability = ability;
        this.castTime = ability.getTotalCastTime();
        this.cooldown = ability.getTotalCooldown();

        // lets issue a trigger that can be modified by other skills
        this.trigger = TriggerManager.callSafeTrigger(new EntityCastAbilityTrigger(this));
    }

    public double getCastTime() {

        return castTime;
    }

    public void setCastTime(double castTime) {

        if (castTime < 0.0) {
            castTime = 0.0;
        }
        this.castTime = castTime;
    }

    public double getCooldown() {

        return cooldown;
    }

    public void setCooldown(double cooldown) {

        if (cooldown < 0.0) {
            cooldown = 0.0;
        }
        this.cooldown = cooldown;
    }

    @Override
    public void run() throws CombatException {

        if (!(ability instanceof Useable)) {
            throw new CombatException("Die Fähigkeit ist passiv und kann nicht aktiv genutzt werden.");
        }

        if (getSource().hasEffect(GlobalCooldown.class)) {
            throw new CombatException(CombatException.Type.ON_GLOBAL_COOLDOWN);
        }

        // lets cancel other casts first
        getSource().removeEffect(CastTime.class);

        if (trigger.isCancelled()) {
            throw new CombatException(CombatException.Type.CANCELLED);
        }

        // check if we meet all requirements to use the skill
        getAbility().checkUsage(this);

        // and call the trigger
        ((Useable) ability).use();
        // substract the usage costs
        ability.substractUsageCost(this);
        // run ambient stuff
        for (AmbientEffect ambientEffect : getAbility().getAmbientEffects(AbilityEffectStage.CAST)) {
            ambientEffect.run(getSource().getEntity().getLocation());
        }

        // also trigger combat effect if not supressed
        try {
            getAbility().getHolder().addEffect(getAbility(), Combat.class);
        } catch (CombatException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
    }

    public Ability<T> getAbility() {

        return ability;
    }
}
