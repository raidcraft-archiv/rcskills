package de.raidcraft.skills.formulas;

import de.raidcraft.skills.api.level.forumla.AbstractFormula;
import de.raidcraft.skills.api.level.forumla.Param;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class StaticFormula extends AbstractFormula {

    @Param("amount")
    private int amount = 1;

    protected StaticFormula(ConfigurationSection config) {

        super(config);
        loadParams();
    }

    @Override
    public int getNeededExpForLevel(int level) {

        return amount;
    }
}
