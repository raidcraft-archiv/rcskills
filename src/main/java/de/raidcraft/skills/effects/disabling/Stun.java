package de.raidcraft.skills.effects.disabling;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.PeriodicExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Stun",
        description = "Stunnt den Gegegner und verhindert alle Aktionen"
)
public class Stun<S> extends PeriodicExpirableEffect<S> {

    private static final int AMPLIFIER = 127;
    private final PotionEffect confusionEffect;
    private final PotionEffect slowEffect;
    private Location location;

    public Stun(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        delay = 0;
        interval = 10;
        duration = 60;
        this.confusionEffect = new PotionEffect(PotionEffectType.CONFUSION, (int) getDuration(), AMPLIFIER, false);
        this.slowEffect = new PotionEffect(PotionEffectType.SLOW, (int) getDuration(), AMPLIFIER, false);
    }

    @Override
    protected void tick(CharacterTemplate target) throws CombatException {

        if (location != null) {
            // this is called every tick of the task
            // set the location that was saved when the effect was applied
            target.getEntity().teleport(location);
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

        target.getEntity().addPotionEffect(confusionEffect, true);
        target.getEntity().addPotionEffect(slowEffect, true);
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        target.getEntity().removePotionEffect(PotionEffectType.SLOW);
        target.getEntity().removePotionEffect(PotionEffectType.CONFUSION);
        this.location = null;
    }
}
