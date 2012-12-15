package de.raidcraft.skills.api.combat;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Snowball;

/**
 * @author Silthus
 */
public enum ProjectileType {

    ARROW,
    EGG,
    SNOWBALL;

    public static ProjectileType matchProjectile(String name) {
        if (name.equalsIgnoreCase("arrow"))
            return ARROW;
        if (name.equalsIgnoreCase("snowball"))
            return SNOWBALL;
        if (name.equalsIgnoreCase("egg")) {
            return EGG;
        }
        return null;
    }

    public static ProjectileType valueOf(Entity entity) {
        if ((entity instanceof Arrow))
            return ARROW;
        if ((entity instanceof Snowball))
            return SNOWBALL;
        if ((entity instanceof Egg)) {
            return EGG;
        }
        return null;
    }
}
