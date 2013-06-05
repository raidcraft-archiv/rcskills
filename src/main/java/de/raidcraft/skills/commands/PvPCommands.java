package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
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
        sender.sendMessage((hero.isPvPEnabled() ? ChatColor.RED : ChatColor.AQUA) + "Dein PvP ist "
                + (hero.isPvPEnabled() ? "eingeschaltet." : "ausgeschaltet."));
    }
}
