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

    protected WoWFormula(ConfigurationSection config) {

        super(config);
        loadParams();
    }

    @Override
    public int getNeededExpForLevel(int level) {

        return (int) (-.4 * Math.pow(level, 3) + modifier * Math.pow(level, 2));
    }
}
