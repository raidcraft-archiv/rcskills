package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.api.commands.QueuedCommand;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.InvalidChoiceException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.util.ProfessionUtil;
import de.raidcraft.util.PaginatedResult;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
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
            for (Profession profession : temp) {
                if (!profession.isUnlockable()) {
                    professions.remove(profession);
                }
            }
        }

        Collections.sort(professions);

        new PaginatedResult<Profession>("Tag   -   Spezialisierung   -   Pfad   -   Level") {

            @Override
            public String format(Profession profession) {

                return ChatColor.YELLOW + "[" + ChatColor.AQUA + profession.getProperties().getTag() + ChatColor.YELLOW + "]" +
                        (profession.isActive() ? ChatColor.GREEN : ChatColor.RED) + profession.getProperties().getFriendlyName() +
                        ChatColor.GRAY + ChatColor.ITALIC + " - " + profession.getPath().getFriendlyName() + " - " +
                        ChatColor.RESET + (profession.getLevel().getLevel() > 0 ? ChatColor.GREEN : ChatColor.RED) + profession.getLevel().getLevel();
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

        try {
            Hero hero = plugin.getCharacterManager().getHero((Player) sender);
            Profession profession = ProfessionUtil.getProfessionFromArgs(hero, args.getJoinedStrings(0));

            if (hero.hasProfession(profession) && profession.isActive()) {
                throw new CommandException("Du hast diese " + profession.getPath().getFriendlyName() + " Spezialisierung bereits ausgewählt.");
            }
            if (!profession.isUnlockable()) {
                throw new CommandException("Du kannst diese " + profession.getPath().getFriendlyName() + " Spezialisierung nicht auswählen: \n" + profession.getUnlockReason());
            }

            if (force) {
                chooseProfession(hero, profession);
            } else {
                int cost = 0;
                if (hero.getProfessions().size() > 0) {
                    cost = (int) (plugin.getCommonConfig().profession_change_cost +
                            (plugin.getCommonConfig().profession_change_level_modifier * profession.getLevel().getLevel()));
                }
                sender.sendMessage(ChatColor.GREEN + "Bist du dir sicher dass du " +
                        "deine " + ChatColor.AQUA + profession.getPath().getFriendlyName()
                        + ChatColor.GREEN + " Spezialisierung wechseln willst?");
                if (cost > 0) {
                    sender.sendMessage(ChatColor.RED +
                            "Das wechseln deiner " + ChatColor.AQUA + profession.getPath().getFriendlyName() + ChatColor.RED +
                            " Spezialisierung zum " + ChatColor.AQUA + profession.getProperties().getFriendlyName() + ChatColor.RED +
                                    " kostet dich " + ChatColor.AQUA + cost + plugin.getEconomy().currencyNamePlural());
                }
                new QueuedCommand(sender, this, "chooseProfession", hero, profession);
            }
        } catch (InvalidChoiceException e) {
            throw new CommandException(e.getMessage());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new CommandException(e.getMessage());
        }
    }

    @Command(
            aliases = {"info"},
            desc = "Shows information about a profession",
            flags = "h"
    )
    @CommandPermissions("rcskills.player.profession.info")
    public void info(CommandContext args, CommandSender sender) throws CommandException {

        Hero hero = plugin.getCharacterManager().getHero((Player) sender);
        if (args.hasFlag('h')) {
            try {
                hero = plugin.getCharacterManager().getHero(args.getFlag('h'));
            } catch (UnknownPlayerException e) {
                throw new CommandException(e.getMessage());
            }
        }

        Profession profession = hero.getSelectedProfession();
        if (args.argsLength() > 0) {
            profession = ProfessionUtil.getProfessionFromArgs(hero, args.getJoinedStrings(0));
        }

        Collection<String> strings = ProfessionUtil.renderProfessionInformation(profession);
        sender.sendMessage(strings.toArray(new String[strings.size()]));
    }

    public void chooseProfession(Hero hero, Profession profession) throws InvalidChoiceException {

        if (!profession.isUnlockable()) {
            return;
        }

        int cost = 0;
        if (hero.getProfessions().size() > 0) {
            cost = (int) (plugin.getCommonConfig().profession_change_cost +
                    (plugin.getCommonConfig().profession_change_level_modifier * profession.getLevel().getLevel()));
        }
        hero.changeProfession(profession);
        hero.sendMessage(ChatColor.YELLOW + "Du hast deine " + ChatColor.AQUA + profession.getPath().getFriendlyName() +
                ChatColor.YELLOW + " Spezialisierung erfolgreich zu " + ChatColor.AQUA + profession.getProperties().getFriendlyName() + " gewechselt.");

        if (cost > 0) {
            hero.sendMessage(ChatColor.RED + "Dir wurden " + ChatColor.AQUA + ChatColor.AQUA + cost + plugin.getEconomy().currencyNamePlural()
                    + ChatColor.RED + " vom Konto abgezogen.");
        }
    }
}
