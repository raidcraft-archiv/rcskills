package de.raidcraft.skills.items;

import de.raidcraft.util.EnumUtils;
import org.bukkit.Material;

/**
 * @author Silthus
 */
public enum ToolType {

    AXE("Axt",
            Material.WOOD_AXE,
            Material.STONE_AXE,
            Material.IRON_AXE,
            Material.GOLD_AXE,
            Material.DIAMOND_AXE
    ),
    SHOVEL("Schaufel",
            Material.WOOD_SPADE,
            Material.STONE_SPADE,
            Material.IRON_SPADE,
            Material.GOLD_SPADE,
            Material.DIAMOND_SPADE
    ),
    PICKAXE("Pickaxt",
            Material.WOOD_PICKAXE,
            Material.STONE_PICKAXE,
            Material.IRON_PICKAXE,
            Material.GOLD_PICKAXE,
            Material.DIAMOND_PICKAXE
    ),
    HOE("Harke",
            Material.WOOD_HOE,
            Material.STONE_HOE,
            Material.IRON_HOE,
            Material.GOLD_HOE,
            Material.DIAMOND_HOE
    ),
    SHEAR("Scheere",
            Material.SHEARS
    );

    private final String friendlyName;
    private final Material[] armor;

    ToolType(String friendlyName, Material... armor) {

        this.friendlyName = friendlyName;
        this.armor = armor;
    }

    public String getFriendlyName() {

        return friendlyName;
    }

    public Material[] getTools() {

        return armor;
    }

    public boolean isOfType(int itemId) {

        for (Material mat : getTools()) {
            if (mat.getId() == itemId) {
                return true;
            }
        }
        return false;
    }

    public boolean isOfType(Material material) {

        return isOfType(material.getId());
    }

    public static ToolType fromItemId(int itemId) {

        for (ToolType type : ToolType.values()) {
            for (Material tool : type.getTools()) {
                if (tool.getId() == itemId) {
                    return type;
                }
            }
        }
        return null;
    }

    public static ToolType fromMaterial(Material material) {

        return fromItemId(material.getId());
    }

    public static ToolType fromName(String toolName) {

        return EnumUtils.getEnumFromString(ToolType.class, toolName);
    }
}