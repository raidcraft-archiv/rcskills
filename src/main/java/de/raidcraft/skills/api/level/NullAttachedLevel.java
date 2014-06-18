package de.raidcraft.skills.api.level;

import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.formulas.FormulaType;

/**
 * Represents an empty level object that blocks all level interaction.
 *
 * @author Silthus
 */
public final class NullAttachedLevel extends AbstractAttachedLevel<Profession> {

    public NullAttachedLevel(Profession levelObject, LevelData data) {

        super(levelObject, FormulaType.STATIC.create(null), data);
    }

    @Override
    public int getLevel() {

        return 1;
    }

    @Override
    public void setLevel(int level) {

    }

    @Override
    public int getMaxLevel() {

        return 1;
    }

    @Override
    public int getExp() {

        return 0;
    }

    @Override
    public void setExp(int exp) {

    }

    @Override
    public int getMaxExp() {

        return 0;
    }

    @Override
    public void calculateMaxExp() {

    }

    @Override
    public int getExpToNextLevel() {

        return 0;
    }

    @Override
    public void addExp(int exp) {

    }

    @Override
    public void removeExp(int exp) {

    }

    @Override
    public void addLevel(int level) {

    }

    @Override
    public void removeLevel(int level) {

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
