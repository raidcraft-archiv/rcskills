package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.skills.SkillsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author Silthus
 */
public class AdminCommands {

    private final SkillsPlugin plugin;

    public AdminCommands(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = "reload",
            desc = "Reloads the Skills plugin"
    )
    @CommandPermissions("rcskills.admin.reload")
    public void reload(CommandContext args, CommandSender sender) {

        plugin.reload();
        sender.sendMessage(ChatColor.GREEN + "Reloaded all " + ChatColor.RED + "Config Files " + ChatColor.GREEN + "and " +
                 ChatColor.RED + "Managers " + ChatColor.GREEN + "successfully!");
    }
}
