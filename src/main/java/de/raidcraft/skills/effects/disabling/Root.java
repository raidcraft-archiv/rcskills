package de.raidcraft.skills.effects.disabling;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.PeriodicExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.Location;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Root",
        description = "Hält das Ziel am unbeweglich am Boden fest.",
        types = {EffectType.HARMFUL, EffectType.MOVEMENT}
)
public class Root<S> extends PeriodicExpirableEffect<S> {

    private Location location;

    public Root(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        this.interval = 2;
    }

    @Override
    protected void tick(CharacterTemplate target) throws CombatException {

        if (location != null) {
            // this is called every tick of the task
            // set the location that was saved when the effect was applied
            target.getEntity().teleport(location);
        }
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        renew(target);
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

        // lets set the original location of the target
        Location loc = target.getEntity().getLocation();
        this.location = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        this.location = null;
    }
}
