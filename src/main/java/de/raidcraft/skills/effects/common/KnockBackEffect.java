package de.raidcraft.skills.effects.common;

import de.raidcraft.api.config.DataMap;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.effect.AbstractEffect;
import de.raidcraft.skills.api.combat.effect.EffectInformation;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "KnockBack",
        description = "Knocks back the target",
        types = {}
)
public class KnockBackEffect extends AbstractEffect<CharacterTemplate, CharacterTemplate> {

    private double power;

    public KnockBackEffect(CharacterTemplate source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    public void load(DataMap data) {

        this.power = data.getDouble("power", 0.4);
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        CharacterTemplate attacker = getSource();
        // knocks back the target based on the attackers center position
        Location knockBackCenter = attacker.getEntity().getLocation();
        double xOff = target.getEntity().getLocation().getX() - knockBackCenter.getX();
        double yOff = target.getEntity().getLocation().getY() - knockBackCenter.getY();
        double zOff = target.getEntity().getLocation().getZ() - knockBackCenter.getZ();
        // power is the velocity applied to the target
        // a power of 0.4 is a player jumping
        target.getEntity().setVelocity(new Vector(xOff, yOff, zOff).normalize().multiply(power));
    }
}
