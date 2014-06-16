package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.CommandTriggered;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Will execute the command to bind or unbind a skill with an item.
 */
public class BindCommand {

    private final SkillsPlugin plugin;

    public BindCommand(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    /**
     * Executes the command to bind or unbind a skill with an item.
     *
     * @param args   Passed command arguments
     * @param sender The source of the command
     *
     * @return true if success, otherwise false
     */
    @Command(
            aliases = {"bind", "unbind"},
            desc = "Bindet eine Fähigkeit an ein Gegenstand",
            usage = "[skill] Der Name der Fähigkeit die du binden möchtest.",
            min = 0, max = 2
    )
    @CommandPermissions("rcskills.player.bind")
    public void onCommand(CommandContext args, CommandSender sender) throws CommandException {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl nutzen.");
            return;
        }

        Player player = (Player) sender;

        if (!player.isOnline()) {
            return;
        }

        Hero hero = plugin.getCharacterManager().getHero(player);
        Material material = player.getItemInHand().getType();
        Skill skill;

        if (args.argsLength() > 0) {

            try {

                skill = hero.getSkill(args.getString(0));

            } catch (UnknownSkillException e) {

                throw new CommandException(ChatColor.YELLOW + "Du besitzt diese Fähigkeit nicht.");
            }
            if (hero.getPlayer().getItemInHand() == null || material == null || material.equals(Material.AIR) || material.isBlock()) {

                throw new CommandException(ChatColor.YELLOW + "Du musst einen Gegenstand in der Hand halten.");
            }
            if (!(skill instanceof CommandTriggered) || !skill.getSkillProperties().isCastable()) {

                throw new CommandException(ChatColor.YELLOW + "Du kannst diese Fähigkeit nicht an einen Gegenstand binden.");
            }
            if (hero.getBindings().add(material, skill, args.argsLength() > 1 ? args.getString(1) : null)) {

                player.sendMessage(ChatColor.DARK_GREEN + "Die Fähigkeit wurde erfolgreich an den Gegenstand gebunden.");
                return;
            } else {

                throw new CommandException(ChatColor.YELLOW + "Die Fähigkeit konnte nicht an den Gegenstand gebunden werden.");
            }
        }

        if (hero.getBindings().remove(material)) {

            player.sendMessage(ChatColor.DARK_GREEN + "Alle Fähigkeiten wurden erfolgreich von den Gegenstand entbunden.");
            return;
        } else {

            player.sendMessage(ChatColor.DARK_GREEN + "Auf dem Gegenstand gibt es keine gebundene Fähigkeit.");
            player.sendMessage(ChatColor.YELLOW + "Gebe eine Fähigkeit an wenn du eine an den Gegenstand binden möchtest.");
            return;
        }
    }
}
