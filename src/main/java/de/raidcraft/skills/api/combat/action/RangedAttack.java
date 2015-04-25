package de.raidcraft.skills.api.combat.action;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.ProjectileType;
import de.raidcraft.skills.api.combat.callback.ProjectileCallback;
import de.raidcraft.skills.api.combat.callback.SourcedRangeCallback;
import de.raidcraft.skills.api.exceptions.CombatException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;

/**
 * @author Silthus
 */
public class RangedAttack<T extends ProjectileCallback> extends AbstractAttack<CharacterTemplate, Location> {

    private final ProjectileType projectileType;
    private T callback;
    private Projectile projectile;
    private Vector velocity;
    private Location spawnLocation;
    private float force = 1.0F;

    public RangedAttack(CharacterTemplate source, ProjectileType projectileType) {

        this(source, projectileType, source.getDamage());
    }

    public RangedAttack(CharacterTemplate source, ProjectileType projectileType, double damage) {

        super(source, source.getEntity().getTargetBlock(new HashSet<Material>(), 100).getLocation(), damage,
                (projectileType == ProjectileType.FIREBALL ? EffectType.MAGICAL : EffectType.PHYSICAL));
        this.projectileType = projectileType;
        addAttackTypes(EffectType.RANGE);
    }

    public RangedAttack(CharacterTemplate source, EntityShootBowEvent event) {

        this(source, event, null);
    }

    public RangedAttack(CharacterTemplate source, EntityShootBowEvent event, T callback) {

        this(source, ProjectileType.ARROW, callback);
        this.force = event.getForce();
    }

    public RangedAttack(CharacterTemplate source, ProjectileType projectileType, T callback) {

        this(source, projectileType, source.getDamage(), callback);
    }

    public RangedAttack(CharacterTemplate source, ProjectileType projectileType, double damage, T callback) {

        this(source, projectileType, damage);
        this.callback = callback;
    }

    public void addCallback(T callback) {

        this.callback = callback;
    }

    public ProjectileType getProjectileType() {

        return projectileType;
    }

    public Projectile getProjectile() {

        return projectile;
    }

    public void setProjectile(Projectile projectile) {

        this.projectile = projectile;
    }

    public T getCallback() {

        return callback;
    }

    public void setCallback(T callback) {

        this.callback = callback;
    }

    public Vector getVelocity() {

        return velocity;
    }

    public void setVelocity(Vector velocity) {

        this.velocity = velocity;
    }

    @Override
    public double getDamage() {

        return super.getDamage() * getForce();
    }

    public float getForce() {

        return force;
    }

    public void setForce(float force) {

        this.force = force;
    }

    @Override
    public void run() throws CombatException {

        if (projectile == null) {
            projectile = (getSpawnLocation() != null ? projectileType.spawn(getSpawnLocation(), getSource()) : projectileType.spawn(getSource()));
            projectile.setBounce(false);
            projectile.setFireTicks(0);
            if (projectile instanceof Explosive) {
                ((Explosive) projectile).setIsIncendiary(false);
            }
        }
        if (velocity != null) projectile.setVelocity(velocity);
        // queue the ranged callback to be called if the projectile hits
        SourcedRangeCallback<T> rangeCallback = new SourcedRangeCallback<>(this);
        rangeCallback.queueCallback();
        getSource().setLastAction(this);
    }

    public Location getSpawnLocation() {

        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {

        this.spawnLocation = spawnLocation;
    }
}
