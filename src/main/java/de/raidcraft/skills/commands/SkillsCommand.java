package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.skills.ProfessionManager;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.LevelableSkill;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.util.ProfessionUtil;
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
            flags = "p:avsh"
    )
    @CommandPermissions("rcskills.player.skill.list")
    public void skills(CommandContext args, CommandSender sender) throws CommandException {

        final Hero hero;
        hero = plugin.getCharacterManager().getHero((Player) sender);
        // Profession profession = null;
        List<Skill> skills = new ArrayList<>();
        // get the profession
        if (args.argsLength() > 0) {
            skills.addAll(ProfessionUtil.getProfessionFromArgs(hero, args.getJoinedStrings(0)).getSkills());
        } else if (args.hasFlag('s')) {
            skills.addAll(hero.getSelectedProfession().getSkills());
        } else if (args.hasFlag('a')) {
            skills.addAll(plugin.getSkillManager().getAllSkills(hero));
            if (args.hasFlag('v')) skills.addAll(plugin.getSkillManager().getAllVirtualSkills(hero));
        } else if (args.hasFlag('v')) {
            // only add virtual skills we have
            for (Skill skill : hero.getSkills()) {
                if (skill.getProfession().getName().equalsIgnoreCase(ProfessionManager.VIRTUAL_PROFESSION)) {
                    skills.add(skill);
                }
            }
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
        // remove all virtual skills
        if (!args.hasFlag('v')) {
            for (Skill skill : new ArrayList<>(skills)) {
                if (skill.getProfession().getName().equalsIgnoreCase(ProfessionManager.VIRTUAL_PROFESSION)) {
                    skills.remove(skill);
                }
            }
        }
        // lets sort them by their required level
        Collections.sort(skills);
        // lets list all skills
        new PaginatedResult<Skill>("[Prof:Level] -   Name") {

            @Override
            public String format(Skill skill) {

                StringBuilder sb = new StringBuilder();

                int level = skill.getProperties().getRequiredLevel();
                Profession profession = skill.getProfession();

                sb.append(ChatColor.YELLOW).append("[").append(profession.isActive() ? ChatColor.GREEN : ChatColor.RED)
                        .append(profession.getProperties().getTag()).append(":")
                        .append((profession.getAttachedLevel().getLevel() < level ? ChatColor.RED : ChatColor.AQUA)).append(level)
                        .append(ChatColor.YELLOW).append("] ");
                sb.append((skill.isActive() && skill.isUnlocked() ? ChatColor.GREEN : ChatColor.RED))
                        .append(skill.getProperties().getFriendlyName());
                if (skill instanceof LevelableSkill) {
                    sb.append(ChatColor.YELLOW).append("[").append(ChatColor.AQUA).append(((LevelableSkill) skill).getAttachedLevel().getLevel())
                            .append(ChatColor.YELLOW).append("] ");
                }
                return sb.toString();
            }
        }.display(sender, skills, args.getFlagInteger('p', 1));
    }
}
