package de.raidcraft.skills.formulas;

import de.raidcraft.skills.api.level.forumla.AbstractFormula;
import de.raidcraft.skills.api.level.forumla.Param;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class MCMMOFormula extends AbstractFormula {

    @Param("base")
    private int base = 200;
    @Param("x")
    private double x = 40.5;

    public MCMMOFormula(ConfigurationSection config) {

        super(config);
        loadParams();
    }

    @Override
    public int getNeededExpForLevel(int level) {

        return (int) (base + x * level);
    }
}
