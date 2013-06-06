package de.raidcraft.skills.effects;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.TriggerPriority;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.AttackTrigger;
import de.raidcraft.skills.trigger.EntityTargetTrigger;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Summoned",
        description = "Markiert beschwörte Kreaturen.",
        types = {EffectType.SUMMON, EffectType.MAGICAL}
)
public class Summoned extends ExpirableEffect<Skill> implements Triggered {

    public Summoned(Skill source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @TriggerHandler(ignoreCancelled = true, priority = TriggerPriority.LOW)
    public void onTargetTrigger(EntityTargetTrigger trigger) {

        LivingEntity target = trigger.getEvent().getTarget();
        if (target.equals(getSource().getHolder().getEntity())) {
            trigger.getEvent().setCancelled(true);
            return;
        }
        for (CharacterTemplate member : trigger.getSource().getParty().getMembers()) {
            if (target.equals(member.getEntity())) {
                trigger.getEvent().setCancelled(true);
                return;
            }
        }
    }

    @TriggerHandler(ignoreCancelled = true, priority = TriggerPriority.LOW)
    public void onAttack(AttackTrigger trigger) {

        CharacterTemplate target = trigger.getAttack().getTarget();
        if (target.equals(getSource().getHolder())) {
            trigger.getAttack().setCancelled(true);
            return;
        }
        for (CharacterTemplate member : trigger.getSource().getParty().getMembers()) {
            if (target.equals(member)) {
                trigger.getAttack().setCancelled(true);
                return;
            }
        }
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        // this will make the creature friendly to the party of the summoner
        getSource().getHolder().getParty().addMember(target);
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        target.getEntity().setCustomNameVisible(false);
        target.kill();
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

    }
}
