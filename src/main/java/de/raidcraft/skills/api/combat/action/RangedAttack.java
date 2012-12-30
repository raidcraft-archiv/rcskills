package de.raidcraft.skills.api.combat.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.AttackType;
import de.raidcraft.skills.api.combat.ProjectileType;
import de.raidcraft.skills.api.combat.callback.RangedCallback;
import de.raidcraft.skills.api.combat.callback.SourcedRangeCallback;
import de.raidcraft.skills.api.exceptions.CombatException;
import org.bukkit.Location;
import org.bukkit.entity.Projectile;

/**
 * @author Silthus
 */
public class RangedAttack extends AbstractAttack<CharacterTemplate, Location> {

    private final ProjectileType projectileType;
    private RangedCallback callback;
    private Projectile projectile;

    public RangedAttack(CharacterTemplate source, ProjectileType projectileType) {

        super(source, source.getEntity().getTargetBlock(null, 100).getLocation(), 0,
                (projectileType == ProjectileType.FIREBALL ? AttackType.MAGICAL : AttackType.PHYSICAL));
        this.projectileType = projectileType;
    }

    public RangedAttack(CharacterTemplate source, ProjectileType projectileType, RangedCallback callback) {

        this(source, projectileType);
        this.callback = callback;
    }

    public void addCallback(RangedCallback callback) {

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
            RaidCraft.getComponent(SkillsPlugin.class).getCombatManager().queueCallback(
                    new SourcedRangeCallback(getSource(), projectile, callback)
            );
        }
    }
}
