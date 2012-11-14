package de.raidcraft.skills.skills.magic;

import de.raidcraft.api.bukkit.BukkitPlayer;
import de.raidcraft.skills.api.Active;
import de.raidcraft.skills.api.skill.AbstractObtainableSkill;
import de.raidcraft.skills.trigger.InteractTrigger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.event.block.Action;

/**
 * @author Silthus
 */
public class RCFireball extends AbstractObtainableSkill implements Active<InteractTrigger> {

    public RCFireball(int id) {

        super(id);
    }

    @Override
    public void run(InteractTrigger trigger) {

        if (trigger.getAction() == Action.RIGHT_CLICK_AIR) {
            if (trigger.getPlayer() instanceof BukkitPlayer) {
                World world = Bukkit.getWorld(trigger.getPlayer().getWorld());
                Fireball fireball = (Fireball) world.spawnEntity(null, EntityType.FIREBALL);
                fireball.setShooter(((BukkitPlayer) trigger.getPlayer()).getBukkitPlayer());
            }
        }
    }
}
