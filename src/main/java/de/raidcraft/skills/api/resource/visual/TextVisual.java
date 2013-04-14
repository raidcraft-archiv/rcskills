package de.raidcraft.skills.api.resource.visual;

import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.resource.VisualResource;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class TextVisual implements VisualResource {

    @Override
    public void update(Resource resource) {

        StringBuilder resourceBar = new StringBuilder(getFilledColor(resource) + resource.getFriendlyName() + ": ");

        resourceBar.append(String.valueOf(ChatColor.RED)).append("[").append(getFilledColor(resource));
        int percent = (int) (((double) resource.getCurrent() / (double) resource.getMax()) * 100);
        int progress = percent / 2;
        for (int i = 0; i < progress; i++) {
            resourceBar.append('|');
        }
        resourceBar.append(getUnfilledColor(resource));
        for (int i = 0; i < 50 - progress; i++) {
            resourceBar.append('|');
        }
        resourceBar.append(ChatColor.RED).append(']');

        String out = String.valueOf(resourceBar) + " - " + getFilledColor(resource) + percent + "%";
        resource.getHero().sendMessage(out);
    }

    private ChatColor getFilledColor(Resource resource) {

        ConfigurationSection config = resource.getConfig();
        String string = config.getString("color.filled", "BLUE");
        ChatColor color = ChatColor.valueOf(string);
        if (color == null) color = ChatColor.getByChar(string);
        if (color == null) color = ChatColor.BLUE;
        return color;
    }

    private ChatColor getUnfilledColor(Resource resource) {

        ConfigurationSection config = resource.getConfig();
        String string = config.getString("color.unfilled", "DARK_RED");
        ChatColor color = ChatColor.valueOf(string);
        if (color == null) color = ChatColor.getByChar(string);
        if (color == null) color = ChatColor.DARK_RED;
        return color;
    }
}
