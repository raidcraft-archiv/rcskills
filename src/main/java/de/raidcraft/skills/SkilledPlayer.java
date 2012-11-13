package de.raidcraft.skills;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.PlayerComponent;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.Levelable;
import de.raidcraft.skills.api.events.PlayerLevelEvent;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.tables.PlayerSkillsTable;
import de.raidcraft.util.BukkitUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public class SkilledPlayer implements PlayerComponent, Levelable {

    private final RCPlayer player;
    private final Map<Integer, Skill> skills = new HashMap<>();
    /* Variables for the Levelable Implementation */
    private int exp;
    private int level;
    private int maxLevel;
    private int maxExp;

    public SkilledPlayer(RCPlayer player) {

        this.player = player;
    }

    @Override
    public RCPlayer getPlayer() {

        return player;
    }

    public boolean hasSkill(int id) {

        if (player.isOnline()) {
            return skills.containsKey(id);
        } else {
            return Database.getTable(PlayerSkillsTable.class).contains(id, player);
        }
    }

    public boolean hasSkill(Skill skill) {

        return hasSkill(skill.getId());
    }

    public Skill getSkill(int id) throws UnknownSkillException {

        Skill skill;
        if (skills.containsKey(id)) {
            skill = skills.get(id);
        } else {
            skill = RaidCraft.getComponent(SkillsComponent.class).getSkillManager().getPlayerSkill(id, player);
            skills.put(skill.getId(), skill);
        }
        return skill;
    }

    public Collection<Skill> getSkills() {

        return skills.values();
    }

    public void save() {

        saveLevelProgress();
        saveSkills();
    }

    private void saveSkills() {

        for (Skill skill : getSkills()) {
            if (skill instanceof Levelable) {
                ((Levelable) skill).save();
            }
        }
    }

    /*/////////////////////////////////////////////////////////////////
    //          Implementation of the Levelable Interface
    /////////////////////////////////////////////////////////////////*/

    @Override
    public int getLevel() {

        return level;
    }

    @Override
    public int getMaxLevel() {

        return maxLevel;
    }

    @Override
    public int getExp() {

        return exp;
    }

    @Override
    public int getMaxExp() {

        return maxExp;
    }

    @Override
    public int calculateMaxExp() {
        // TODO: calculate formula for next exp max level
        maxExp = (int) (maxExp * 1.5);
        return maxExp;
    }

    @Override
    public int getExpToNextLevel() {

        return maxExp - exp;
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

        PlayerLevelEvent event = new PlayerLevelEvent(this, getLevel() + 1);
        BukkitUtil.callEvent(event);
        if (!event.isCancelled()) {
            increaseLevel();
            this.level += level;
            // set the exp
            setExp(getExp() - getMaxExp());
            calculateMaxExp();
            saveSkills();
        }
    }

    @Override
    public void removeLevel(int level) {

        PlayerLevelEvent event = new PlayerLevelEvent(this, getLevel() - 1);
        BukkitUtil.callEvent(event);
        if (!event.isCancelled()) {
            decreaseLevel();
            this.level -= level;
            calculateMaxExp();
            saveSkills();
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
    public void increaseLevel() {
        // called after the player leveled
    }

    @Override
    public void decreaseLevel() {
        // called after the player lost a level
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

    private void saveLevelProgress() {

        // TODO: save level progress
    }
}
