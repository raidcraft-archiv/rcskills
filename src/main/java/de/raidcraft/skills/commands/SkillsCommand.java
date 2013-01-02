package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.*;
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
            flags = "p:ash"
    )
    @CommandPermissions("rcskills.player.skill.list")
    public void skills(CommandContext args, CommandSender sender) throws CommandException {

        final Hero hero;
        hero = plugin.getCharacterManager().getHero((Player) sender);
        // Profession profession = null;
        List<Skill> skills = new ArrayList<>();
        // get the profession
        if (args.argsLength() > 0) {
            try {
                skills.addAll(plugin.getProfessionManager().getProfession(hero, args.getString(0)).getSkills());
            } catch (UnknownProfessionException | UnknownSkillException e) {
                throw new CommandException(e.getMessage());
            }
        } else if (args.hasFlag('s')) {
            skills.addAll(hero.getSelectedProfession().getSkills());
        } else if (args.hasFlag('a')) {
            skills.addAll(plugin.getSkillManager().getAllSkills(hero));
        } else {
            // lets get the skills the sender wants to have displayed
            skills.addAll(hero.getSkills());
        }
        // lets remove hidden skills from the list
        if (!args.hasFlag('h')) {
            for (Skill skill : new ArrayList<>(skills)) {
                if (skill.isHidden()) {
                    skills.remove(skill);
                }
            }
        }
        // lets sort them by their required level
        Collections.sort(skills);
        // lets list all skills
        new PaginatedResult<Skill>("[Prof:Level] -   Name    -   Beschreibung") {

            @Override
            public String format(Skill skill) {

                StringBuilder sb = new StringBuilder();

                int level = skill.getProperties().getRequiredLevel();
                Profession profession = skill.getProfession();

                sb.append(ChatColor.YELLOW).append("[").append(profession.isActive() ? ChatColor.GREEN : ChatColor.RED)
                        .append(profession.getProperties().getTag()).append(":")
                        .append((profession.getLevel().getLevel() < level ? ChatColor.RED : ChatColor.AQUA)).append(level)
                        .append(ChatColor.YELLOW).append("] ");
                sb.append((skill.isActive() && skill.isUnlocked() ? ChatColor.GREEN : ChatColor.RED))
                        .append(skill.getProperties().getFriendlyName());
                sb.append(ChatColor.GRAY).append(ChatColor.ITALIC).append(" - ").append(skill.getDescription());
                return sb.toString();
            }
        }.display(sender, skills, args.getFlagInteger('p', 1));
    }
}
