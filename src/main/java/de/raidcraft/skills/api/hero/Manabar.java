package de.raidcraft.skills.api.hero;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class Manabar extends AbstractResourceBar {


    public Manabar(Hero hero, String name, ConfigurationSection config) {

        super(hero, name, config);
    }

    @Override
    public ChatColor getFilledColor() {

        return ChatColor.BLUE;
    }

    @Override
    public ChatColor getUnfilledColor() {

        return ChatColor.DARK_RED;
    }

    @Override
    public int getDefault() {

        return getMax();
    }

    @Override
    public int getMin() {

        return 0;
    }
}
