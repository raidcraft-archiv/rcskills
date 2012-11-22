package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.skills.SkillsPlugin;
import org.bukkit.command.CommandSender;

/**
 * @author Silthus
 */
public class SkillCommands {

    public static class Parent {

        @Command(
                aliases = {"skill"},
                desc = "Command zur Verwaltung der eigenen und verfügbaren Skills."
        )
        @NestedCommand(SkillCommands.class)
        public static void skills(CommandContext args, CommandSender sender) {

        }
    }

    private final SkillsPlugin component;

    public SkillCommands(SkillsPlugin component) {

        this.component = component;
    }

    @Command(
            aliases = "list",
            desc = "Listet je nach Parameter die eigenen oder verfügbaren Skills auf.",
            usage = "[flags]",
            help = "-a: zeigt alle Skills an\n" +
                    "-g: zeigt alle automatisch erhaltenen Skills an\n" +
                    "-l <level>: zeigt alle Skills für das Level an\n" +
                    "-b: zeigt alle kaufbaren Skills an\n" +
                    "-c <Klasse/Beruf>: zeigt alle Skills der Klasse, des Berufes an\n" +
                    "-p <Seite>: Zeigt weitere Seiten an\n",
            flags = "c:agbl:p:"
    )
    @CommandPermissions("rcskills.skill.list")
    public void list(CommandContext args, CommandSender sender) throws CommandException {


    }
}
