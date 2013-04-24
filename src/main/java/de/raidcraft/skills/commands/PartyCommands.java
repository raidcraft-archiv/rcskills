package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.util.StringUtil;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class PartyCommands {

    private final SkillsPlugin plugin;

    public PartyCommands(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"invite", "i"},
            desc = "Invites the given member to the party.",
            min = 1
    )
    @CommandPermissions("rcskills.party.invite")
    public void invite(CommandContext args, CommandSender sender) throws CommandException {

        Player player = Bukkit.getPlayer(args.getString(0));
        if (player == null) {
            throw new CommandException("Es ist kein Spieler mit dem Namen " + args.getString(0) + " online.");
        }
        Hero hero = plugin.getCharacterManager().getHero(player);
        Hero owner = plugin.getCharacterManager().getHero((Player) sender);
        if (!owner.getParty().getOwner().equals(owner)) {
            throw new CommandException("Nur der Gruppenleiter (" + hero.getParty().getOwner() + ") kann neue Spieler einladen.");
        }
        if (hero.getPendingPartyInvite() != null) {
            throw new CommandException(hero.getName() + " wurde bereits in eine Gruppe eingeladen.");
        }
        owner.getParty().inviteMember(hero);
        sender.sendMessage(ChatColor.YELLOW + "Du hast " + hero.getName() + " in deine Gruppe eingeladen.");
    }

    @Command(
            aliases = {"accept", "a"},
            desc = "Accepts an open group invite."
    )
    @CommandPermissions("rcskills.party.accept")
    public void accept(CommandContext args, CommandSender sender) throws CommandException {

        Hero hero = plugin.getCharacterManager().getHero((Player) sender);
        Party party = hero.getPendingPartyInvite();
        if (party == null) {
            throw new CommandException("Du hast keine offene Gruppeneinladung.");
        }
        party.sendMessage(ChatColor.YELLOW + hero.getName() + " ist der Gruppe beigetreten.");
        sender.sendMessage(ChatColor.YELLOW + "Du bist der Gruppe von " + party.getOwner().getName() + " beigetreten.");
        sender.sendMessage(ChatColor.YELLOW + "Mitglieder: " + StringUtil.joinString(party.getHeroes(), ", ", 0));
        party.addMember(hero);
    }

    @Command(
            aliases = {"deny", "d"},
            desc = "Denies an open party request."
    )
    @CommandPermissions("rcskills.party.deny")
    public void deny(CommandContext args, CommandSender sender) throws CommandException {

        Hero hero = plugin.getCharacterManager().getHero((Player) sender);
        Party party = hero.getPendingPartyInvite();
        if (party == null) {
            throw new CommandException("Du hast keine offene Gruppeneinladung.");
        }
        party.removeMember(hero);
        sender.sendMessage(ChatColor.RED + "Du hast die Gruppeneinladung von " + party.getOwner().getName() + " abgelehnt.");
    }

    @Command(
            aliases = {"kick", "k"},
            desc = "Kicks the given member from the group.",
            min = 1
    )
    @CommandPermissions("rcskills.party.kick")
    public void kick(CommandContext args, CommandSender sender) throws CommandException {

        try {
            Hero hero = plugin.getCharacterManager().getHero(args.getString(0));
            Hero owner = plugin.getCharacterManager().getHero((Player) sender);
            if (!owner.getParty().isInGroup(hero)) {
                throw new CommandException("Der Spieler " + hero.getName() + " ist nicht in deiner Gruppe.");
            }
            owner.getParty().kickMember(hero);
        } catch (UnknownPlayerException e) {
            throw new CommandException(e.getMessage());
        }
    }

    @Command(
            aliases = {"list", "l"},
            desc = "Lists all party members."
    )
    @CommandPermissions("rcskills.party.list")
    public void list(CommandContext args, CommandSender sender) {

        Party party = plugin.getCharacterManager().getHero((Player) sender).getParty();
        sender.sendMessage(ChatColor.YELLOW + "Gruppenmitglieder: " + StringUtil.joinString(party.getHeroes(), ", ", 0));
    }
}
