package de.raidcraft.skills.api.requirement;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.level.Levelable;

/**
 * @author Silthus
 */
public abstract class LevelRequirement<T extends Levelable<T>> extends AbstractRequirement<Level<T>> {

    private final int requiredLevel;

    public LevelRequirement(Level<T> type, int requiredLevel) {

        super(type);
        this.requiredLevel = requiredLevel;
    }

    public LevelRequirement(Level<T> type) {

        // needs to be mastered
        this(type, type.getMaxLevel());
    }

    public T getLevelObject() {

        return getType().getLevelObject();
    }

    public int getRequiredLevel() {

        return requiredLevel;
    }

    @Override
    public boolean isMet(Hero hero) {

        // we assume that the hero already matches
        return requiredLevel <= getType().getLevel();
    }
}
