package de.raidcraft.skills.api.combat;

import de.raidcraft.skills.api.character.CharacterTemplate;
import org.bukkit.entity.*;

/**
 * @author Silthus
 */
public enum ProjectileType {

    ARROW(Arrow.class),
    EGG(Egg.class),
    SNOWBALL(Snowball.class),
    FIREBALL(Fireball.class);

    private final Class<? extends Projectile> clazz;

    private ProjectileType(Class<? extends Projectile> clazz) {

        this.clazz = clazz;
    }

    public Class<? extends Projectile> getClazz() {

        return clazz;
    }

    public Projectile spawn(CharacterTemplate character) {

        Projectile projectile = character.getEntity().launchProjectile(getClazz());
        projectile.setShooter(character.getEntity());
        return projectile;
    }

    public static ProjectileType matchProjectile(String name) {

        for (ProjectileType type : values()) {
            if (type.name().equalsIgnoreCase(name) || type.getClazz().getSimpleName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public static ProjectileType valueOf(Entity entity) {

        for (ProjectileType type : values()) {
            if (type.getClazz().isInstance(entity)) {
                return type;
            }
        }
        return null;
    }
}