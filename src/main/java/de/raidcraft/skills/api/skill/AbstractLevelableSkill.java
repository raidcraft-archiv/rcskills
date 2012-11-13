package de.raidcraft.skills.api.skill;

import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.LevelableSkill;
import de.raidcraft.skills.api.events.SkillLevelEvent;
import de.raidcraft.skills.tables.PlayerSkillsLevelTable;
import de.raidcraft.util.BukkitUtil;

/**
 * @author Silthus
 */
public abstract class AbstractLevelableSkill extends AbstractSkill implements LevelableSkill {

    private final RCPlayer player;
    private int level = 1;
    private int maxLevel = 10;
    private int exp = 0;
    private int maxExp;

    public AbstractLevelableSkill(int id, RCPlayer player) {

        super(id);
        this.player = player;
        load();
    }

    private void load() {

        PlayerSkillsLevelTable.Data data = Database.getTable(PlayerSkillsLevelTable.class).getLevelData(getId(), player);
        // abort in case there are no entries yet
        if (data == null) {
            maxExp = calculateMaxExp();
            return;
        }
        this.level = data.level;
        this.maxLevel = data.maxLevel;
        this.exp = data.exp;
        this.maxExp = data.maxExp;
    }

    @Override
    public RCPlayer getPlayer() {

        return player;
    }

    @Override
    public int getLevel() {

        return this.level;
    }

    @Override
    public int getMaxLevel() {

        return this.maxLevel;
    }

    @Override
    public int getExp() {

        return this.exp;
    }

    @Override
    public int getMaxExp() {

        return this.maxExp;
    }

    @Override
    public int calculateMaxExp() {
        // TODO: calculate formula for next exp max level
        maxExp = (int) (maxExp * 1.5);
        return maxExp;
    }

    @Override
    public int getExpToNextLevel() {

        return this.maxExp - this.exp;
    }

    @Override
    public void addExp(int exp) {

        this.exp += exp;
        checkProgress();
    }

    @Override
    public void removeExp(int exp) {

        this.exp -= exp;
        checkProgress();
    }

    @Override
    public void setExp(int exp) {

        this.exp = exp;
        checkProgress();
    }

    @Override
    public void setLevel(int level) {

        if (level < this.level) {
            do {
                removeLevel(1);
            } while (getLevel() > level);
        } else if (level > this.level) {
            do {
                addLevel(1);
            } while (getLevel() < level);
        }
    }

    @Override
    public void addLevel(int level) {

        SkillLevelEvent event = new SkillLevelEvent(this, getLevel() + 1);
        BukkitUtil.callEvent(event);
        if (!event.isCancelled()) {
            increaseLevel();
            this.level += level;
            // set the exp
            setExp(getExp() - getMaxExp());
            calculateMaxExp();
            save();
        }
    }

    @Override
    public void removeLevel(int level) {

        SkillLevelEvent event = new SkillLevelEvent(this, getLevel() - 1);
        BukkitUtil.callEvent(event);
        if (!event.isCancelled()) {
            decreaseLevel();
            this.level -= level;
            calculateMaxExp();
            save();
        }
    }

    @Override
    public boolean canLevel() {

        return getExpToNextLevel() < 1 && !hasReachedMaxLevel();
    }

    @Override
    public boolean hasReachedMaxLevel() {

        return !(getLevel() < getMaxLevel());
    }

    @Override
    public void save() {

        Database.getTable(PlayerSkillsLevelTable.class).saveSkillLevel(this);
    }

    private void checkProgress() {

        if (canLevel()) {
            // increase the level
            addLevel(1);
        } else if (getExp() < 0 && getLevel() > 0) {
            // decrease the level...
            removeLevel(1);
            // our exp are negative when we get reduced
            setExp(getMaxExp() + getExp());
        }
    }

    @Override
    public void increaseLevel() {
        // override if needed
    }

    @Override
    public void decreaseLevel() {
        // override if needed
    }
}
