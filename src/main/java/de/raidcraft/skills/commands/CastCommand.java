package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.util.StringUtil;
import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * All Skills and Abilities can be bound to an item or cast with a command.
 * This class handels the manual casting of a skill.
 *
 * @author Silthus
 */
public class CastCommand {

    private final SkillsPlugin plugin;

    public CastCommand(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = "cast",
            desc = "Casts the given skill",
            min = 1
    )
    public void cast(CommandContext args, CommandSender sender) throws CommandException {

        if (!(sender instanceof Player)) {
            return;
        }

        Hero hero;
        hero = plugin.getHeroManager().getHero((Player) sender);

        // lets parse the argument for a valid spell
        Skill skill = getSkillFromArg(hero, args.getJoinedStrings(0));

        if (!skill.isActive()) {
            throw new CommandException("Der gewählte Skills gehört zu keinem aktiven Beruf oder Klasse.");
        }
        if (!skill.isUnlocked()) {
            throw new CommandException("Du hast diesen Skill noch nicht freigeschaltet.");
        }

        try {
            hero.runSkill(skill);
        } catch (CombatException | InvalidTargetException e) {
            throw new CommandException(e.getMessage());
        }
    }

    private Skill getSkillFromArg(Hero hero, String input) throws CommandException {

        List<Skill> foundSkills = new ArrayList<>();
        input = input.toLowerCase().trim();
        for (Skill skill : hero.getSkills()) {
            if (skill.getName().toLowerCase().contains(input)
                    || skill.getFriendlyName().toLowerCase().contains(input)) {
                foundSkills.add(skill);
            }
        }

        if (foundSkills.size() < 1) {
            throw new CommandException("Du kennst keinen Skill mit dem Namen: " + input);
        }

        if (foundSkills.size() > 1) {
            throw new CommandException(
                    "Es gibt mehrere Skills mit dem Namen: " + input + " - " + StringUtil.joinString(foundSkills, ", ", 0));
        }

        return foundSkills.get(0);
    }
}
