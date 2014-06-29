package de.raidcraft.skills.api.effect.common;

import de.raidcraft.skills.api.ability.Ability;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.util.ConfigUtil;
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
public class SunderingArmor extends ExpirableEffect<Ability> {

    private double armorReduction = 0.05;
    private double armorReductionPerStack;
    private double armorReductionCap = 0.6;

    public SunderingArmor(Ability source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    public double getArmorReduction() {

        return armorReduction;
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        armorReduction = armorReductionPerStack;
    }

    @Override
    public void load(ConfigurationSection data) {

        armorReductionPerStack = ConfigUtil.getTotalValue(getSource(), data.getConfigurationSection("reduction"));
        armorReductionCap = ConfigUtil.getTotalValue(getSource(), data.getConfigurationSection("reduction-cap"));
        // cap reduction default is 60%
        if (armorReductionCap < armorReductionPerStack) {
            armorReductionPerStack = armorReductionCap;
        }
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

        if (armorReduction + armorReductionPerStack > armorReductionCap) {
            return;
        }
        armorReduction += armorReductionPerStack;
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

    }
}
