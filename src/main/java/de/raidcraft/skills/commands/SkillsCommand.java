package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.util.PaginatedResult;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Silthus
 */
public class SkillsCommand {

    private final SkillsPlugin plugin;

    public SkillsCommand(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = "skills",
            desc = "Shows all skills for the selected profession.",
            usage = "[profession] -p #",
            flags = "p:ag"
    )
    public void skills(CommandContext args, CommandSender sender) throws CommandException {

        final Hero hero;
        hero = plugin.getHeroManager().getHero((Player) sender);
        final Profession profession;
        List<Skill> skills = new ArrayList<>();
        // get the profession
        if (args.argsLength() > 0) {
            try {
                profession = plugin.getProfessionManager().getProfession(hero, args.getString(0));
            } catch (UnknownProfessionException | UnknownSkillException e) {
                throw new CommandException(e.getMessage());
            }
        } else {
            profession = hero.getSelectedProfession();
        }
        if (profession == null) {
            throw new CommandException("Du hast noch keinen Beruf oder Klasse gewählt.");
        }
        // lets get the skills the sender wants to have displayed
        skills.addAll(profession.getSkills());
        if (args.hasFlag('a')) {
            skills.addAll(plugin.getSkillManager().getAllSkills());
        }
        if (args.hasFlag('g')) {
            skills.removeAll(profession.getSkills());
            skills.addAll(profession.getUnlockedSkills());
        }
        // lets sort them by their required level
        Collections.sort(skills);
        // lets list all skills
        new PaginatedResult<Skill>("[Prof:Level] -   Name    -   Beschreibung   | " + ChatColor.AQUA + profession.getProperties().getFriendlyName()) {

            @Override
            public String format(Skill skill) {

                StringBuilder sb = new StringBuilder();

                int level = skill.getProperties().getRequiredLevel();

                sb.append(ChatColor.YELLOW).append("[").append(ChatColor.GREEN)
                        .append(profession.getProperties().getTag()).append(":")
                        .append((profession.getLevel().getLevel() < level ? ChatColor.RED : ChatColor.GREEN)).append(level)
                        .append(ChatColor.YELLOW).append("] ");
                sb.append((hero.hasSkill(skill) ? ChatColor.GREEN : ChatColor.RED)).append(skill.getProperties().getFriendlyName());
                sb.append(ChatColor.GRAY).append(ChatColor.ITALIC).append(" - ").append(skill.getDescription());
                return sb.toString();
            }
        }.display(sender, skills, args.getFlagInteger('p', 1));
    }
}
