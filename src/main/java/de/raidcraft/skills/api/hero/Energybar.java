package de.raidcraft.skills.api.hero;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class Energybar extends AbstractResourceBar {


    public Energybar(Hero hero, String name, ConfigurationSection config) {

        super(hero, name, config);
    }

    @Override
    public ChatColor getFilledColor() {

        return ChatColor.YELLOW;
    }

    @Override
    public ChatColor getUnfilledColor() {

        return ChatColor.GRAY;
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
