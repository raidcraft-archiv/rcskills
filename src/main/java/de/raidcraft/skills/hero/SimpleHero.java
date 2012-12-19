package de.raidcraft.skills.hero;

import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.api.hero.AbstractHero;
import de.raidcraft.skills.api.persistance.HeroData;
import de.raidcraft.skills.api.ui.BukkitUserInterface;
import de.raidcraft.skills.api.ui.UserInterface;

/**
 * @author Silthus
 */
public class SimpleHero extends AbstractHero {

    private final UserInterface userInterface;

    public SimpleHero(HeroData data) throws UnknownPlayerException {

        super(data);
        attachLevel(new HeroLevel(this, data.getLevelData()));
        this.userInterface = new BukkitUserInterface(this);
    }

    @Override
    public UserInterface getUserInterface() {

        return userInterface;
    }
}
