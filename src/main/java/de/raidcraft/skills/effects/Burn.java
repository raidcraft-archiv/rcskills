package de.raidcraft.skills.effects;

import com.comphenix.protocol.wrappers.EnumWrappers;
import de.raidcraft.api.ambient.ParticleEffect;
import de.raidcraft.skills.api.ability.Ability;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.action.EffectDamage;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.PeriodicExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Burn",
        description = "Verbrennt das Ziel",
        types = {EffectType.HARMFUL, EffectType.DAMAGING, EffectType.DEBUFF},
        elements = {EffectElement.FIRE}
)
public class Burn extends PeriodicExpirableEffect<Ability> {

    public Burn(Ability source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    public void apply(CharacterTemplate target) {

        Location location = target.getEntity().getLocation();
        location.getWorld().playSound(location, Sound.BLOCK_FIRE_AMBIENT, 10F, 1F);
        renew(target);
    }

    @Override
    protected void renew(CharacterTemplate target) {

        ParticleEffect.sendToLocation(Particle.FLAME, target.getEntity().getLocation(), 0.25F, 0.25F, 0.25F, 3);
    }

    @Override
    protected void remove(CharacterTemplate target) {


    }

    @Override
    protected void tick(CharacterTemplate target) {

        try {
            renew(target);
            new EffectDamage(this, getDamage()).run();
        } catch (CombatException e) {
            warn(e.getMessage());
        }
    }
}
