package de.raidcraft.skills.items;

import org.bukkit.Material;

/**
* @author Silthus
*/
public enum ArmorType {

    HEAD("Helm",
            Material.LEATHER_HELMET,
            Material.IRON_HELMET,
            Material.CHAINMAIL_HELMET,
            Material.GOLD_HELMET,
            Material.DIAMOND_HELMET
    ),
    CHEST("Brustplatte",
            Material.LEATHER_CHESTPLATE,
            Material.IRON_CHESTPLATE,
            Material.CHAINMAIL_CHESTPLATE,
            Material.GOLD_CHESTPLATE,
            Material.DIAMOND_CHESTPLATE
    ),
    LEGS("Beinschoner",
            Material.LEATHER_LEGGINGS,
            Material.IRON_LEGGINGS,
            Material.CHAINMAIL_LEGGINGS,
            Material.GOLD_LEGGINGS,
            Material.DIAMOND_LEGGINGS
    ),
    FEET("Schuhe",
            Material.LEATHER_BOOTS,
            Material.IRON_BOOTS,
            Material.CHAINMAIL_BOOTS,
            Material.GOLD_BOOTS,
            Material.DIAMOND_BOOTS
    ),
    SHIELD("Schild",
            Material.TRAP_DOOR,
            Material.IRON_DOOR,
            Material.WOOD_DOOR,
            Material.WOODEN_DOOR
    );

    private final String friendylName;
    private final Material[] armor;

    ArmorType(String friendylName, Material... armor) {

        this.friendylName = friendylName;
        this.armor = armor;
    }

    public String getFriendylName() {

        return friendylName;
    }

    public Material[] getArmor() {

        return armor;
    }

    public static ArmorType fromItemId(int id) {

        for (ArmorType slot : ArmorType.values()) {
            for (Material armor : slot.getArmor()) {
                if (id == armor.getId()) {
                    return slot;
                }
            }
        }
        return null;
    }

    public static ArmorType fromMaterial(Material material) {

        return fromItemId(material.getId());
    }
}
