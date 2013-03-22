package de.raidcraft.skills.hero;

import de.raidcraft.skills.api.hero.AbstractHero;
import de.raidcraft.skills.api.persistance.HeroData;
import de.raidcraft.skills.api.ui.BukkitUserInterface;
import de.raidcraft.skills.api.ui.UserInterface;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class SimpleHero extends AbstractHero {

    private final UserInterface userInterface;

    public SimpleHero(HeroData data) {

        super(data);
        this.userInterface = new BukkitUserInterface(this);
    }

    public SimpleHero(Player player, HeroData data) {

        super(player, data);
        this.userInterface = new BukkitUserInterface(this);
    }

    @Override
    public UserInterface getUserInterface() {

        return userInterface;
    }
}
