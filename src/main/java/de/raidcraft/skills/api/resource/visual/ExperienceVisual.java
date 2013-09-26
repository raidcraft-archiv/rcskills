package de.raidcraft.skills.api.resource.visual;

import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.resource.VisualResource;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class ExperienceVisual implements VisualResource {

    @Override
    public void update(Resource resource) {

        Player player = resource.getHero().getPlayer();
        player.setExp((float) resource.getPercentage());
        player.setTotalExperience(0);
    }
}
