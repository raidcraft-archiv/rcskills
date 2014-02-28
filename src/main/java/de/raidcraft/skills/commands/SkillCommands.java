package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.effect.EffectInformation;
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
            flags = "c",
            min = 1
    )
    @CommandPermissions("rcskills.player.skill.info")
    public void info(CommandContext args, CommandSender sender) throws CommandException {

        Hero hero = plugin.getCharacterManager().getHero((Player) sender);
        Skill skill = SkillUtil.getSkillFromArgs(hero, args.getJoinedStrings(0));

        sender.sendMessage(SkillUtil.formatHeader(skill));
        for (String msg : SkillUtil.formatBody(skill)) {
            sender.sendMessage(msg);
        }
        
        if (args.hasFlag('c')) {
            String[] configUsage = skill.getSkillProperties().getInformation().configUsage();
            if (configUsage.length > 0) {
                sender.sendMessage("\nConfiguration Information:");
                for (String usage : configUsage) {
                    sender.sendMessage(usage);
                }
            }
            Class<? extends Effect>[] effects = skill.getSkillProperties().getInformation().effects();
            for (Class<? extends Effect> effect : effects) {
                EffectInformation annotation = effect.getAnnotation(EffectInformation.class);
                sender.sendMessage(annotation.name() + "{");
                String[] effectUsage = annotation.configUsage();
                for (String usage : effectUsage) {
                    sender.sendMessage("\t" + usage);
                }
                sender.sendMessage("},");
            }
        }
    }
}