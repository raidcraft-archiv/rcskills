package de.raidcraft.skills.api.combat.attack;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
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

    public RangedAttack(CharacterTemplate source, ProjectileType projectileType) {

        super(source, source.getEntity().getTargetBlock(null, 100).getLocation(), 0);
        this.projectileType = projectileType;
    }

    public RangedAttack(CharacterTemplate attacker, ProjectileType projectileType, RangedCallback callback) {

        this(attacker, projectileType);
        this.callback = callback;
    }

    @Override
    public void run() throws CombatException, InvalidTargetException {

        // TODO: add fancy resitence checks and so on
        Projectile projectile = projectileType.spawn(getSource());
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
