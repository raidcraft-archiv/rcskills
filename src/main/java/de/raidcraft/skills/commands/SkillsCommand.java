package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.ProfessionManager;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.util.HeroUtil;
import de.raidcraft.skills.util.ProfessionUtil;
import de.raidcraft.skills.util.SkillUtil;
import de.raidcraft.util.ComponentPaginatedResult;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

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
            usage = "[profession] [-o <player>] -p #",
            flags = "p:avsho:"
    )
    @CommandPermissions("rcskills.player.skill.list")
    public void skills(CommandContext args, CommandSender sender) throws CommandException {

        final Hero hero;
        if (args.hasFlag('o')) {
            try {
                hero = HeroUtil.getHeroFromName(args.getFlag('o'));
            } catch (UnknownPlayerException e) {
                throw new CommandException(e.getMessage());
            }
        } else {
            hero = plugin.getCharacterManager().getHero((Player) sender);
        }
        // Profession profession = null;
        Collection<Skill> skills = new HashSet<>();
        // get the profession
        if (args.argsLength() > 0) {
            skills.addAll(ProfessionUtil.getProfessionFromArgs(hero, args.getString(0)).getSkills());
        } else if (args.hasFlag('s')) {
            skills.addAll(hero.getSelectedProfession().getSkills());
        } else if (args.hasFlag('a')) {
            skills.addAll(plugin.getSkillManager().getAllSkills(hero));
            if (args.hasFlag('v')) skills.addAll(plugin.getSkillManager().getAllVirtualSkills(hero));
        } else if (args.hasFlag('v')) {
            skills.addAll(hero.getVirtualProfession().getSkills());
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

        skills = new ArrayList<>(skills);
        // lets sort them by their required level
        Collections.sort((List<Skill>) skills);
        // lets list all skills
        new ComponentPaginatedResult<Skill>("[Prof:Level] -   Name") {

            @Override
            public BaseComponent[] format(Skill skill) {

                int level = skill.getRequiredLevel();
                Profession profession = skill.getProfession();

                ComponentBuilder msg = new ComponentBuilder("[").color(ChatColor.YELLOW.asBungee())
                        .append(profession.getProperties().getTag()).color(profession.getProperties().getColor().asBungee())
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, ProfessionUtil.getProfessionTooltip(profession, true)))
                        .append(":").color(ChatColor.YELLOW.asBungee())
                        .append(level + "").color(profession.getAttachedLevel().getLevel() < level ? ChatColor.DARK_RED.asBungee() : ChatColor.AQUA.asBungee())
                        .append("] ").color(ChatColor.YELLOW.asBungee())
                        .append(skill.getFriendlyName()).color(skill.isUnlocked() ? ChatColor.GREEN.asBungee() : ChatColor.DARK_RED.asBungee())
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, SkillUtil.getSkillTooltip(skill, true)));
                if (skill instanceof Levelable) {
                    msg = msg.append(" (").color(ChatColor.YELLOW.asBungee())
                            .append(((Levelable) skill).getAttachedLevel().getLevel() + "").color(ChatColor.AQUA.asBungee())
                            .append("/").color(ChatColor.YELLOW.asBungee())
                            .append(((Levelable) skill).getAttachedLevel().getMaxLevel() + "").color(ChatColor.AQUA.asBungee())
                            .append(")").color(ChatColor.YELLOW.asBungee());
                }
                return msg.create();
            }
        }.display(sender, skills, args.getFlagInteger('p', 1));
    }
}
