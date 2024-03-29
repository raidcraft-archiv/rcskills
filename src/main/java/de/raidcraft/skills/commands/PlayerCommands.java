package de.raidcraft.skills.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.api.commands.QueuedCommand;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.InvalidChoiceException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.hero.Option;
import de.raidcraft.skills.api.level.AttachedLevel;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.util.HeroUtil;
import de.raidcraft.skills.util.ProfessionUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public class PlayerCommands {

    private final SkillsPlugin plugin;

    public PlayerCommands(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"info"},
            desc = "Gives information about the hero level and exp pool"
    )
    @CommandPermissions("rcskills.player.cmd.info")
    public void info(CommandContext args, CommandSender sender) throws CommandException {

        Hero hero;
        if (args.argsLength() > 0) {
            Optional<Hero> optional = plugin.getCharacterManager().getHero(args.getString(0));
            if (!optional.isPresent()) {
                throw new CommandException("Hero " + args.getString(0) + " not found!");
            }
            hero = optional.get();
        } else {
            hero = plugin.getCharacterManager().getHero((Player) sender);
        }
        List<BaseComponent> messages = new ArrayList<>();
        ComponentBuilder cb = new ComponentBuilder("------- ").color(ChatColor.GOLD)
                .append("[").color(ChatColor.YELLOW).append(hero.getVirtualProfession().getTotalLevel() + "").color(ChatColor.AQUA)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, ProfessionUtil.getProfessionTooltip(hero.getVirtualProfession(), true)))
                .append("]").color(ChatColor.YELLOW).append(" ")
                .append("[").color(ChatColor.BLACK).append(hero.getName()).color(HeroUtil.getPvPColor(hero, (Player) sender).asBungee())
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, HeroUtil.getHeroTooltip(hero, (Player) sender, true).toArray(new BaseComponent[0])))
                .append("]").color(ChatColor.BLACK)
                .append(" -------").color(ChatColor.GOLD);
        messages.addAll(Arrays.asList(cb.create()));

        messages.addAll(HeroUtil.getBasicHeroInfo(hero, false));

        List<Profession> professions = hero.getProfessions().stream().filter(Profession::isActive).collect(Collectors.toList());
        professions.stream().map(profession -> new ComponentBuilder(profession.getFriendlyName()).color(profession.isMastered() ? ChatColor.GOLD : ChatColor.YELLOW)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, ProfessionUtil.getProfessionTooltip(profession, true)))
                .append("  |  ").color(ChatColor.DARK_PURPLE)
                .append("Level: ").color(ChatColor.YELLOW)
                .append(profession.getAttachedLevel().getLevel() + "").color(ChatColor.AQUA)
                .append("/").color(ChatColor.YELLOW)
                .append(profession.getAttachedLevel().getMaxLevel() + "").color(ChatColor.AQUA)
                .append("  |  ").color(ChatColor.DARK_PURPLE)
                .append("EXP: ").color(ChatColor.YELLOW)
                .append(profession.getAttachedLevel().getExp() + "").color(ChatColor.AQUA)
                .append("/").color(ChatColor.YELLOW)
                .append(profession.getAttachedLevel().getMaxExp() + "").color(ChatColor.AQUA).create())
                .forEach(componentBuilder -> messages.addAll(Arrays.asList(componentBuilder)));

        sender.spigot().sendMessage(messages.toArray(new BaseComponent[0]));
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
            if (exp < 0) {
                throw new CommandException("Du kannst dir nicht EXP wegnehmen sondern nur hinzufügen!");
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

    private void addExp(AttachedLevel<Hero> expPool, AttachedLevel attachedLevel, int exp) throws InvalidChoiceException {

        Hero hero = expPool.getLevelObject();

        // pool -> level
        // check if pool contains enough exp
        if (exp > expPool.getExp()) {
            plugin.getLogger().warning(hero.getName() + " tried to exploit the system by adding more exp than he has!");
            throw new InvalidChoiceException("Du kannst maximal " + expPool.getExp() + "exp verteilen.");
        }
        // level -> pool
        // check if level contains enough exp
        if(exp < 0) {
            plugin.getLogger().warning(hero.getName() + " tried to exploit the system by removing more exp than he has!");
            throw new InvalidChoiceException("Du kannst dir nicht EXP wegnehmen sondern nur hinzufügen!");
        }

        int level = attachedLevel.getLevel() + attachedLevel.getLevelAmountForExp(exp);
        if (level >= attachedLevel.getMaxLevel()) {
            exp = attachedLevel.getNeededExpForLevel(attachedLevel.getLevel(), attachedLevel.getMaxLevel() - 1);
            if (exp <= 0) {
                throw new InvalidChoiceException("Du hast mit dieser Klasse bereits das max. Level erreicht.");
            }
            hero.sendMessage(ChatColor.RED + "Es wurden nur " + ChatColor.AQUA + exp
                    + ChatColor.RED + " EXP verteilt, da du das max. Level erreicht hast.");
        }
        expPool.removeExp(exp);
        attachedLevel.addExp(exp);
        hero.sendMessage(ChatColor.GREEN + "Die EXP aus deinem EXP Pool wurden erfolgreich übertragen.");
    }

    @Command(
            aliases = {"clear", "leave", "clearcache", "cache"},
            desc = "Kicks the player and clears his cache"
    )
    @CommandPermissions("rcskills.player.cmd.clearcache")
    public void kick(CommandContext args, CommandSender sender) {

        final Hero hero = plugin.getCharacterManager().getHero((Player) sender);
        hero.getPlayer().kickPlayer("Dein RPG Profil Cache wird geleert. Bitte warte 10 Sekunden.");
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {

                plugin.getCharacterManager().clearCacheOf(hero);
            }
        }, 5L);
    }

    @Command(
            aliases = {"combatlog", "cl", "kampflog", "kl"},
            desc = "Aktiviert/Deaktiviert das Kampflog."
    )
    @CommandPermissions("rcskills.player.cmd.combatlog")
    public void combatLog(CommandContext args, CommandSender sender) {

        Hero hero = plugin.getCharacterManager().getHero((Player) sender);
        Option.COMBAT_LOGGING.set(hero, !Option.COMBAT_LOGGING.isSet(hero));
        sender.sendMessage("" + ChatColor.RED + ChatColor.ITALIC + "Kampflog wurde " + ChatColor.AQUA +
                (Option.COMBAT_LOGGING.isSet(hero) ? "eingeschaltet." : "ausgeschaltet."));
    }

    @Command(
            aliases = {"party", "partyhealth", "ph"},
            desc = "Zeigt Gruppenmitglieder in der Seitenleiste an."
    )
    @CommandPermissions("rcskills.player.cmd.partyhealth")
    public void partyDisplay(CommandContext args, CommandSender sender) {

        Hero hero = plugin.getCharacterManager().getHero((Player) sender);
        Option.SIDEBAR_PARTY_HP.set(hero, !Option.SIDEBAR_PARTY_HP.isSet(hero));
        sender.sendMessage("" + ChatColor.RED + ChatColor.ITALIC + "Die Gruppen Anzeige " + ChatColor.AQUA +
                (Option.SIDEBAR_PARTY_HP.isSet(hero) ? "eingeschaltet." : "ausgeschaltet."));
    }

    @Command(
            aliases = {"actionbar", "ab", "leveldisplay", "ld", "hud"},
            desc = "Zeigt Gruppenmitglieder in der Seitenleiste an."
    )
    @CommandPermissions("rcskills.player.cmd.actionbar")
    public void actionBar(CommandContext args, CommandSender sender) {

        Hero hero = plugin.getCharacterManager().getHero((Player) sender);
        Option.ACTION_BAR.set(hero, !Option.ACTION_BAR.isSet(hero));
        sender.sendMessage("" + ChatColor.RED + ChatColor.ITALIC + "Das Level HUD wurde " + ChatColor.AQUA +
                (Option.ACTION_BAR.isSet(hero) ? "eingeschaltet." : "ausgeschaltet."));
    }
}
