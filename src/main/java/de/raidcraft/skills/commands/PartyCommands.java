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
        if (owner.equals(hero)) {
            throw new CommandException("Du kannst dich nicht selbst in eine Gruppe einladen.");
        }
        if (!owner.getParty().getOwner().equals(owner)) {
            throw new CommandException("Nur der Gruppenleiter (" + hero.getParty().getOwner() + ") kann neue Spieler einladen.");
        }
        if (hero.getParty().getHeroes().size() > 1) {
            throw new CommandException(hero.getName() + " befindet sich bereits in einer Gruppe.");
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
            aliases = {"leave"},
            desc = "Leaves the current party."
    )
    @CommandPermissions("rcskills.party.leave")
    public void leave(CommandContext args, CommandSender sender) throws CommandException {

        Hero hero = plugin.getCharacterManager().getHero((Player) sender);
        Party party = hero.getParty();
        if (party.getHeroes().size() <= 1) {
            throw new CommandException("Du bist in keiner Gruppe.");
        }
        hero.leaveParty();
        sender.sendMessage(ChatColor.YELLOW + "Du hast die Gruppe von " + party.getOwner().getName() + " verlassen.");
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
            if (owner.getParty().getHeroes().size() <= 1) {
                throw new CommandException("Du bist in keiner Gruppe.");
            }
            if (hero.equals(owner)) {
                throw new CommandException("Du kannst dich nicht selbst aus der Gruppe kicken.");
            }
            if (!owner.getParty().getOwner().equals(owner)) {
                throw new CommandException("Nur der Gruppenleiter kann Spieler aus der Gruppe kicken.");
            }
            if (!owner.getParty().isInGroup(hero)) {
                throw new CommandException("Der Spieler " + hero.getName() + " ist nicht in deiner Gruppe.");
            }
            owner.getParty().kickMember(hero);
        } catch (UnknownPlayerException e) {
            throw new CommandException(e.getMessage());
        }
    }

    @Command(
            aliases = {"dispand", "auflösen"},
            desc = "Dispands the party, removing all members."
    )
    @CommandPermissions("rcskills.party.kick")
    public void dispand(CommandContext args, CommandSender sender) throws CommandException {

        Hero owner = plugin.getCharacterManager().getHero((Player) sender);
        if (owner.getParty().getHeroes().size() <= 1) {
            throw new CommandException("Du bist in keiner Gruppe.");
        }
        if (!owner.getParty().getOwner().equals(owner)) {
            throw new CommandException("Nur der Gruppenleiter kann die Gruppe auflösen.");
        }
        owner.getParty().dispandParty();
    }

    @Command(
            aliases = {"list", "l"},
            desc = "Lists all party members."
    )
    @CommandPermissions("rcskills.party.list")
    public void list(CommandContext args, CommandSender sender) throws CommandException {

        Party party = plugin.getCharacterManager().getHero((Player) sender).getParty();
        if (party.getHeroes().size() <= 1) {
            throw new CommandException("Du bist in keiner Gruppe.");
        }
        sender.sendMessage(ChatColor.YELLOW + "Gruppenmitglieder: " + StringUtil.joinString(party.getHeroes(), ", ", 0));
    }
}
