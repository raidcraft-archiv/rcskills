package de.raidcraft.skills.hero;

import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.api.hero.AbstractHero;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.persistance.HeroData;
import de.raidcraft.skills.api.ui.BukkitUserInterface;
import de.raidcraft.skills.api.ui.UserInterface;

/**
 * @author Silthus
 */
public class SimpleHero extends AbstractHero {

    private final Level<Hero> level;
    private final UserInterface userInterface;

    public SimpleHero(HeroData data) throws UnknownPlayerException {

        super(data);
        this.level = new HeroLevel(this, data.getLevelData());
        this.userInterface = new BukkitUserInterface(this);
    }

    @Override
    public Level<Hero> getLevel() {

        return level;
    }

    @Override
    public UserInterface getUserInterface() {

        return userInterface;
    }

    @Override
    public void onExpGain(int exp) {
        //TODO: implement
    }

    @Override
    public void onExpLoss(int exp) {
        //TODO: implement
    }

    @Override
    public void onLevelGain(int level) {
        //TODO: implement
    }

    @Override
    public void onLevelLoss(int level) {
        //TODO: implement
    }
}
