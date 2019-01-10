package de.raidcraft.skills.actionapi.actions;

import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.skills.CharacterManager;
import de.raidcraft.skills.SkillManager;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.util.ConfigUtil;
import de.raidcraft.util.UUIDUtil;
import io.ebeaninternal.server.el.CharMatch;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class AddSkillAction implements Action<Player> {

    @Information(
            value = "skill.add",
            desc = "Adds the given skill to the player.",
            conf = {
                    "skill: the skill to add"
            }
    )
    @Override
    public void accept(Player player, ConfigurationSection config) {

        try {
            Hero hero = RaidCraft.getComponent(CharacterManager.class).getHero(player);
            Skill skill = RaidCraft.getComponent(SkillManager.class).getSkill(hero, hero.getVirtualProfession(), config.getString("skill"));
            if (skill.isUnlocked()) {
                player.sendMessage(ChatColor.GREEN + "Du hast den Skill " + skill.getFriendlyName() + " bereits.");
            } else {
                hero.addSkill(skill);
            }
        } catch (UnknownSkillException e) {
            player.sendMessage(ChatColor.RED + e.getMessage());
            RaidCraft.LOGGER.warning("Failed to execute !skill.add action inside " + ConfigUtil.getFileName(config) + ": " + e.getMessage());
        }
    }
}
