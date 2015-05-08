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
        name = "Miss",
        description = "Chance bei einen Angriff zu verfehlen.",
        types = {EffectType.HARMFUL, EffectType.SYSTEM}
)
public class Miss extends AbstractSkill implements Triggered {

    private ConfigurationSection chance;
    private PseudoRandomGenerator randomGenerator;

    public Miss(Hero hero, SkillProperties data, Profession profession, THeroSkill database) {

        super(hero, data, profession, database);
    }

    @Override
    public void load(ConfigurationSection data) {

        chance = data.getConfigurationSection("chance");
        randomGenerator = new PseudoRandomGenerator(getMissChance());
    }

    public double getMissChance() {

        return ConfigUtil.getTotalValue(this, chance);
    }

    @TriggerHandler(ignoreCancelled = true, priority = TriggerPriority.HIGHEST)
    public void onAttack(AttackTrigger trigger) throws CombatException {

        if (randomGenerator.getIteration() <= 1) {
            // recalculate the chance from our config
            randomGenerator.setChance(getMissChance());
        }
        if (randomGenerator.isHit()) {
            throw new CombatException(CombatException.Type.MISSED);
        }
    }
}
