package de.raidcraft.skills.items;

import org.bukkit.Material;

/**
 * @author Silthus
 */
public enum ToolType {

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
    SHEAR(
            Material.SHEARS
    );

    private final Material[] armor;

    ToolType(Material... armor) {

        this.armor = armor;
    }

    public Material[] getTools() {

        return armor;
    }

    public static ToolType fromMaterial(Material material) {

        for (ToolType type : ToolType.values()) {
            for (Material tool : type.getTools()) {
                if (material == tool) {
                    return type;
                }
            }
        }
        return null;
    }
}
