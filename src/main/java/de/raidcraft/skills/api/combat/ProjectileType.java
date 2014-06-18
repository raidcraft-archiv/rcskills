package de.raidcraft.skills.api.combat;

import de.raidcraft.skills.api.character.CharacterTemplate;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Fish;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.WitherSkull;

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

    public Projectile spawn(Location location, CharacterTemplate source) {

        Projectile projectile = location.getWorld().spawn(location, getClazz());
        projectile.setShooter(source.getEntity());
        return projectile;
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
}
