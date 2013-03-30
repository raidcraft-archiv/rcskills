package de.raidcraft.skills.api.level;

import de.raidcraft.skills.api.level.forumla.LevelFormula;
import de.raidcraft.skills.api.persistance.LevelData;

/**
 * @author Silthus
 */
public class ConfigurableAttachedLevel<T extends Levelable<T>> extends AbstractAttachedLevel<T> {

    public ConfigurableAttachedLevel(T levelObject, LevelFormula formula, LevelData data) {

        super(levelObject, formula, data);
    }
}
