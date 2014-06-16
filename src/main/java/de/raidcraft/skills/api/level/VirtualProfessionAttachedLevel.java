package de.raidcraft.skills.api.level;

import de.raidcraft.skills.api.level.forumla.LevelFormula;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.professions.VirtualProfession;

/**
 * @author Silthus
 */
public final class VirtualProfessionAttachedLevel extends AbstractAttachedLevel<Profession> {

    public VirtualProfessionAttachedLevel(VirtualProfession levelObject, LevelData data) {

        super(levelObject, null, data);
    }

    @Override
    public LevelFormula getFormula() {

        return new LevelFormula() {
            @Override
            public int getNeededExpForLevel(int level) {

                return 1;
            }
        };
    }

    @Override
    public int getExp() {

        return 0;
    }

    @Override
    public void setExp(int exp) {

        setLevel(exp);
    }

    @Override
    public int getMaxExp() {

        return 1;
    }

    @Override
    public void calculateMaxExp() {

    }

    @Override
    public int getExpToNextLevel() {

        return 1;
    }

    @Override
    public void addExp(int exp) {

        addLevel(exp);
    }

    @Override
    public void removeExp(int exp) {

        removeLevel(exp);
    }

    @Override
    public boolean canLevel() {

        return true;
    }
}
