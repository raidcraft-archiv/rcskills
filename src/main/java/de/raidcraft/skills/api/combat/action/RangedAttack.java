package de.raidcraft.skills.api.combat.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.ProjectileType;
import de.raidcraft.skills.api.combat.callback.RangedCallback;
import de.raidcraft.skills.api.combat.callback.LocationCallback;
import de.raidcraft.skills.api.combat.callback.ProjectileCallback;
import de.raidcraft.skills.api.combat.callback.SourcedRangeCallback;
import de.raidcraft.skills.api.exceptions.CombatException;
import org.bukkit.Location;
import org.bukkit.entity.Projectile;

/**
 * @author Silthus
 */
public class RangedAttack<T> extends AbstractAttack<CharacterTemplate, Location> {

    private final ProjectileType projectileType;
    private ProjectileCallback<T> callback;
    private Projectile projectile;

    public RangedAttack(CharacterTemplate source, ProjectileType projectileType) {

        super(source, source.getEntity().getTargetBlock(null, 100).getLocation(), 0,
                (projectileType == ProjectileType.FIREBALL ? EffectType.MAGICAL : EffectType.PHYSICAL));
        this.projectileType = projectileType;
    }

    public RangedAttack(CharacterTemplate source, ProjectileType projectileType, ProjectileCallback<T> callback) {

        this(source, projectileType);
        this.callback = callback;
    }

    public void addCallback(ProjectileCallback<T> callback) {

        this.callback = callback;
    }

    public ProjectileType getProjectileType() {

        return projectileType;
    }

    public Projectile getProjectile() {

        return projectile;
    }

    @Override
    public void run() throws CombatException {

        projectile = projectileType.spawn(getSource());
        projectile.setBounce(false);
        projectile.setFireTicks(0);
        // queue the ranged callback to be called if the projectile hits
        if (callback != null) {
            if (callback instanceof RangedCallback) {
                SourcedRangeCallback<CharacterTemplate> callback =
                        new SourcedRangeCallback<>(getSource(), projectile, (RangedCallback) this.callback);
                RaidCraft.getComponent(SkillsPlugin.class).getCombatManager().queueEntityCallback(callback);
            } else if (callback instanceof LocationCallback) {
                SourcedRangeCallback<Location> callback =
                        new SourcedRangeCallback<>(getSource(), projectile, (LocationCallback) this.callback);
                RaidCraft.getComponent(SkillsPlugin.class).getCombatManager().queueLocationCallback(callback);
            }
        }
    }
}
