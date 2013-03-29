package de.raidcraft.skills.api.combat.action;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.ProjectileType;
import de.raidcraft.skills.api.combat.callback.ProjectileCallback;
import de.raidcraft.skills.api.combat.callback.SourcedRangeCallback;
import de.raidcraft.skills.api.exceptions.CombatException;
import org.bukkit.Location;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

/**
 * @author Silthus
 */
public class RangedAttack<T extends ProjectileCallback> extends AbstractAttack<CharacterTemplate, Location> {

    private final ProjectileType projectileType;
    private T callback;
    private Projectile projectile;
    private Vector velocity;

    public RangedAttack(CharacterTemplate source, ProjectileType projectileType, int damage) {

        super(source, source.getEntity().getTargetBlock(null, 100).getLocation(), damage,
                (projectileType == ProjectileType.FIREBALL ? EffectType.MAGICAL : EffectType.PHYSICAL));
        this.projectileType = projectileType;
    }

    public RangedAttack(CharacterTemplate source, ProjectileType projectileType) {

        this(source, projectileType, source.getDamage());
    }

    public RangedAttack(CharacterTemplate source, ProjectileType projectileType, T callback) {

        this(source, projectileType, source.getDamage(), callback);
    }

    public RangedAttack(CharacterTemplate source, ProjectileType projectileType, int damage, T callback) {

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

    public void setCallback(T callback) {

        this.callback = callback;
    }

    public T getCallback() {

        return callback;
    }

    public Vector getVelocity() {

        return velocity;
    }

    public void setVelocity(Vector velocity) {

        this.velocity = velocity;
    }

    @Override
    public void run() throws CombatException {

        if (projectile == null) projectile = projectileType.spawn(getSource());
        projectile.setBounce(false);
        projectile.setFireTicks(0);
        if (velocity != null) projectile.setVelocity(velocity);
        // queue the ranged callback to be called if the projectile hits
        SourcedRangeCallback<T> rangeCallback = new SourcedRangeCallback<>(this);
        rangeCallback.queueCallback();
    }
}
