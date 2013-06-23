package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.util.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class PvPCommands {

    private final SkillsPlugin plugin;

    public PvPCommands(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"status", "info"},
            desc = "Checks the pvp status of the player"
    )
    @CommandPermissions("rcskills.player.pvp")
    public void pvpStatus(CommandContext args, CommandSender sender) {

        Hero hero = plugin.getCharacterManager().getHero((Player) sender);
        if (plugin.getCharacterManager().isPvPToggleQueued(hero)) {
            plugin.getCharacterManager().removeQueuedPvPToggle(hero);
            sender.sendMessage(ChatColor.YELLOW + "Das umschalten deines PvP Statuses auf "
                    + (!hero.isPvPEnabled() ? ChatColor.RED + "ein" : ChatColor.GREEN + "aus")
                    + ChatColor.YELLOW + " wurde erfolgreich abgebrochen.");
            return;
        }

        String time = TimeUtil.getFormattedTime(plugin.getCommonConfig().pvp_toggle_delay);
        plugin.getCharacterManager().queuePvPToggle(hero, !hero.isPvPEnabled());
        sender.sendMessage((!hero.isPvPEnabled() ? ChatColor.RED : ChatColor.AQUA) + "Dein PvP wird in " + time
                + (!hero.isPvPEnabled() ? "eingeschaltet." : "ausgeschaltet."));
    }
}
