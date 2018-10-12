package de.raidcraft.skills.actionapi.actions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.skills.SkillManager;
import de.raidcraft.skills.api.exceptions.CombatException;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@Data
public class CastSkillAction implements Action<Player> {

    private SkillManager _skillManager;
    private SkillManager getSkillManager() {
        if (_skillManager == null) {
            _skillManager = RaidCraft.getComponent(SkillManager.class);
        }
        return _skillManager;
    }

    @Information(
            value = "cast.override",
            desc = "Casts the given skill in the context of the player. The player does not need to own the skill.",
            conf = {
                    "skill: to cast",
                    "any additional config overrides for the skill"
            }
    )
    @Override
    public void accept(Player player, ConfigurationSection config) {

        getSkillManager().getSkill(player, config.getString("skill"), config).ifPresent(skill -> {
            skill.use(true);
        });
    }
}
