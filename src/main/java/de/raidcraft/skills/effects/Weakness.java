package de.raidcraft.skills.effects;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.AttackTrigger;
import de.raidcraft.skills.util.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Weakness",
        description = "Schwächt das Ziel und verringert dessen Schaden.",
        types = {EffectType.HARMFUL, EffectType.DEBUFF}
)
public class Weakness<S extends Skill> extends ExpirableEffect<S> implements Triggered {

    private PotionEffect weakness;
    private ConfigurationSection config;

    public Weakness(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        weakness = new PotionEffect(PotionEffectType.WEAKNESS, (int) getDuration(), 0, false);
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        target.getEntity().addPotionEffect(weakness);
    }

    @Override
    public void load(ConfigurationSection data) {

        config = data.getConfigurationSection("reduction");
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

        target.getEntity().addPotionEffect(weakness);
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        target.getEntity().removePotionEffect(PotionEffectType.WEAKNESS);
    }

    @TriggerHandler
    public void onAttack(AttackTrigger trigger) {

        double damage = trigger.getAttack().getDamage();
        int newDamage = (int) (damage - damage * getReduction());
        trigger.getAttack().setDamage(newDamage);
    }

    private double getReduction() {

        return ConfigUtil.getTotalValue(getSource(), config);
    }
}
