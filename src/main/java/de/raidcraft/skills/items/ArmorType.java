package de.raidcraft.skills.items;

import org.bukkit.Material;

/**
* @author Silthus
*/
public enum ArmorType {

    HEAD(103,
            Material.LEATHER_HELMET,
            Material.IRON_HELMET,
            Material.CHAINMAIL_HELMET,
            Material.GOLD_HELMET,
            Material.DIAMOND_HELMET
    ),
    CHEST(102,
            Material.LEATHER_CHESTPLATE,
            Material.IRON_CHESTPLATE,
            Material.CHAINMAIL_CHESTPLATE,
            Material.GOLD_CHESTPLATE,
            Material.DIAMOND_CHESTPLATE
    ),
    LEGS(101,
            Material.LEATHER_LEGGINGS,
            Material.IRON_LEGGINGS,
            Material.CHAINMAIL_LEGGINGS,
            Material.GOLD_LEGGINGS,
            Material.DIAMOND_LEGGINGS
    ),
    FEET(100,
            Material.LEATHER_BOOTS,
            Material.IRON_BOOTS,
            Material.CHAINMAIL_BOOTS,
            Material.GOLD_BOOTS,
            Material.DIAMOND_BOOTS
    ),
    SHIELD(-1,
            Material.TRAP_DOOR,
            Material.IRON_DOOR,
            Material.WOOD_DOOR,
            Material.WOODEN_DOOR
    );

    private final int slotId;
    private final Material[] armor;

    ArmorType(int slotId, Material... armor) {

        this.slotId = slotId;
        this.armor = armor;
    }

    public int getSlotId() {

        return slotId;
    }

    public Material[] getArmor() {

        return armor;
    }

    public static ArmorType fromMaterial(Material material) {

        for (ArmorType slot : ArmorType.values()) {
            for (Material armor : slot.getArmor()) {
                if (material == armor) {
                    return slot;
                }
            }
        }
        return null;
    }
}
