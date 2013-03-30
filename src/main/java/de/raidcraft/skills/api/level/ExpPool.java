package de.raidcraft.skills.api.level;

import com.avaje.ebean.Ebean;
import de.raidcraft.api.database.Database;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.formulas.FormulaType;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.tables.THeroExpPool;

/**
 * @author Silthus
 */
public class ExpPool extends AbstractAttachedLevel<Hero> {

    public ExpPool(Hero levelObject, LevelData data) {

        super(levelObject, FormulaType.STATIC.create(null), data);
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
    public int getMaxExp() {

        return 999999999;
    }

    @Override
    public void calculateMaxExp() {

    }

    @Override
    public int getExpToNextLevel() {

        return 1;
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
    public void saveLevelProgress() {

        THeroExpPool db = Ebean.find(THeroExpPool.class).where().eq("player", getLevelObject().getName()).findUnique();
        db.setExp(getExp());
        Database.save(db);
    }
}
