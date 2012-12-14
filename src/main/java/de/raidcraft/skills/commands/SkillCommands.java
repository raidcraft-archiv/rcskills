package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.util.SkillUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class SkillCommands {

    private final SkillsPlugin plugin;

    public SkillCommands(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"info", "i"},
            desc = "Gives information about the skill",
            usage = "<skill>",
            min = 1
    )
    public void info(CommandContext args, CommandSender sender) throws CommandException {

        Hero hero = plugin.getHeroManager().getHero((Player) sender);
        Skill skill = SkillUtil.getSkillFromArgs(hero, args.getJoinedStrings(0));

        sender.sendMessage(SkillUtil.formatHeader(skill));
        for (String msg : SkillUtil.formatBody(skill)) {
            sender.sendMessage(msg);
        }
    }
}
