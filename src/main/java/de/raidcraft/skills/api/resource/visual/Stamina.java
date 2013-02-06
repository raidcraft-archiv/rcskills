package de.raidcraft.skills.api.resource.visual;

import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.resource.VisualResource;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class Stamina implements VisualResource {

    @Override
    public void update(Resource resource) {

        Player player = resource.getHero().getPlayer();
        // set the stamina bar to a percentage of the actual stamina
        int stamina = (int) (((double) resource.getCurrent() / (double) resource.getMax()) * 20);
        player.setFoodLevel(stamina);
        // see the minecraft wiki for the mechanics: http://www.minecraftwiki.net/wiki/Hunger#Mechanics
        player.setSaturation(20.0F);
        player.setExhaustion(0.0F);
    }
}
