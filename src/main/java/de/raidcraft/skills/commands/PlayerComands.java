package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.api.commands.QueuedCommand;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.InvalidChoiceException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.hero.Option;
import de.raidcraft.skills.api.level.AttachedLevel;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.util.ProfessionUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * @author Silthus
 */
public class PlayerComands {

    private final SkillsPlugin plugin;

    public PlayerComands(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"info"},
            desc = "Gives information about the hero level and exp pool"
    )
    @CommandPermissions("rcskills.player.cmd.info")
    public void info(CommandContext args, CommandSender sender) throws CommandException {

        try {
            Hero hero = plugin.getCharacterManager().getHero(sender.getName());
            Collection<String> strings = ProfessionUtil.renderProfessionInformation(hero.getVirtualProfession());
            strings.add(ChatColor.YELLOW + "EXP Pool: " + ChatColor.AQUA + hero.getExpPool().getExp() + ChatColor.YELLOW + " EXP");
            for (String line : strings) {
                hero.sendMessage(line);
            }
            if (hero.getResources().size() > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(ChatColor.YELLOW).append("Resourcen: \n");
                for (Resource resource : hero.getResources()) {
                    sb.append(ChatColor.YELLOW).append("  - ");
                    sb.append(ChatColor.YELLOW).append(resource.getFriendlyName()).append(": ");
                    sb.append(ChatColor.AQUA).append(resource.getCurrent()).append(ChatColor.YELLOW).append("/");
                    sb.append(ChatColor.AQUA).append(resource.getMax()).append(ChatColor.YELLOW);
                    sb.append("\n");
                }
                hero.sendMessage(sb.toString());
            }
        } catch (UnknownPlayerException e) {
            throw new CommandException(e.getMessage());
        }
    }

    @Command(
            aliases = {"link"},
            desc = "Links the EXP pool to the defined profession.",
            usage = "<beruf/klasse>",
            flags = "d"
    )
    @CommandPermissions("rcskills.player.cmd.link")
    public void linkExpPool(CommandContext args, CommandSender sender) throws CommandException {

        Hero hero = plugin.getCharacterManager().getHero((Player) sender);
        if (args.hasFlag('d') || args.argsLength() < 1) {
            // unlink exp pool
            Option.EXP_POOL_LINK.set(hero, null);
            sender.sendMessage(ChatColor.RED + "Die Verknüpfung mit deinem EXP Pool wurde aufgehoben.");
        } else {
            Profession profession = ProfessionUtil.getProfessionFromArgs(hero, args.getJoinedStrings(0));
            if (profession.isMastered()) {
                throw new CommandException("Diese " + profession.getPath().getFriendlyName()
                        + " Spezialisierung hat bereits das maximale Level erreicht.");
            }
            Option.EXP_POOL_LINK.set(hero, profession.getName());
            sender.sendMessage(ChatColor.GREEN + "Dein EXP Pool ist nun mit deiner " + profession.getPath().getFriendlyName()
                    + " Spezialisierung " + ChatColor.AQUA + profession.getFriendlyName() + ChatColor.GREEN + " verknüpft.");
        }
    }

    @Command(
            aliases = {"addxp", "addexp"},
            desc = "Adds EXP from the EXP pool into a profession or skill",
            flags = "fa",
            usage = "<beruf/klasse> [-a] <exp>",
            min = 1
    )
    @CommandPermissions("rcskills.player.cmd.addexp")
    public void addExpCommand(CommandContext args, CommandSender sender) throws CommandException {

        try {
            Hero hero = plugin.getCharacterManager().getHero((Player) sender);
            AttachedLevel<Hero> expPool = hero.getExpPool();
            if (!(expPool.getExp() > 0)) {
                throw new CommandException("Dein EXP Pool ist leer und du kannst keine EXP verteilen.");
            }
            if (!args.hasFlag('a') && args.argsLength() < 2) {
                throw new CommandException("Du musst angeben wieviel EXP du vergeben möchtest: /rcs addxp <beruf/klasse> <exp>. " +
                        "Gebe /rcsa addxp <beruf/klasse> -a ein um alle EXP zu vergeben.");
            }
            int exp = (args.hasFlag('a') ? expPool.getExp() : args.getInteger(1));
            if (exp > expPool.getExp()) {
                throw new CommandException("Du kannst maximal " + expPool.getExp() + "exp verteilen. " +
                        "Gebe /rcs addxp <beruf/klasse> -a ein um alle EXP zu vergeben.");
            }
            AttachedLevel attachedLevel = ProfessionUtil.getProfessionFromArgs(hero, args.getString(0)).getAttachedLevel();
            if (attachedLevel == null) {
                throw new CommandException("Bitte gebe eine Spezialisierung an, der du EXP geben willst.");
            }

            if (args.hasFlag('f')) {
                // force the addexp
                addExp(expPool, attachedLevel, exp);
            } else {
                hero.sendMessage(ChatColor.RED + "Bist du sicher, dass du " + ChatColor.AQUA
                        + attachedLevel.getLevelObject() + " " + exp + "exp " + ChatColor.RED + "zuteilen willst?");
                new QueuedCommand(sender, this, "addExp", expPool, attachedLevel, exp);
            }
        } catch (InvalidChoiceException e) {
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
    @CommandPermissions("rcskills.player.cmd.combatlog")
    public void combatLog(CommandContext args, CommandSender sender) {

        Hero hero = plugin.getCharacterManager().getHero((Player) sender);
        Option.COMBAT_LOGGING.set(hero, (!Option.COMBAT_LOGGING.getBoolean(hero)) + "");
        sender.sendMessage("" + ChatColor.RED + ChatColor.ITALIC + "Kampflog wurde " + ChatColor.AQUA +
                (Option.COMBAT_LOGGING.getBoolean(hero) ? "eingeschaltet." : "ausgeschaltet."));
    }

    private void addExp(AttachedLevel<Hero> expPool, AttachedLevel attachedLevel, int exp) throws InvalidChoiceException {

        Hero hero = expPool.getLevelObject();
        if (exp > expPool.getExp()) {
            plugin.getLogger().warning(hero.getName() + " tried to exploit the system by adding more exp than he has!");
            throw new InvalidChoiceException("Du kannst maximal " + expPool.getExp() + "exp verteilen.");
        }
        expPool.removeExp(exp);
        attachedLevel.addExp(exp);
        hero.sendMessage(ChatColor.GREEN + "Die EXP aus deinem EXP Pool wurden erfolgreich übertragen.");
    }
}
