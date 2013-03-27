package de.raidcraft.skills.items;

import de.raidcraft.util.EnumUtils;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Silthus
 */
public enum WeaponType {

    SWORD("Schwert",
            Material.WOOD_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLD_SWORD,
            Material.DIAMOND_SWORD
    ),
    AXE("Axt",
            Material.WOOD_AXE,
            Material.STONE_AXE,
            Material.IRON_AXE,
            Material.GOLD_AXE,
            Material.DIAMOND_AXE
    ),
    SPADE("Lanze",
            Material.WOOD_SPADE,
            Material.STONE_SPADE,
            Material.IRON_SPADE,
            Material.GOLD_SPADE,
            Material.DIAMOND_SPADE
    ),
    PICKAXE("Streitkolben",
            Material.WOOD_PICKAXE,
            Material.STONE_PICKAXE,
            Material.IRON_PICKAXE,
            Material.GOLD_PICKAXE,
            Material.DIAMOND_PICKAXE
    ),
    HOE("Lanze",
            Material.WOOD_HOE,
            Material.STONE_HOE,
            Material.IRON_HOE,
            Material.GOLD_HOE,
            Material.DIAMOND_HOE
    ),
    BOW("Bogen",
            Material.BOW
    ),
    MAGIC_WAND("Zauberstab",
            Material.STICK,
            Material.BLAZE_ROD
    );

    private final Set<Material> items = new HashSet<>();
    private final String friendlyName;

    private WeaponType(String friendlyName, Material... items) {

        this.friendlyName = friendlyName;
        this.items.addAll(Arrays.asList(items));
    }

    public boolean isOfType(Material material) {

        return items.contains(material);
    }

    public String getFriendlyName() {

        return friendlyName;
    }

    public static WeaponType fromMaterial(Material material) {

        return fromItemId(material.getId());
    }

    public static WeaponType fromItemId(int typeId) {

        for (WeaponType type : values()) {
            if (type.isOfType(Material.getMaterial(typeId))) {
                return type;
            }
        }
        return null;
    }

    public static WeaponType fromString(String name) {

        return EnumUtils.getEnumFromString(WeaponType.class, name);
    }
}
