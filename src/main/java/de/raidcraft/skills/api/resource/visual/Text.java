package de.raidcraft.skills.api.resource.visual;

import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.resource.VisualResource;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public class Text implements VisualResource {

    @Override
    public void update(Resource resource) {

        StringBuilder resourceBar = new StringBuilder(resource.getFilledColor() + resource.getFriendlyName() + ": ");

        resourceBar.append(String.valueOf(ChatColor.RED)).append("[").append(resource.getFilledColor());
        int percent = (int) ((resource.getCurrent() / resource.getMax()) * 100.0);
        int progress = percent / 2;
        for (int i = 0; i < progress; i++) {
            resourceBar.append('|');
        }
        resourceBar.append(resource.getUnfilledColor());
        for (int i = 0; i < 50 - progress; i++) {
            resourceBar.append('|');
        }
        resourceBar.append(ChatColor.RED).append(']');

        String out = String.valueOf(resourceBar) + " - " + resource.getFilledColor() + percent + "%";
        resource.getHero().sendMessage(out);
    }
}
