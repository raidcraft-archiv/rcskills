package de.raidcraft.skills.skills;

import de.raidcraft.api.random.PseudoRandomGenerator;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.AbstractSkill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.TriggerPriority;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.skills.trigger.AttackTrigger;
import de.raidcraft.skills.util.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author mdoering
 */
@SkillInformation(
        name = "Critical Strike",
        description = "Chance bei einem Angriff einen kritischen Treffer zu erzielen.",
        types = {EffectType.DAMAGING, EffectType.HELPFUL}
)
public class CriticalStrike extends AbstractSkill implements Triggered {

    private ConfigurationSection chance;
    private double damageModifier;
    private PseudoRandomGenerator randomGenerator;

    public CriticalStrike(Hero hero, SkillProperties data, Profession profession, THeroSkill database) {

        super(hero, data, profession, database);
    }

    @Override
    public void load(ConfigurationSection data) {

        chance = data.getConfigurationSection("chance");
        damageModifier = ConfigUtil.getTotalValue(this, data.getConfigurationSection("damage-modifier"));
        randomGenerator = new PseudoRandomGenerator(getCriticalStrikeChance());
    }

    public double getCriticalStrikeChance() {

        return ConfigUtil.getTotalValue(this, chance);
    }

    @TriggerHandler(ignoreCancelled = true, priority = TriggerPriority.HIGHEST)
    public void onDamage(AttackTrigger trigger) throws CombatException {

        if (randomGenerator.getIteration() <= 1) {
            // recalculate the chance from our config
            randomGenerator.setChance(getCriticalStrikeChance());
        }
        if (randomGenerator.isHit()) {
            trigger.getAttack().addAttackTypes(EffectType.CRITICAL);
            trigger.getAttack().setDamage(trigger.getAttack().getDamage() * damageModifier);
        }
    }
}
