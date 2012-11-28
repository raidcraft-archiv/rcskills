package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.AreaAttack;
import de.raidcraft.skills.api.Passive;
import de.raidcraft.skills.api.TargetedAttack;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * All Skills and Abilities can be bound to an item or cast with a command.
 * This class handels the manual casting of a skill.
 *
 * @author Silthus
 */
public final class CastCommand {

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
        try {
            hero = plugin.getHeroManager().getHero((Player) sender);
        } catch (UnknownProfessionException e) {
            throw new CommandException(e);
        }

        // lets parse the argument for a valid spell
        Skill skill = hero.getSkillFromArg(args.getJoinedStrings(0));

        try {
            if (skill instanceof TargetedAttack) {
                ((TargetedAttack) skill).run(hero, hero.getTarget());
            } else if (skill instanceof AreaAttack) {
                ((AreaAttack) skill).run(hero, BukkitUtil.toBlock(hero.getTargetBlock()).getLocation());
            } else if (skill instanceof Passive) {
                // always keep this the last check
                throw new CommandException("Dieser Skill ist passiv und kann nicht angewendet werden.");
            }
        } catch (CombatException e) {
            throw new CommandException("Skill konnte nicht angewendet werden: " + e.getFailCause().getMessage());
        }
    }
}
