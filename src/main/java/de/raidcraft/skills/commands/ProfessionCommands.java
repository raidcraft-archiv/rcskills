package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.api.commands.QueuedCaptchaCommand;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.InvalidChoiceException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.util.ProfessionUtil;
import de.raidcraft.util.PaginatedResult;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            flags = "p:ac"
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
            throw new CommandException("Da gibts noch nix zu sehen!");
        }

        for (int i = 0; i < professions.size(); i++) {
            if (!args.hasFlag('a') && !professions.get(i).isActive()) {
                professions.remove(i);
            } else if (args.hasFlag('c') && !professions.get(i).isUnlockable()) {
                professions.remove(i);
            }
        }
        Collections.sort(professions);

        new PaginatedResult<Profession>("Tag   -   Beruf/Klasse   -   Primär/Sekundär   -   Level") {

            @Override
            public String format(Profession profession) {

                return ChatColor.YELLOW + "[" + ChatColor.AQUA + profession.getProperties().getTag() + ChatColor.YELLOW + "]" +
                        (profession.isActive() ? ChatColor.GREEN : ChatColor.RED) + profession.getProperties().getFriendlyName() +
                        ChatColor.GRAY + ChatColor.ITALIC + " - " + (profession.getProperties().isPrimary() ? "Primär" : "Sekundär") + " - " +
                        ChatColor.RESET + (profession.getLevel().getLevel() > 0 ? ChatColor.GREEN : ChatColor.RED) + profession.getLevel().getLevel();
            }
        }.display(sender, professions, args.getFlagInteger('p', 1));
    }

    @Command(
            aliases = {"choose", "c"},
            desc = "Wählt die gewünschte Klasse oder Beruf.",
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
            boolean primary = profession.getProperties().isPrimary();

            if (primary && hero.getPrimaryProfession() != null && hero.getPrimaryProfession().equals(profession)) {
                throw new CommandException("Du hast diese Klasse aktuell ausgewählt.");
            }
            if (hero.getSecundaryProfession() != null && hero.getSecundaryProfession().equals(profession)) {
                throw new CommandException("Du hast diesen Beruf aktuell ausgewählt.");
            }
            if (!profession.isUnlockable()) {
                throw new CommandException("Du kannst " +
                        (primary ? "diese Klasse" : "diesen Beruf")
                        + " nicht auswählen. \n" + profession.getUnlockReason());
            }

            if (force) {
                chooseProfession(hero, profession);
            } else {
                int cost = (int) (plugin.getCommonConfig().profession_change_cost +
                        (plugin.getCommonConfig().profession_change_level_modifier * profession.getLevel().getLevel()));
                sender.sendMessage(ChatColor.GREEN + "Bist du dir sicher dass du " +
                        (primary ? "deine " + ChatColor.AQUA + "Klasse" : "deinen " + ChatColor.AQUA + "Beruf") + ChatColor.GREEN
                        + " neuwählen willst?");
                sender.sendMessage(ChatColor.RED +
                        "Das wechseln deiner " +
                        (primary ? "deiner " + ChatColor.AQUA + "Klasse" : "deines " + ChatColor.AQUA + "Berufs") + ChatColor.RED +
                        " zum " + ChatColor.AQUA + profession.getProperties().getFriendlyName() + ChatColor.RED +
                        " kostet dich " + ChatColor.AQUA + cost + plugin.getEconomy().currencyNamePlural());
                new QueuedCaptchaCommand(sender, this,
                        getClass().getDeclaredMethod("chooseProfession", Hero.class, Profession.class),
                        hero, profession);
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
            desc = "Shows information about a profession"
    )
    @CommandPermissions("rcskills.player.profession.info")
    public void info(CommandContext args, CommandSender sender) {

        Hero hero = plugin.getCharacterManager().getHero((Player) sender);
        Profession profession = hero.getSelectedProfession();

        sender.sendMessage(ChatColor.YELLOW + profession.getProperties().getFriendlyName());
    }

    private void chooseProfession(Hero hero, Profession profession) throws InvalidChoiceException {

        if (!profession.isUnlockable()) {
            return;
        }

        int cost = (int) (plugin.getCommonConfig().profession_change_cost +
                (plugin.getCommonConfig().profession_change_level_modifier * profession.getLevel().getLevel()));
        boolean primary = profession.getProperties().isPrimary();
        hero.changeProfession(profession);
        hero.sendMessage(ChatColor.YELLOW + "Du hast " +
                (primary ? "deine " + ChatColor.AQUA + "Klasse" : "deinen " + ChatColor.AQUA + "Beruf") + ChatColor.YELLOW +
                " erfolgreich zum " + ChatColor.AQUA + profession.getProperties().getFriendlyName() + " gewechselt.");
        hero.sendMessage(ChatColor.RED + "Dir wurden " + ChatColor.AQUA + ChatColor.AQUA + cost + plugin.getEconomy().currencyNamePlural()
                + ChatColor.RED + " vom Konto abgezogen.");
    }
}
