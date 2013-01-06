package de.raidcraft.skills.api.hero;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class Ragebar extends AbstractResourceBar {


    public Ragebar(Hero hero, String name, ConfigurationSection config) {

        super(hero, name, config);
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
