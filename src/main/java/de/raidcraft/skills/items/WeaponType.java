package de.raidcraft.skills.items;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Silthus
 */
public enum WeaponType {

    SWORD(
            Material.WOOD_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLD_SWORD,
            Material.DIAMOND_SWORD
    ),
    AXE(
            Material.WOOD_AXE,
            Material.STONE_AXE,
            Material.IRON_AXE,
            Material.GOLD_AXE,
            Material.DIAMOND_AXE
    ),
    SPADE(
            Material.WOOD_SPADE,
            Material.STONE_SPADE,
            Material.IRON_SPADE,
            Material.GOLD_SPADE,
            Material.DIAMOND_SPADE
    ),
    PICKAXE(
            Material.WOOD_PICKAXE,
            Material.STONE_PICKAXE,
            Material.IRON_PICKAXE,
            Material.GOLD_PICKAXE,
            Material.DIAMOND_PICKAXE
    ),
    HOE(
            Material.WOOD_HOE,
            Material.STONE_HOE,
            Material.IRON_HOE,
            Material.GOLD_HOE,
            Material.DIAMOND_HOE
    ),
    BOW(
            Material.BOW
    );

    private final Set<Material> items = new HashSet<>();

    private WeaponType(Material... items) {

        this.items.addAll(Arrays.asList(items));
    }

    public boolean isOfType(Material material) {

        return items.contains(material);
    }

    public static WeaponType fromMaterial(Material material) {

        for (WeaponType type : values()) {
            if (type.isOfType(material)) {
                return type;
            }
        }
        return null;
    }
}
