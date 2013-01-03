package de.raidcraft.skills.effects.damaging;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.AttackType;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.action.EffectDamage;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.PeriodicExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "bleed",
        description = "Lässt das Ziel bluten.",
        types = {EffectType.PHYSICAL, EffectType.HARMFUL, EffectType.DAMAGING, EffectType.DEBUFF}
)
public class Bleed extends PeriodicExpirableEffect<Skill> {

    private int damagePerTick = 0;

    public Bleed(Skill source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    public void load(ConfigurationSection data) {

        damagePerTick = data.getInt("damage", 0);
    }

    @Override
    protected void tick(CharacterTemplate target) throws CombatException {

        new EffectDamage(this, damagePerTick, AttackType.PHYSICAL).run();
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        warn("Blutungseffekt erhalten!");
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        warn("Blutungseffekt entfernt!");
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

        warn("Blutungseffekt wurde erneuert!");
    }
}
