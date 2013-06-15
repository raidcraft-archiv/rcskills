package de.raidcraft.skills.formulas;

import de.raidcraft.skills.api.level.forumla.AbstractFormula;
import de.raidcraft.skills.api.level.forumla.Param;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class WoWFormula extends AbstractFormula {

    @Param("modifier")
    private double modifier = 40.4;

    public WoWFormula(ConfigurationSection config) {

        super(config);
        loadParams();
    }

    @Override
    public int getNeededExpForLevel(int level) {

        return (int) Math.ceil((-.4 * Math.pow(level, 2)) + (modifier * Math.pow(level, 2)));
    }
}
