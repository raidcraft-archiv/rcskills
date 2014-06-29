package de.raidcraft.skills.effects;

import de.raidcraft.skills.api.ability.Ability;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.action.EffectDamage;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.PeriodicExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.util.EffectUtil;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "bleed",
        description = "Lässt das Ziel bluten.",
        types = {EffectType.PHYSICAL, EffectType.HARMFUL, EffectType.DAMAGING, EffectType.DEBUFF}
)
public class Bleed extends PeriodicExpirableEffect<Ability> {

    private static final FireworkEffect BLEED_EFFECT = FireworkEffect.builder()
            .with(FireworkEffect.Type.BURST)
            .withColor(Color.RED)
            .withFade(Color.BLACK)
            .build();

    public Bleed(Ability source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    protected void tick(CharacterTemplate target) throws CombatException {

        new EffectDamage(this, getDamage()).run();
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        World world = target.getEntity().getWorld();
        Location location = target.getEntity().getLocation();
        EffectUtil.playFirework(world, location, BLEED_EFFECT);
        world.playSound(location, Sound.SHEEP_SHEAR, 10F, 1F);
        world.playSound(location, Sound.SLIME_ATTACK, 10F, 0.0001F);
        warn("Blutungseffekt erhalten!");
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

        warn("Blutungseffekt wurde erneuert!");
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        warn("Blutungseffekt entfernt!");
    }
}
