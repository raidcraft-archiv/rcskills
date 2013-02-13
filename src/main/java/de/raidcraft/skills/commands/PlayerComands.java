package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.api.commands.QueuedCommand;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.InvalidChoiceException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.hero.Option;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.util.ProfessionUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class PlayerComands {

    private final SkillsPlugin plugin;

    public PlayerComands(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"addxp", "addexp"},
            desc = "Adds EXP from the EXP pool into a profession or skill",
            flags = "p:s:f",
            usage = "[-p profession] [-s skill] [-f] <exp>",
            min = 1
    )
    public void addExpCommand(CommandContext args, CommandSender sender) throws CommandException {

        try {
            if (!args.hasFlag('p') && !args.hasFlag('s')) {
                throw new CommandException("Du musst die EXP einem Skill oder Spezialisierung geben.");
            }
            Hero hero = plugin.getCharacterManager().getHero((Player) sender);
            Level<Hero> expPool = hero.getExpPool();
            if (!(expPool.getExp() > 0)) {
                throw new CommandException("Dein EXP Pool ist leer und du kannst keine EXP verteilen.");
            }
            int exp = args.getInteger(0);
            if (exp > expPool.getExp()) {
                throw new CommandException("Du kannst maximal " + expPool.getExp() + "exp verteilen.");
            }
            Level level = null;
            if (args.hasFlag('p')) {
                Profession profession = ProfessionUtil.getProfessionFromArgs(hero, args.getFlag('p'));
                level = profession.getLevel();
            } else if (args.hasFlag('s')) {
                Skill skill = hero.getSkill(args.getFlag('s'));
                if (!(skill instanceof Levelable)) {
                    throw new CommandException("Der Skill " + skill.getFriendlyName() + " ist nicht levelbar.");
                }
                level = ((Levelable) skill).getLevel();
            }
            if (level == null) {
                throw new CommandException("Bitte gebe einen Skill oder eine Spezialisierung an, dem du EXP geben willst.");
            }

            if (args.hasFlag('f')) {
                // force the addexp
                addExp(expPool, level, exp);
            } else {
                hero.sendMessage(ChatColor.RED + "Bist du sicher, dass du " + ChatColor.AQUA
                        + level.getLevelObject() + " " + exp + "exp " + ChatColor.RED + "zuteilen willst?");
                new QueuedCommand(sender, this, "addExp", expPool, level, exp);
            }
        } catch (UnknownSkillException | InvalidChoiceException e) {
            throw new CommandException(e.getMessage());
        } catch (NoSuchMethodException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
    }

    @Command(
            aliases = {"combatlog", "cl", "kampflog", "kl"},
            desc = "Aktiviert/Deaktiviert das Kampflog."
    )
    public void combatLog(CommandContext args, CommandSender sender) {

        Hero hero = plugin.getCharacterManager().getHero((Player) sender);
        Option.COMBAT_LOGGING.set(hero, !Option.COMBAT_LOGGING.isSet(hero));
        sender.sendMessage("" + ChatColor.RED + ChatColor.ITALIC + "Kampflog wurde " + ChatColor.AQUA +
                (Option.COMBAT_LOGGING.isSet(hero) ? "eingeschaltet." : "ausgeschaltet."));
    }

    private void addExp(Level<Hero> expPool, Level level, int exp) throws InvalidChoiceException {

        Hero hero = expPool.getLevelObject();
        if (exp > expPool.getExp()) {
            plugin.getLogger().warning(hero.getName() + " tried to exploit the system by adding more exp than he has!");
            throw new InvalidChoiceException("Du kannst maximal " + expPool.getExp() + "exp verteilen.");
        }
        expPool.removeExp(exp);
        level.addExp(exp);
        hero.sendMessage(ChatColor.GREEN + "Die EXP aus deinem EXP Pool wurden erfolgreich übertragen.");
    }
}
