package de.raidcraft.skills.api.hero;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public enum ResourceType {

    MANA("Mana") {
        @Override
        public ResourceBar create(Hero hero, ConfigurationSection config) {

            return new Manabar(hero, this, config);
        }
    },

    RAGE("Wut") {
        @Override
        public ResourceBar create(Hero hero, ConfigurationSection config) {

            return new Ragebar(hero, this, config);
        }
    },

    ENERGY("Energie") {
        @Override
        public ResourceBar create(Hero hero, ConfigurationSection config) {

            return new Energybar(hero, this, config);
        }
    },

    UNKNOWN("Unknown") {
        @Override
        public ResourceBar create(Hero hero, ConfigurationSection config) {

            return new Nullbar(hero, this, config);
        }
    };

    private final String name;

    private ResourceType(String name) {

        this.name = name;
    }

    public String getName() {

        return name;
    }

    public abstract ResourceBar create(Hero hero, ConfigurationSection config);

    public static ResourceType fromName(String name) {

        for (ResourceType type : values()) {
            if (type.getName().equalsIgnoreCase(name) || type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
