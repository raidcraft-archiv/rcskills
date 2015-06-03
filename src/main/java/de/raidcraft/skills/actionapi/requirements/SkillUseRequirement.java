package de.raidcraft.skills.actionapi.requirements;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.skills.CharacterManager;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.Action;
import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
public class SkillUseRequirement implements Requirement<Player> {

    @Override
    @Information(
            value = "skill.use",
            desc = "Checks if the last action of the player was a SkillAction.class",
            conf = {
                    "skill: <identifier>"
            }
    )
    public boolean test(Player player, ConfigurationSection config) {

        Hero hero = RaidCraft.getComponent(CharacterManager.class).getHero(player);
        if (hero == null) return false;
        Action<? extends CharacterTemplate> lastAction = hero.getLastAction();
        return lastAction instanceof SkillAction
                && ((SkillAction) lastAction).getSkill().getName().equalsIgnoreCase(config.getString("skill"));
    }
}
