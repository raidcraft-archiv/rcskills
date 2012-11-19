package de.raidcraft.skills;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.PlayerComponent;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.api.Levelable;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.AbstractHero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.tables.PlayerTable;
import de.raidcraft.skills.tables.skills.PlayerSkillsTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public class RCHero extends AbstractHero implements PlayerComponent {

    private final RCPlayer player;
    private final Map<Integer, Skill> skills = new HashMap<>();
    private final Collection<PlayerProfession> playerProfessions = new ArrayList<>();

    public RCHero(RCPlayer player) throws UnknownPlayerException {

        super(player, Database.getTable(PlayerTable.class).getLevelData(player.getUserName()));
        this.player = player;
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

    public Collection<PlayerProfession> getPlayerProfessions() {

        return playerProfessions;
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
