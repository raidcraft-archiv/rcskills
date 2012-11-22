package de.raidcraft.skills.hero;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.AbstractLevel;
import de.raidcraft.skills.api.persistance.LevelData;

/**
 * @author Silthus
 */
public class HeroLevel extends AbstractLevel<Hero> {

    public HeroLevel(Hero levelObject, LevelData data) {

        super(levelObject, data);
    }
}
