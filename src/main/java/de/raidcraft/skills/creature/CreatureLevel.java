package de.raidcraft.skills.creature;

import de.raidcraft.skills.api.level.AbstractLevel;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.formulas.FormulaType;

/**
 * @author Silthus
 */
public class CreatureLevel<T extends Levelable<T>> extends AbstractLevel<T> {

    private int level;

    public CreatureLevel(T levelObject, int level) {

        super(levelObject, FormulaType.STATIC.create(null));
        this.level = level;
    }

    @Override
    public int getLevel() {

        return level;
    }

    @Override
    public int getMaxLevel() {

        return level;
    }

    @Override
    public int getExp() {

        return 0;
    }

    @Override
    public int getMaxExp() {

        return 0;
    }

    @Override
    public void addExp(int exp) {

    }

    @Override
    public void removeExp(int exp) {

    }

    @Override
    public void setExp(int exp) {

    }

    @Override
    public void setLevel(int level) {

        this.level = level;
    }

    @Override
    public void addLevel(int level) {

        this.level += level;
    }

    @Override
    public void removeLevel(int level) {

        this.level -= level;
    }

    @Override
    public boolean canLevel() {

        return false;
    }

    @Override
    public boolean hasReachedMaxLevel() {

        return true;
    }
}
