package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.combat.attack.SkillAction;
import de.raidcraft.skills.api.combat.effect.common.CastTime;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.util.SkillUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        hero = plugin.getCharacterManager().getHero((Player) sender);

        // lets parse the argument for a valid spell
        Skill skill = SkillUtil.getSkillFromArgs(hero, args.getJoinedStrings(0));

        if (!skill.isActive()) {
            throw new CommandException("Der gewählte Skills gehört zu keinem aktiven Beruf oder Klasse.");
        }
        if (!skill.isUnlocked()) {
            throw new CommandException("Du hast diesen Skill noch nicht freigeschaltet.");
        }

        try {
            if (skill.getTotalCastTime() > 0) {
                hero.addEffect(skill, CastTime.class);
            } else {
                new SkillAction(skill).run();
            }
        } catch (CombatException | InvalidTargetException e) {
            throw new CommandException(e.getMessage());
        }
    }
}
