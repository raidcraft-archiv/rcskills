package de.raidcraft.skills.commands;

import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.CommandTriggered;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Will execute the command to bind or unbind a skill with an item.
 */
public class BindCommand implements CommandExecutor {

    private final SkillsPlugin plugin;

    public BindCommand(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    /**
     * Executes the command to bind or unbind a skill with an item.
     *
     * @param sender The source of the command
     * @param cmd    The command which was executed
     * @param label  The alias of the command which was used
     * @param args   Passed command arguments
     *
     * @return true if success, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler können dieses Befehl nutzen.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.isOnline()) {
            return true;
        }

        Hero hero = plugin.getCharacterManager().getHero(player);
        Material material = player.getItemInHand().getType();
        Skill skill;
        String arg = "";


        if (args.length > 0) {

            try {

                skill = hero.getSkill(args[0]);

            } catch (UnknownSkillException e) {

                player.sendMessage(ChatColor.YELLOW + "Du besitzt diese Fähigkeit nicht.");
                return true;
            }
            if (hero.getPlayer().getItemInHand() == null || material == null || material.equals(Material.AIR) || material.isBlock()) {

                player.sendMessage(ChatColor.YELLOW + "Du musst einen Gegenstand in der Hand halten.");
                return true;
            }
            if (!(skill instanceof CommandTriggered) || !skill.getSkillProperties().isCastable()) {

                player.sendMessage(ChatColor.RED + "Du kannst diese Fähigkeit nicht an ein Gegenstand binden.");
                return true;
            }
            if (args.length > 1) {

                arg = args[1];
            }
            if (hero.getBindings().add(material, skill, arg)) {

                player.sendMessage(ChatColor.DARK_GREEN + "Die Fähigkeit wurde erfolgreich an den Gegenstand gebunden.");
                return true;
            } else {

                player.sendMessage(ChatColor.YELLOW + "Die Fähigkeit konnte nicht an den Gegenstand gebunden werden.");
                return true;
            }
        }

        if (hero.getBindings().remove(material)) {

            player.sendMessage(ChatColor.DARK_GREEN + "Alle Fähigkeiten wurden erfolgreich von den Gegenstand entbunden.");
            return true;
        } else {

            player.sendMessage(ChatColor.DARK_GREEN + "Auf dem Gegenstand gibt es keine gebundene Fähigkeit.");
            player.sendMessage(ChatColor.YELLOW + "Gebe eine Fähigkeit an wenn du eine an den Gegenstand binden möchtest.");
            return false;
        }
    }
}
