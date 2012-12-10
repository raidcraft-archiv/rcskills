package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

    @Command(
            aliases = "debug",
            desc = "Sends a lot of debug output to the player"
    )
    @CommandPermissions("rcskills.admin.debug")
    public void debug(CommandContext args, CommandSender sender) throws CommandException {

        Hero hero;
        if (args.argsLength() > 1) {
            try {
                hero = plugin.getHeroManager().getHero(args.getString(0));
            } catch (UnknownPlayerException e) {
                throw new CommandException(e.getMessage());
            }
        } else {
            hero = plugin.getHeroManager().getHero((Player) sender);
        }
        hero.setDebugging(!hero.isDebugging());
        sender.sendMessage("" + ChatColor.RED + ChatColor.ITALIC + "Toggled debug mode: " + ChatColor.AQUA +
                (hero.isDebugging() ? "on." : "off."));
    }
}
