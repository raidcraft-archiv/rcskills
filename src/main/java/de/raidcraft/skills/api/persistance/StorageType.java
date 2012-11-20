package de.raidcraft.skills.api.persistance;

import de.raidcraft.util.EnumUtils;

/**
 * @author Silthus
 */
public enum StorageType {

    MYSQL("sql"),
    SQLITE(),
    YAML("yml");

    private final String[] aliases;

    private StorageType(String... aliases) {

        this.aliases = aliases;
    }

    public static StorageType fromString(String type) {

        for (StorageType t : values()) {
            for (String s : t.aliases) {
                if (s.equalsIgnoreCase(type)) {
                    return t;
                }
            }
        }
        return EnumUtils.getEnumFromString(StorageType.class, type);
    }
}
