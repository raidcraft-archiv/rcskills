package de.raidcraft.skills.items;

import de.raidcraft.util.EnumUtils;
import org.bukkit.Material;

/**
 * @author Silthus
 */
public enum ToolType {

    AXE("Axt",
            Material.WOODEN_AXE,
            Material.STONE_AXE,
            Material.IRON_AXE,
            Material.GOLDEN_AXE,
            Material.DIAMOND_AXE
    ),
    SHOVEL("Schaufel",
            Material.WOODEN_SHOVEL,
            Material.STONE_SHOVEL,
            Material.IRON_SHOVEL,
            Material.GOLDEN_SHOVEL,
            Material.DIAMOND_SHOVEL
    ),
    PICKAXE("Pickaxt",
            Material.WOODEN_PICKAXE,
            Material.STONE_PICKAXE,
            Material.IRON_PICKAXE,
            Material.GOLDEN_PICKAXE,
            Material.DIAMOND_PICKAXE
    ),
    HOE("Harke",
            Material.WOODEN_HOE,
            Material.STONE_HOE,
            Material.IRON_HOE,
            Material.GOLDEN_HOE,
            Material.DIAMOND_HOE
    ),
    FISHING_ROD("Angel",
            Material.FISHING_ROD
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

    public static ToolType fromMaterial(Material material) {

        return fromItemId(material.getId());
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

    public Material[] getTools() {

        return armor;
    }

    public static ToolType fromName(String toolName) {

        return EnumUtils.getEnumFromString(ToolType.class, toolName);
    }

    public String getFriendlyName() {

        return friendlyName;
    }

    public boolean isOfType(Material material) {

        return isOfType(material.getId());
    }

    public boolean isOfType(int itemId) {

        for (Material mat : getTools()) {
            if (mat.getId() == itemId) {
                return true;
            }
        }
        return false;
    }
}