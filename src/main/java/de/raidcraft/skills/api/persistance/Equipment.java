package de.raidcraft.skills.api.persistance;

import de.raidcraft.api.config.DataMap;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class Equipment extends DataMap {

    private final Material material;
    private final byte data;

    public Equipment(Material id, int data, ConfigurationSection config) {

        super(config);
        this.material = id;
        this.data = (byte) data;
    }

    public Equipment(Equipment equipment) {

        super(equipment.getValues(true));
        this.material = equipment.getType();
        this.data = equipment.getData();
    }

    public Material getType() {

        return material;
    }

    public byte getData() {

        return data;
    }

    public int getMinLevel() {

        return getInt("level", 1);
    }

    public int getBaseDamage() {

        return getInt("damage.base");
    }

    public double getDamageLevelModifier() {

        return getDouble("damage.level-modifier", 0.0);
    }

    public double getDamageProfessionLevelModifier() {

        return getDouble("damage.prof-level-modifier", 0.0);
    }
}
