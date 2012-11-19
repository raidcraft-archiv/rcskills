package de.raidcraft.skills.skills.magic;

import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.Active;
import de.raidcraft.skills.api.skill.AbstractLevelableSkill;
import de.raidcraft.skills.trigger.InteractTrigger;
import de.raidcraft.spells.fire.RCFireball;
import org.bukkit.event.block.Action;

/**
 * @author Silthus
 */
public class Fireball extends AbstractLevelableSkill implements Active<InteractTrigger> {

    public Fireball(int id, RCPlayer player) {

        super(id, player);
    }

    @Override
    public void run(InteractTrigger trigger) {

        if (trigger.getAction() == Action.RIGHT_CLICK_AIR) {

            RCFireball fireball = new RCFireball();
            // TODO: set variable strength of the fireball based on the skill level
            fireball.fireTicks = 60;
            fireball.incinerate = true;
            fireball.run(getHero().getBukkitPlayer());
        }
    }
}
