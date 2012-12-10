package de.raidcraft.skills.hero;

import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.api.hero.AbstractHero;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.persistance.HeroData;

/**
 * @author Silthus
 */
public class SimpleHero extends AbstractHero {


    public SimpleHero(HeroData data) throws UnknownPlayerException {

        super(data);
        attachLevel(new HeroLevel(this, data.getLevelData()));
    }

    @Override
    public void onLevelUp(Level<Hero> level) {
        //TODO: override
    }

    @Override
    public void onLevelDown(Level<Hero> level) {
        //TODO: override
    }
}
