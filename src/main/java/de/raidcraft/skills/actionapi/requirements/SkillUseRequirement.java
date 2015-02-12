package de.raidcraft.skills.actionapi.requirements;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.skills.CharacterManager;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.Action;
import de.raidcraft.skills.api.combat.action.SkillAction;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
public class SkillUseRequirement implements Requirement<Player> {

    @Override
    public boolean test(Player player, ConfigurationSection config) {

        Action<? extends CharacterTemplate> lastAction = RaidCraft.getComponent(CharacterManager.class).getHero(player).getLastAction();
        return lastAction instanceof SkillAction
                && ((SkillAction) lastAction).getSkill().getName().equalsIgnoreCase(config.getString("skill"));
    }
}
