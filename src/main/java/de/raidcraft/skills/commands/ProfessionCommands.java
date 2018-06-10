package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.commands.QueuedCommand;
import de.raidcraft.api.economy.BalanceSource;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.util.ProfessionUtil;
import de.raidcraft.util.FancyPaginatedResult;
import de.raidcraft.util.UUIDUtil;
import de.raidcraft.util.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Silthus
 */
public class ProfessionCommands {

    private final SkillsPlugin plugin;

    public ProfessionCommands(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"list", "l"},
            desc = "Lists professions",
            flags = "p:a"
    )
    @CommandPermissions("rcskills.player.profession.list")
    public void list(final CommandContext args, CommandSender sender) throws CommandException {

        if (!(sender instanceof Player)) {
            return;
        }

        final Hero hero = plugin.getCharacterManager().getHero((Player) sender);
        List<Profession> professions;
        professions = plugin.getProfessionManager().getAllProfessions(hero);

        if (professions == null || professions.size() < 1) {
            throw new CommandException("Es wurden noch keine Berufe oder Klassen konfiguriert.");
        }

        if (!args.hasFlag('a')) {
            List<Profession> temp = new ArrayList<>(professions);
            temp.stream()
                    .filter(profession -> !profession.isMeetingAllRequirements(hero.getPlayer()))
                    .forEach(professions::remove);
        }

        if (!args.hasFlag('v')) {
            professions.remove(plugin.getProfessionManager().getVirtualProfession(hero));
        }

        Collections.sort(professions);

        new FancyPaginatedResult<Profession>("Tag   -   Spezialisierung   -   Pfad   -   Level") {

            @Override
            public FancyMessage format(Profession profession) {

                return new FancyMessage("[").color(ChatColor.YELLOW)
                        .then(profession.getProperties().getTag()).color(ChatColor.AQUA)
                        .then("]").color(ChatColor.YELLOW)
                        .then(" ").then(profession.getFriendlyName()).color(profession.isActive() ? ChatColor.GREEN : ChatColor.DARK_RED)
                        .formattedTooltip(ProfessionUtil.getProfessionTooltip(profession, true))
                        .then(" - ").color(ChatColor.YELLOW)
                        .then(profession.getPath().getFriendlyName()).color(ChatColor.GRAY).style(ChatColor.ITALIC)
                        .then(" - ").color(ChatColor.YELLOW)
                        .then(profession.getAttachedLevel().getLevel() + "").color(profession.getAttachedLevel().getLevel() > 0 ? ChatColor.GREEN : ChatColor.DARK_RED);
            }
        }.display(sender, professions, args.getFlagInteger('p', 1));
    }

    @Command(
            aliases = {"choose", "c"},
            desc = "Wählt die gewünschte Spezialisierung.",
            min = 1,
            flags = "f"
    )
    @CommandPermissions("rcskills.player.profession.choose")
    public void choose(CommandContext args, CommandSender sender) throws CommandException {

        if (!(sender instanceof Player)) throw new CommandException("...");


        boolean force = args.hasFlag('f');
        if(force && !sender.hasPermission("rcskills.admin")) {
            throw  new CommandException("Du hast nicht genügend Rechte um den Klassenwechsel zu erzwingen!");
        }

        try {
            Hero hero = plugin.getCharacterManager().getHero((Player) sender);
            Profession profession = ProfessionUtil.getProfessionFromArgs(hero, args.getJoinedStrings(0));

            if (hero.hasProfession(profession) && profession.isActive()) {
                throw new CommandException("Du hast diese " + profession.getPath().getFriendlyName() + " Spezialisierung bereits ausgewählt.");
            }
            if (!profession.isMeetingAllRequirements(hero.getPlayer())) {
                throw new CommandException("Du kannst diese " + profession.getPath().getFriendlyName() + " Spezialisierung nicht auswählen: \n" + profession.getResolveReason(hero.getPlayer()));
            }

            if (force) {
                chooseProfession(hero, profession);
            } else {
                double cost = 0.0;
                if (profession.getAttachedLevel().getLevel() > 1) {
                    cost = ProfessionUtil.getProfessionChangeCost(profession);
                }
                if(cost > RaidCraft.getEconomy().getBalance(((Player) sender).getUniqueId())) {
                    sender.sendMessage(ChatColor.RED + "Du kannst dir den Wechsel nicht leisten.");
                    sender.sendMessage(ChatColor.RED + "Dir fehlen noch " +
                            RaidCraft.getEconomy().getFormattedAmount(cost-RaidCraft.getEconomy().getBalance(((Player) sender).getUniqueId())));
                    return;
                }
                sender.sendMessage(ChatColor.GREEN + "Bist du dir sicher dass du " +
                        "deine " + ChatColor.AQUA + profession.getPath().getFriendlyName()
                        + ChatColor.GREEN + " Spezialisierung wechseln willst?");
                if (cost > 0.0 && RaidCraft.getEconomy() != null) {
                    sender.sendMessage(ChatColor.RED +
                            "Das wechseln deiner " + ChatColor.AQUA + profession.getPath().getFriendlyName() + ChatColor.RED +
                            " Spezialisierung zum " + ChatColor.AQUA + profession.getProperties().getFriendlyName() + ChatColor.RED +
                            " kostet dich " + RaidCraft.getEconomy().getFormattedAmount(cost));
                }
                new QueuedCommand(sender, this, "chooseProfession", hero, profession);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new CommandException(e.getMessage());
        }
    }

    public void chooseProfession(Hero hero, Profession profession) {

        if (!profession.isMeetingAllRequirements(hero.getPlayer())) {
            return;
        }

        double cost = 0.0;
        if (profession.getAttachedLevel().getLevel() > 1) {
            cost = ProfessionUtil.getProfessionChangeCost(profession);
        }
        hero.changeProfession(profession);
        hero.sendMessage(ChatColor.YELLOW + "Du hast deine " + ChatColor.AQUA + profession.getPath().getFriendlyName() +
                ChatColor.YELLOW + " Spezialisierung erfolgreich zu " + ChatColor.AQUA + profession.getProperties().getFriendlyName() + " gewechselt.");

        if (RaidCraft.getEconomy() != null && cost > 0.0) {
            hero.sendMessage(ChatColor.RED + "Dir wurden " + ChatColor.AQUA + RaidCraft.getEconomy().getFormattedAmount(cost)
                    + ChatColor.RED + " vom Konto abgezogen.");
            RaidCraft.getEconomy().modify(hero.getPlayer().getUniqueId(), -cost, BalanceSource.SKILL, "--> " + profession.getFriendlyName());
        }
    }

    @Command(
            aliases = {"info"},
            desc = "Shows information about a profession",
            flags = "p:"
    )
    @CommandPermissions("rcskills.player.profession.info")
    public void info(CommandContext args, CommandSender sender) throws CommandException {

        Hero hero = plugin.getCharacterManager().getHero((Player) sender);
        if (args.hasFlag('p')) {
                hero = plugin.getCharacterManager().getHero(UUIDUtil.convertPlayer(args.getFlag('p')));
        }
        if(hero == null) {
            throw new CommandException("invalid Player");
        }

        Profession profession = hero.getSelectedProfession();
        if (args.argsLength() > 0) {
            profession = ProfessionUtil.getProfessionFromArgs(hero, args.getJoinedStrings(0));
        }

        for (FancyMessage message : ProfessionUtil.getProfessionTooltip(profession, false)) {
            message.send(sender);
        }
    }
}
