package de.raidcraft.skills.api.persistance;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

/**
 * @author Silthus
 */
public class Equipment extends ItemStack {

    private int minLevel;
    private int baseDamage;
    private double damageLevelModifier;
    private double damageProfessionLevelModifier;

    public Equipment(ConfigurationSection config) {

        super(config.getItemStack(""));
        minLevel = config.getInt("min-level", 1);
        baseDamage = config.getInt("damage.base", 0);
        damageLevelModifier = config.getDouble("damage.level-modifier", 0.0);
        damageProfessionLevelModifier = config.getDouble("damage.prof-level-modifier", 0.0);
    }

    public int getMinLevel() {

        return minLevel;
    }

    public int getBaseDamage() {

        return baseDamage;
    }

    public double getDamageLevelModifier() {

        return damageLevelModifier;
    }

    public double getDamageProfessionLevelModifier() {

        return damageProfessionLevelModifier;
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof ItemStack
                && ((ItemStack) obj).getType() == getType()
                && ((ItemStack) obj).getData().getData() == getData().getData();
    }
}
