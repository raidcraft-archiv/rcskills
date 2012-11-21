package de.raidcraft.skills.hero;

import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.PlayerComponent;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.level.SimpleLevel;
import de.raidcraft.skills.api.hero.AbstractHero;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.tables.PlayerTable;

/**
 * @author Silthus
 */
public class RCHero extends AbstractHero implements PlayerComponent {


    protected RCHero(HeroData heroData) throws UnknownPlayerException {

        super(heroData);
        attachLevel(new SimpleLevel<Hero>(this,
                Database.getTable(PlayerTable.class).getLevelData(getName())));
    }

    @Override
    public void increaseLevel(Level<Hero> level) {
        //TODO: implement
    }

    @Override
    public void decreaseLevel(Level<Hero> level) {
        //TODO: implement
    }

    @Override
    public void saveLevelProgress(Level<Hero> level) {
        //TODO: implement
    }
}
