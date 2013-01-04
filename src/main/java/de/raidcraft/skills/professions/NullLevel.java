package de.raidcraft.skills.professions;

import de.raidcraft.skills.api.level.AbstractLevel;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.api.profession.Profession;

/**
 * Represents an empty level object that blocks all level interaction.
 *
 * @author Silthus
 */
public final class NullLevel extends AbstractLevel<Profession> {

    public NullLevel(Profession levelObject, LevelData data) {

        super(levelObject, data);
    }

    @Override
    public int getLevel() {

        return 1;
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
    public void setExp(int exp) {

    }

    @Override
    public void setLevel(int level) {

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

    @Override
    public int getNeededExpForLevel(int level) {

        return 1;
    }
}
