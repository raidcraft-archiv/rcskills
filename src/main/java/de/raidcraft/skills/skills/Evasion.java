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
import de.raidcraft.skills.trigger.DamageTrigger;
import de.raidcraft.skills.util.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author mdoering
 */
@SkillInformation(
        name = "Evasion",
        description = "Chance einem physischen Angriff auszuweichen.",
        types = {EffectType.PROTECTION, EffectType.HELPFUL, EffectType.PHYSICAL}
)
public class Evasion extends AbstractSkill implements Triggered {

    private ConfigurationSection chance;
    private PseudoRandomGenerator randomGenerator;

    public Evasion(Hero hero, SkillProperties data, Profession profession, THeroSkill database) {

        super(hero, data, profession, database);
    }

    @Override
    public void load(ConfigurationSection data) {

        chance = data.getConfigurationSection("chance");
        randomGenerator = new PseudoRandomGenerator(getEvasionChance());
    }

    public double getEvasionChance() {

        return ConfigUtil.getTotalValue(this, chance);
    }

    @TriggerHandler(ignoreCancelled = true, priority = TriggerPriority.HIGHEST)
    public void onDamage(DamageTrigger trigger) throws CombatException {

        if (!trigger.getAttack().isOfAttackType(EffectType.PHYSICAL)) {
            return;
        }
        if (randomGenerator.getIteration() <= 1) {
            // recalculate the chance from our config
            randomGenerator.setChance(getEvasionChance());
        }
        if (randomGenerator.isHit()) {
            throw new CombatException(CombatException.Type.EVADED);
        }
    }
}
