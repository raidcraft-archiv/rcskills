package de.raidcraft.skills;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.PlayerComponent;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.Levelable;
import de.raidcraft.skills.api.Skill;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.tables.PlayerSkillsTable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public class SkilledPlayer implements PlayerComponent {

    private final RCPlayer player;
    private final Map<Integer, Skill> skills = new HashMap<>();

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

        for (Skill skill : getSkills()) {
            if (skill instanceof Levelable) {
                ((Levelable) skill).save();
            }
        }
    }
}
