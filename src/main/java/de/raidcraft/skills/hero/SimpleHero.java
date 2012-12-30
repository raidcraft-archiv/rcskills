package de.raidcraft.skills.hero;

import de.raidcraft.skills.api.hero.AbstractHero;
import de.raidcraft.skills.api.persistance.HeroData;
import de.raidcraft.skills.api.ui.BukkitUserInterface;
import de.raidcraft.skills.api.ui.UserInterface;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public class SimpleHero extends AbstractHero {

    private final UserInterface userInterface;

    public SimpleHero(HeroData data) {

        super(data);
        attachLevel(new HeroLevel(this, data.getLevelData()));
        this.userInterface = new BukkitUserInterface(this);
    }

    @Override
    public UserInterface getUserInterface() {

        return userInterface;
    }

    @Override
    public void onExpGain(int exp) {}

    @Override
    public void onExpLoss(int exp) {}

    @Override
    public void onLevelGain(int level) {

        sendMessage(ChatColor.GREEN + "Du bist ein Level aufgestiegen: " +
                ChatColor.ITALIC + ChatColor.YELLOW + " Level " + getLevel().getLevel());
    }

    @Override
    public void onLevelLoss(int level) {

        sendMessage(ChatColor.RED + "Du bist ein Level abgestiegen: " +
                ChatColor.ITALIC + ChatColor.YELLOW + " Level " + getLevel().getLevel());
    }
}
