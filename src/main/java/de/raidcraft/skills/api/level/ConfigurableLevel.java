package de.raidcraft.skills.api.level;

import de.raidcraft.skills.api.level.forumla.LevelFormula;
import de.raidcraft.skills.api.persistance.LevelData;

/**
 * @author Silthus
 */
public class ConfigurableLevel<T extends Levelable<T>> extends AbstractLevel<T> {

    public ConfigurableLevel(T levelObject, LevelFormula formula, LevelData data) {

        super(levelObject, formula, data);
    }
}
