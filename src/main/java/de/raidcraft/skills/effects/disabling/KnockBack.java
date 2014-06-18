package de.raidcraft.skills.effects.disabling;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
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
        types = {EffectType.PHYSICAL, EffectType.MOVEMENT, EffectType.HARMFUL}
)
public class KnockBack extends AbstractEffect<Location> {

    private boolean interrupt = false;
    private double power;

    public KnockBack(Location source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        Vector directionVector = getSource().getDirection().normalize();
        // power is the velocity applied to the target
        // a power of 0.4 is a player jumping
        target.getEntity().setVelocity(target.getEntity().getVelocity().add(directionVector.multiply(power)));
        // also interrupt the target
        if (interrupt) target.addEffect(this, Interrupt.class);
        remove();
    }

    @Override
    public void load(ConfigurationSection data) {

        this.interrupt = data.getBoolean("interrupt", false);
        this.power = data.getDouble("power", 0.4);
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {
        // not much to do here
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {
        // not much to do here
    }
}
