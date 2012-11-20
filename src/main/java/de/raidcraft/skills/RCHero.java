package de.raidcraft.skills;

import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.PlayerComponent;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.api.hero.AbstractHero;
import de.raidcraft.skills.api.persistance.HeroData;
import de.raidcraft.skills.tables.PlayerTable;

/**
 * @author Silthus
 */
public class RCHero extends AbstractHero implements PlayerComponent {

    public RCHero(HeroData data) throws UnknownPlayerException {

        super(data, Database.getTable(PlayerTable.class).getLevelData(data.getPlayer().getUserName()));
    }

    @Override
    public void saveLevelProgress() {

        // TODO: saveLevelProgress level progress
    }

    @Override
    public void increaseLevel() {
        // called after the player leveled
    }

    @Override
    public void decreaseLevel() {
        // called after the player lost a level
    }
}
