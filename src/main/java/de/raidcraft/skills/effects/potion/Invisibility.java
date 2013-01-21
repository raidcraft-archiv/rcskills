package de.raidcraft.skills.effects.potion;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.TriggerPriority;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.AttackTrigger;
import de.raidcraft.skills.trigger.DamageTrigger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Author: Philip
 * Date: 27.12.12 - 21:45
 * Description:
 */
@EffectInformation(
        name = "Invisibility",
        description = "Lässt das Ziel unsichtbar werden",
        types = {EffectType.BUFF}
)
public class Invisibility<S> extends ExpirableEffect<S> implements Triggered {

    private boolean slow = false;
    private boolean removeOnDamage = true;
    private boolean removeOnAttack = true;
    private final PotionEffect invisibility;
    private final PotionEffect slowEffect;

    public Invisibility(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        invisibility = new PotionEffect(PotionEffectType.INVISIBILITY, (int) getDuration(), 1);
        // slows by 15% for each strength point
        slowEffect = new PotionEffect(PotionEffectType.SLOW, (int) getDuration(), 3);
    }

    @Override
    public void load(ConfigurationSection data) {

        slow = data.getBoolean("slow", false);
        removeOnDamage = data.getBoolean("remove-on-damage", true);
        removeOnAttack = data.getBoolean("remove-on-attack", true);
    }

    @TriggerHandler(ignoreCancelled = true, priority = TriggerPriority.MONITOR)
    public void onDamage(DamageTrigger trigger) throws CombatException {

        if (removeOnDamage) {
            remove();
        }
    }

    @TriggerHandler(ignoreCancelled = true, priority = TriggerPriority.MONITOR)
    public void onAttack(AttackTrigger trigger) throws CombatException {

        if (removeOnAttack) {
            remove();
        }
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        info("Du bist nun unsichtbar.");
        target.getEntity().addPotionEffect(invisibility);
        if (slow) target.getEntity().addPotionEffect(slowEffect);
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        info("Unsichtbarkeit wurde aufgehoben.");
        target.getEntity().removePotionEffect(PotionEffectType.INVISIBILITY);
        target.getEntity().removePotionEffect(PotionEffectType.SLOW);
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

        target.getEntity().addPotionEffect(invisibility);
        if (slow) target.getEntity().addPotionEffect(slowEffect);
    }
}
