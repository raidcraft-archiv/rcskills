package de.raidcraft.skills.api.hero;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class Nullbar extends AbstractResourceBar {

    public Nullbar(Hero hero, String name, ConfigurationSection config) {

        super(hero, name, config);
    }

    @Override
    public ChatColor getFilledColor() {

        return ChatColor.GRAY;
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

    @Override
    public int getMax() {

        return 0;
    }

    @Override
    public boolean isMax() {

        return true;
    }

    @Override
    public boolean isMin() {

        return true;
    }

    @Override
    public void regen() {
        // do nothing
    }

    @Override
    public String draw() {

        return ChatColor.RED + "Resource ist nicht verfügbar.";
    }
}
