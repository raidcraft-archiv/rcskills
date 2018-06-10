package de.raidcraft.skills.api.combat;

import de.raidcraft.skills.api.character.CharacterTemplate;
import org.bukkit.Location;
import org.bukkit.entity.*;

/**
 * @author Silthus
 */
public enum ProjectileType {

    ARROW(Arrow.class),
    EGG(Egg.class),
    SNOWBALL(Snowball.class),
    FIREBALL(Fireball.class),
    LARGE_FIREBALL(LargeFireball.class),
    SMALL_FIREBALL(SmallFireball.class),
    WITHER_SKULL(WitherSkull.class),
    ENDER_PEARL(EnderPearl.class),
    FISH(Fish.class),
    EXP_BOTTLE(ThrownExpBottle.class),
    SPLASH_POTION(ThrownPotion.class);

    private final Class<? extends Projectile> clazz;

    ProjectileType(Class<? extends Projectile> clazz) {

        this.clazz = clazz;
    }

    public static ProjectileType fromName(String name) {

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

    public Projectile spawn(CharacterTemplate character) {

        Projectile projectile = character.getEntity().launchProjectile(getClazz());
        projectile.setShooter(character.getEntity());
        return projectile;
    }

    public Class<? extends Projectile> getClazz() {

        return clazz;
    }

    public Projectile spawn(Location location, CharacterTemplate source) {

        Projectile projectile = location.getWorld().spawn(location, getClazz());
        projectile.setShooter(source.getEntity());
        return projectile;
    }
}
