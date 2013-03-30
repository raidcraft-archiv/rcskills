package de.raidcraft.skills.api.effect.common;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.LevelableSkill;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Sundering Armor",
        description = "Verringt der Rüstung des Ziels - Stapelbar.",
        types = {EffectType.HARMFUL, EffectType.DEBUFF, EffectType.PHYSICAL},
        priority = 1.0
)
public class SunderingArmor extends ExpirableEffect<Skill> {

    private double armorReduction = 0.05;
    private double armorReductionPerStack;
    private double armorReductionCap = 0.6;

    public SunderingArmor(Skill source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    public double getArmorReduction() {

        return armorReduction;
    }

    @Override
    public void load(ConfigurationSection data) {

        armorReductionPerStack = data.getDouble("reduction.base", 0.05);
        armorReductionPerStack += data.getDouble("reduction.level-modifier") * getSource().getHero().getAttachedLevel().getLevel();
        armorReductionPerStack += data.getDouble("reduction.prof-level-modifier") * getSource().getProfession().getAttachedLevel().getLevel();

        if (getSource() instanceof LevelableSkill) {
            armorReductionPerStack += data.getDouble("reduction.skill-level-modifier") * ((LevelableSkill) getSource()).getAttachedLevel().getLevel();
        }
        armorReductionCap = data.getDouble("reduction.cap", 0.6);
        // cap reduction default is 60%
        if (armorReductionCap < armorReductionPerStack) {
            armorReductionPerStack = armorReductionCap;
        }
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        armorReduction = armorReductionPerStack;
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

        if (armorReduction + armorReductionPerStack > armorReductionCap) {
            return;
        }
        armorReduction += armorReductionPerStack;
    }
}
