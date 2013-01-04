package de.raidcraft.skills.effects.disabling;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.AbstractEffect;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "KnockBack",
        description = "Knocks back the target",
        types = {}
)
public class KnockBack extends AbstractEffect<Location> {

    private double power;

    public KnockBack(Location source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    public void load(ConfigurationSection data) {

        this.power = data.getDouble("power", 0.4);
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        // knocks back the target based on the attackers center position
        Location knockBackCenter = getSource();
        double xOff = target.getEntity().getLocation().getX() - knockBackCenter.getX();
        double yOff = target.getEntity().getLocation().getY() - knockBackCenter.getY();
        double zOff = target.getEntity().getLocation().getZ() - knockBackCenter.getZ();
        // power is the velocity applied to the target
        // a power of 0.4 is a player jumping
        target.getEntity().setVelocity(new Vector(xOff, yOff, zOff).normalize().multiply(power));
        // also interrupt the target
        target.addEffect(this, Interrupt.class);
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {
        // not much to do here
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {
        // not much to do here
    }
}
