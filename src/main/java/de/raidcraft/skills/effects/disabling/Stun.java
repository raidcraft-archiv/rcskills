package de.raidcraft.skills.effects.disabling;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.DiminishingReturnType;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.PeriodicExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.TriggerPriority;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.AttackTrigger;
import de.raidcraft.skills.trigger.DamageTrigger;
import de.raidcraft.skills.trigger.PlayerCastSkillTrigger;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Stun",
        description = "Stunnt den Gegegner und verhindert alle Aktionen",
        types = {EffectType.DISABLEING, EffectType.HARMFUL},
        diminishingReturn = DiminishingReturnType.CONTROLLED_STUN
)
public class Stun<S> extends PeriodicExpirableEffect<S> implements Triggered {

    private final PotionEffect jumpBlock;
    private final PotionEffect moveBlock;
    private Location location;
    private boolean removeOnDamage = false;
    private boolean cancelAttacks = true;
    private boolean cancelSkills = true;

    public Stun(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        this.interval = 2;
        jumpBlock = new PotionEffect(PotionEffectType.JUMP, (int) getDuration(), 128, false);
        moveBlock = new PotionEffect(PotionEffectType.SLOW, (int) getDuration(), 6, false);
    }

    @Override
    public void load(ConfigurationSection data) {

        removeOnDamage = data.getBoolean("remove-on-damage", false);
        cancelAttacks = data.getBoolean("cancel-attacks", true);
        cancelSkills = data.getBoolean("cancel-skills", true);
    }

    @TriggerHandler(ignoreCancelled = true, priority = TriggerPriority.LOWEST)
    public void onAttack(AttackTrigger trigger) throws CombatException {

        if (cancelAttacks) {
            trigger.getAttack().setCancelled(true);
            throw new CombatException(CombatException.Type.STUNNED);
        }
    }

    @TriggerHandler(ignoreCancelled = true, priority = TriggerPriority.LOWEST)
    public void onSkillUse(PlayerCastSkillTrigger trigger) throws CombatException {

        if (cancelSkills) {
            trigger.setCancelled(true);
            throw new CombatException(CombatException.Type.STUNNED);
        }
    }

    @TriggerHandler(ignoreCancelled = true, priority = TriggerPriority.MONITOR)
    public void onDamage(DamageTrigger trigger) throws CombatException {

        if (removeOnDamage && trigger.getAttack().getDamage() > 0) {
            remove();
        }
    }

    @Override
    protected void tick(CharacterTemplate target) throws CombatException {

        if (location != null) {
            // this is called every tick of the task
            // set the location that was saved when the effect was applied
            target.getEntity().getLocation().setPitch(location.getPitch());
            target.getEntity().getLocation().setYaw(location.getYaw());
        }
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        // lets set the original location of the target
        this.location = target.getEntity().getLocation();
        renew(target);
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

        target.getEntity().addPotionEffect(jumpBlock);
        target.getEntity().addPotionEffect(moveBlock);
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        target.getEntity().removePotionEffect(PotionEffectType.JUMP);
        target.getEntity().removePotionEffect(PotionEffectType.SLOW);
        this.location = null;
    }
}
