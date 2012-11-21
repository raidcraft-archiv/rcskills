package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownPlayerProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.util.PaginatedResult;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Silthus
 */
public class SkillsCommands {

    private final SkillsPlugin component;

    public SkillsCommands(SkillsPlugin component) {

        this.component = component;
    }

    @Command(
            aliases = "skills",
            desc = "Shows all skills for the selected profession.",
            usage = "[profession] -p #",
            flags = "p:ag"
    )
    public void skills(CommandContext args, CommandSender sender) throws CommandException {

        final Hero hero;
        try {
            hero = component.getHero(sender.getName());
        } catch (UnknownPlayerException e) {
            throw new CommandException(e.getMessage());
        } catch (UnknownProfessionException e) {
            throw new CommandException(e.getMessage());
        }
        final LevelableProfession profession;
        List<Skill> skills = new ArrayList<>();
        // get the profession
        if (args.argsLength() > 0) {
            try {
                profession = component.getProfessionManager().getPlayerProfession(args.getString(0), hero);
            } catch (UnknownProfessionException e) {
                throw new CommandException(e.getMessage());
            } catch (UnknownPlayerProfessionException e) {
                throw new CommandException(e.getMessage());
            }
        } else {
            try {
                profession = (LevelableProfession) hero.getSelectedProfession();
            } catch (ClassCastException e) {
                e.printStackTrace();
                throw new CommandException("Programmierfehler wat da fuck?!");
            }
        }
        // lets get the skills the sender wants to have displayed
        skills.addAll(profession.getSkills());
        if (args.hasFlag('a')) {
            skills.addAll(component.getSkillManager().getAllSkills());
        }
        if (args.hasFlag('g')) {
            skills.removeAll(profession.getSkills());
            skills.addAll(profession.getGainedSkills());
        }
        // lets sort them by their required level
        Collections.sort(skills);
        // lets list all skills
        new PaginatedResult<Skill>("[Prof:Level] -   Name    -   Beschreibung   | " + ChatColor.AQUA + profession.getFriendlyName()) {

            @Override
            public String format(Skill skill) {

                StringBuilder sb = new StringBuilder();

                int level = skill.getRequiredLevel();

                sb.append(ChatColor.YELLOW).append("[").append(ChatColor.GREEN)
                        .append(profession.getTag()).append(":")
                        .append((profession.getLevel().getLevel() < level ? ChatColor.RED : ChatColor.GREEN)).append(level)
                        .append(ChatColor.YELLOW).append("] ");
                sb.append((hero.hasSkill(skill) ? ChatColor.GREEN : ChatColor.RED)).append(skill.getFriendlyName());
                sb.append(ChatColor.GRAY).append(ChatColor.ITALIC).append(" - ").append(skill.getDescription(hero));
                return sb.toString();
            }
        }.display(sender, skills, args.getFlagInteger('p', 1));
    }
}
