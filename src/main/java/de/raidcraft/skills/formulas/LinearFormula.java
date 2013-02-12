package de.raidcraft.skills.formulas;

import de.raidcraft.skills.api.level.forumla.AbstractFormula;
import de.raidcraft.skills.api.level.forumla.Param;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class LinearFormula extends AbstractFormula {

    @Param("x")
    private double x = 50;

    protected LinearFormula(ConfigurationSection config) {

        super(config);
        loadParams();
    }

    @Override
    public int getNeededExpForLevel(int level) {

        return (int) (x * level);
    }
}
