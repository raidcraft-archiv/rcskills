package de.raidcraft.skills.bindings;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.CommandTriggered;
import de.raidcraft.skills.util.SkillUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BindCommands {

    private final SkillsPlugin plugin;

    public BindCommands(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = "bind",
            desc = "Binds a skill to an item",
            min = 1
    )
    @CommandPermissions("rcskills.player.bind")
    public void bind(CommandContext args, CommandSender sender) throws CommandException {

        if (!(sender instanceof Player)) {
            return;
        }

        Hero hero;
        hero = plugin.getCharacterManager().getHero((Player) sender);

        if(hero.getPlayer().getItemInHand() == null) {
            throw new CommandException("Kein Item in der Hand.");
        }

        // lets parse the argument for a valid spell
        Skill skill = SkillUtil.getSkillFromArgs(hero, args.getString(0));

        if (!(skill instanceof CommandTriggered)) {
            throw new CommandException("Du kannst diesen Skill nicht binden.");
        }

        if(BindManager.INST.addBinding(hero, hero.getPlayer().getItemInHand().getType(), skill)) {
            hero.sendMessage(ChatColor.DARK_GREEN + "Der Skill " + skill.getFriendlyName() + " wurde an dieses Item gebunden!");
            return;
        }
        throw new CommandException("Dieser Skill ist bereits an dieses Item gebunden!");
    }

    @Command(
            aliases = "unbind",
            desc = "Unbind all skills on an item"
    )
         @CommandPermissions("rcskills.player.unbind")
         public void unbind(CommandContext args, CommandSender sender) throws CommandException {

        if (!(sender instanceof Player)) {
            return;
        }

        Hero hero;
        hero = plugin.getCharacterManager().getHero((Player) sender);

        if(hero.getPlayer().getItemInHand() == null) {
            throw new CommandException("Kein Item in der Hand.");
        }

        if(BindManager.INST.removeBindings(hero, hero.getPlayer().getItemInHand().getType())) {
            hero.sendMessage(ChatColor.DARK_GREEN + "Alle Skills auf diesem Item wurden entfernt!");
            return;
        }
        throw new CommandException("An dieses Item sind keine Skills gebunden!");
    }

    @Command(
            aliases = "autobind",
            desc = "Autobind all skills to items"
    )
    @CommandPermissions("rcskills.player.unbind")
    public void autobind(CommandContext args, CommandSender sender) throws CommandException {

        if (!(sender instanceof Player)) {
            return;
        }

        Hero hero;
        hero = plugin.getCharacterManager().getHero((Player) sender);
    }
}
