package de.raidcraft.skills.api.hero;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class Ragebar extends AbstractResourceBar {


    public Ragebar(Hero hero, ResourceType type, ConfigurationSection config) {

        super(hero, type, config);
    }

    @Override
    public ChatColor getFilledColor() {

        return ChatColor.DARK_RED;
    }

    @Override
    public ChatColor getUnfilledColor() {

        return ChatColor.GRAY;
    }

    @Override
    public int getDefault() {

        return 0;
    }

    @Override
    public int getMin() {

        return 0;
    }
}
