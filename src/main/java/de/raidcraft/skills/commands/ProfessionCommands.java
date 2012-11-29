package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
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
    public void list(final CommandContext args, CommandSender sender) throws CommandException {

        if (!(sender instanceof Player)) {
            return;
        }

        final Hero hero = plugin.getHeroManager().getHero((Player) sender);
        List<Profession> professions = hero.getProfessions();

        if (professions == null || professions.size() < 1) {
            throw new CommandException("Da gibts noch nix zu sehen!");
        }

        for (int i = 0; i < professions.size(); i++) {
            if (!args.hasFlag('a') && !professions.get(i).isActive()) {
                professions.remove(i);
            }
            if (args.hasFlag('c') && !hero.canChoose(professions.get(i))) {
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
}
