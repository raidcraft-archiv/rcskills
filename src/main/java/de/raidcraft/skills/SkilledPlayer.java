package de.raidcraft.skills;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.PlayerComponent;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.AbstractLevelable;
import de.raidcraft.skills.api.Levelable;
import de.raidcraft.skills.api.Obtainable;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.tables.skills.PlayerSkillsTable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public class SkilledPlayer extends AbstractLevelable implements PlayerComponent, Levelable {

    private final RCPlayer player;
    private final Map<Integer, Skill> skills = new HashMap<>();

    public SkilledPlayer(RCPlayer player) {

        this.player = player;
    }

    @Override
    protected void loadLevel() {

        //TODO: implement
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

    public boolean canOptainSkill(Skill skill) {

        if (skill instanceof Obtainable) {
            Obtainable obtainable = (Obtainable) skill;

            switch (obtainable.getType()) {

                case ADMIN:
                    return player.isOp() || player.hasPermission("rcskills.admin");
                case BUYABLE:
                    return obtainable.hasBuyPermission(player)
                            && obtainable.getNeededLevel() < getLevel()
                            && player.hasEnoughMoney(obtainable.getCost())
                            && meetsProfessionRequirements(skill, obtainable);
                case GAINABLE:
                    return obtainable.hasGainPermission(player)
                            && obtainable.getNeededLevel() < getLevel()
                            && meetsProfessionRequirements(skill, obtainable);
            }
        }
        return false;
    }

    private boolean meetsProfessionRequirements(Skill skill, Obtainable obtainable) {

        if (obtainable.getNeededProfessions().size() < 1) {
            return true;
        }

        boolean needAll = obtainable.areAllProfessionsRequired();
        for (Profession profession : obtainable.getNeededProfessions()) {
            if (needAll && !profession.canPlayerObtainSkill(skill)) {
                return false;
            } else if (!needAll && profession.canPlayerObtainSkill(skill)) {
                return true;
            }
        }
        return false;
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

    public void saveSkills() {

        for (Skill skill : getSkills()) {
            if (skill instanceof Levelable) {
                ((Levelable) skill).saveLevelProgress();
            }
        }
    }

    @Override
    public void saveLevelProgress() {

        // TODO: saveLevelProgress level progress
    }

    @Override
    public void increaseLevel() {
        // called after the player leveled
    }

    @Override
    public void decreaseLevel() {
        // called after the player lost a level
    }
}
