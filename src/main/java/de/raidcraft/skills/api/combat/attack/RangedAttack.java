package de.raidcraft.skills.api.combat.attack;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.callback.RangedCallback;
import de.raidcraft.skills.api.combat.callback.SourcedCallback;
import de.raidcraft.skills.api.exceptions.CombatException;
import org.bukkit.Location;
import org.bukkit.entity.Projectile;

/**
 * @author Silthus
 */
public class RangedAttack extends AbstractAttack<CharacterTemplate, Location> {

    private final Class<? extends Projectile> projectile;
    private RangedCallback callback;

    public RangedAttack(CharacterTemplate source, Class<? extends Projectile> projectile) {

        super(source, source.getEntity().getTargetBlock(null, 100).getLocation(), 0);
        this.projectile = projectile;
    }

    public RangedAttack(CharacterTemplate attacker, Class<? extends Projectile> projectile, RangedCallback callback) {

        this(attacker, projectile);
        this.callback = callback;
    }

    @Override
    public void run() throws CombatException, InvalidTargetException {

        // TODO: add fancy resitence checks and so on
        Projectile projectile = getSource().getEntity().launchProjectile(this.projectile);
        projectile.setShooter(getSource().getEntity());
        projectile.setBounce(false);
        projectile.setFireTicks(0);
        // queue the ranged callback to be called if the projectile hits
        if (callback != null) {
            RaidCraft.getComponent(SkillsPlugin.class).getCombatManager().queueCallback(
                    new SourcedCallback(getSource(), callback)
            );
        }
    }
}
