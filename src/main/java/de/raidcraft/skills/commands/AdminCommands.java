package de.raidcraft.skills.commands;

import com.avaje.ebean.Ebean;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.api.commands.QueuedCaptchaCommand;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.hero.Option;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.util.HeroUtil;
import de.raidcraft.skills.util.ProfessionUtil;
import de.raidcraft.skills.util.SkillUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class AdminCommands {

    private final SkillsPlugin plugin;

    public AdminCommands(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = "reload",
            desc = "Reloads the Skills plugin"
    )
    @CommandPermissions("rcskills.admin.reload")
    public void reload(CommandContext args, CommandSender sender) {

        plugin.reload();
        sender.sendMessage(ChatColor.GREEN + "Reloaded all " + ChatColor.RED + "Config Files " + ChatColor.GREEN + "and " +
                ChatColor.RED + "Managers " + ChatColor.GREEN + "successfully!");
    }

    @Command(
            aliases = "debug",
            desc = "Sends a lot of debug output to the player"
    )
    @CommandPermissions("rcskills.admin.debug")
    public void debug(CommandContext args, CommandSender sender) throws CommandException {

        sender.sendMessage("Du hast die permission testpermission: " + sender.hasPermission("testpermission"));

        Hero hero;
        if (args.argsLength() > 1) {
            try {
                hero = plugin.getCharacterManager().getHero(args.getString(0));
            } catch (UnknownPlayerException e) {
                throw new CommandException(e.getMessage());
            }
        } else {
            hero = plugin.getCharacterManager().getHero((Player) sender);
        }
        Option.DEBUGGING.set(hero, !Option.DEBUGGING.isSet(hero));
        sender.sendMessage("" + ChatColor.RED + ChatColor.ITALIC + "Toggled debug mode: " + ChatColor.AQUA +
                (Option.DEBUGGING.isSet(hero) ? "on." : "off."));
    }

    @Command(
            aliases = "maxout",
            desc = "Maxes our all of the player levels",
            usage = "<player>",
            min = 1
    )
    public void maxOutAll(CommandContext args, CommandSender sender) throws CommandException {

        try {
            Hero hero = plugin.getCharacterManager().getHero(args.getString(0));
            HeroUtil.maxOutAll(hero);
            sender.sendMessage(ChatColor.GREEN + "Alle Skills, Berufe und Klassen von " + hero.getName() + " wurden auf max gesetzt.");
        } catch (UnknownPlayerException e) {
            throw new CommandException(e.getMessage());
        }
    }

    @Command(
            aliases = "addskill",
            desc = "Adds a skill to a player - not the profession!",
            usage = "<player> <skill>",
            min = 2
    )
    @CommandPermissions("rcskills.admin.skill.add")
    public void addSkill(CommandContext args, CommandSender sender) throws CommandException {

        try {
            Hero hero = plugin.getCharacterManager().getHero(args.getString(0));
            Skill skill = plugin.getSkillManager().getSkill(hero, hero.getVirtualProfession(), args.getString(1));
            if (skill.isUnlocked()) {
                throw new CommandException("Der Spieler hat den Skill bereits.");
            }
            hero.addSkill(skill);
            sender.sendMessage(ChatColor.GREEN + "Du hast " + ChatColor.AQUA + hero.getName() + ChatColor.GREEN + " den Skill "
                    + ChatColor.AQUA + skill.getName() + ChatColor.GREEN + " hinzugefügt.");
        } catch (UnknownPlayerException | UnknownSkillException e) {
            throw new CommandException(e.getMessage());
        }
    }

    @Command(
            aliases = "removeskill",
            desc = "Removes a virtual skill from the player",
            usage = "<player> <skill>",
            min = 2
    )
    @CommandPermissions("rcskills.admin.skill.remove")
    public void removeSkill(CommandContext args, CommandSender sender) throws CommandException {

        try {
            Hero hero = plugin.getCharacterManager().getHero(args.getString(0));
            Skill skill = plugin.getSkillManager().getSkill(hero, hero.getVirtualProfession(), args.getString(1));
            if (!skill.isUnlocked()) {
                throw new CommandException("Der Spieler hat den Skill nicht.");
            }
            hero.removeSkill(skill);
            sender.sendMessage(ChatColor.RED + "Du hast " + ChatColor.AQUA + hero.getName() + ChatColor.RED + " den Skill "
                    + ChatColor.AQUA + skill.getName() + ChatColor.RED + " entfernt.");
        } catch (UnknownPlayerException | UnknownSkillException e) {
            throw new CommandException(e.getMessage());
        }
    }

    @Command(
            aliases = {"addexp", "addxp", "axp"},
            desc = "Adds exp to the hero, prof or skill of the player",
            usage = "<player> [-p <prof>] [-s <skill>] [-h] <exp>",
            flags = "p:s:h",
            min = 2
    )
    @CommandPermissions("rcskills.admin.exp.add")
    public void addExp(CommandContext args, CommandSender sender) throws CommandException {

        try {
            Hero hero = plugin.getCharacterManager().getHero(args.getString(0));
            int exp = args.getInteger(1);
            if (args.hasFlag('h')) {
                hero.getLevel().addExp(exp);
                sender.sendMessage(ChatColor.GREEN + "Du hast " + ChatColor.AQUA +
                        hero.getName() + " " + exp + "xp" + ChatColor.GREEN + " hinzugefügt.");
                hero.sendMessage(ChatColor.GREEN + "Ein Admin hat dir " + ChatColor.AQUA
                        + " " + exp + "exp" + ChatColor.GREEN + " hinzugefügt.");
            }
            if (args.hasFlag('p')) {
                Profession profession = ProfessionUtil.getProfessionFromArgs(hero, args.getFlag('p'), hero.getProfessions());
                profession.getLevel().addExp(exp);
                sender.sendMessage(ChatColor.GREEN + "Du hast " + ChatColor.AQUA +
                        hero.getName() + "'s " + ChatColor.GREEN + "Spezialisierung " + ChatColor.AQUA + profession.getName()
                        + exp + "xp" + ChatColor.GREEN + " hinzugefügt.");
                hero.sendMessage(ChatColor.GREEN + "Ein Admin hat deiner Spezialisierung " + ChatColor.AQUA + profession.getFriendlyName()
                        + " " + exp + "exp" + ChatColor.GREEN + " hinzugefügt.");
            }
            if (args.hasFlag('s')) {
                Skill skill = SkillUtil.getSkillFromArgs(hero, args.getFlag('s'));
                if (skill instanceof Levelable) {
                    ((Levelable) skill).getLevel().addExp(exp);
                    sender.sendMessage(ChatColor.GREEN + "Du hast " + ChatColor.AQUA +
                            hero.getName() + "'s " + ChatColor.GREEN + "Skill " + ChatColor.AQUA + skill.getName()
                            + exp + "xp" + ChatColor.GREEN + " hinzugefügt.");
                    hero.sendMessage(ChatColor.GREEN + "Ein Admin hat deinem Skill " + ChatColor.AQUA + skill.getFriendlyName()
                            + " " + exp + "exp" + ChatColor.GREEN + " hinzugefügt.");
                } else {
                    throw new CommandException("Der Skill " + skill.getName() + " ist kein Levelbarer Skill.");
                }
            }
            if (!args.hasFlag('p') && !args.hasFlag('s') && !args.hasFlag('h')) {
                // lets add the exp to the pool of the hero
                hero.getExpPool().addExp(exp);
                sender.sendMessage(ChatColor.GREEN + "Du hast " + ChatColor.AQUA +
                        hero.getName() + "'s " + ChatColor.GREEN + "EXP Pool "
                        + ChatColor.AQUA + exp + "xp" + ChatColor.GREEN + " hinzugefügt.");
                hero.sendMessage(ChatColor.GREEN + "Ein Admin hat deinem EXP Pool " + ChatColor.AQUA +
                        + exp + "exp" + ChatColor.GREEN + " hinzugefügt.");
            }
        } catch (UnknownPlayerException e) {
            throw new CommandException(e.getMessage());
        }
    }

    @Command(
            aliases = {"removeexp", "removexp", "rxp"},
            desc = "Removes exp to the hero, prof or skill of the player",
            usage = "<player> [-p <prof>] [-s <skill>] [-h] <exp>",
            flags = "p:s:h",
            min = 2
    )
    @CommandPermissions("rcskills.admin.exp.remove")
    public void removeExp(CommandContext args, CommandSender sender) throws CommandException {

        try {
            Hero hero = plugin.getCharacterManager().getHero(args.getString(0));
            int exp = args.getInteger(1);
            if (args.hasFlag('h')) {
                hero.getLevel().removeExp(exp);
                sender.sendMessage(ChatColor.RED + "Du hast " + ChatColor.AQUA +
                        hero.getName() + " " + exp + "xp" + ChatColor.RED + " entfernt.");
                hero.sendMessage(ChatColor.RED + "Ein Admin hat dir " + ChatColor.AQUA
                        + " " + exp + "exp" + ChatColor.RED + " entfernt.");
            }
            if (args.hasFlag('p')) {
                Profession profession = ProfessionUtil.getProfessionFromArgs(hero, args.getFlag('p'), hero.getProfessions());
                profession.getLevel().removeExp(exp);
                sender.sendMessage(ChatColor.RED + "Du hast " + ChatColor.AQUA +
                        hero.getName() + "'s " + ChatColor.RED + "Spezialisierung " + ChatColor.AQUA + profession.getName()
                        + exp + "xp" + ChatColor.RED + " entfernt.");
                hero.sendMessage(ChatColor.RED + "Ein Admin hat deiner Spezialisierung " + ChatColor.AQUA + profession.getFriendlyName()
                        + " " + exp + "exp" + ChatColor.RED + " entfernt.");
            }
            if (args.hasFlag('s')) {
                Skill skill = SkillUtil.getSkillFromArgs(hero, args.getFlag('s'));
                if (skill instanceof Levelable) {
                    ((Levelable) skill).getLevel().removeExp(exp);
                    sender.sendMessage(ChatColor.RED + "Du hast " + ChatColor.AQUA +
                            hero.getName() + "'s " + ChatColor.RED + "Skill " + ChatColor.AQUA + skill.getName()
                            + exp + "xp" + ChatColor.RED + " entfernt.");
                    hero.sendMessage(ChatColor.RED + "Ein Admin hat deinem Skill " + ChatColor.AQUA + skill.getFriendlyName()
                            + " " + exp + "exp" + ChatColor.RED + " entfernt.");
                } else {
                    throw new CommandException("Der Skill " + skill.getName() + " ist kein Levelbarer Skill.");
                }
            }
            if (!args.hasFlag('p') && !args.hasFlag('s') && !args.hasFlag('h')) {
                // lets add the exp to the pool of the hero
                hero.getExpPool().removeExp(exp);
                sender.sendMessage(ChatColor.RED + "Du hast " + ChatColor.AQUA +
                        hero.getName() + "'s " + ChatColor.RED + "EXP Pool "
                        + ChatColor.AQUA + exp + "xp" + ChatColor.RED + " entfernt.");
                hero.sendMessage(ChatColor.RED + "Ein Admin hat deinem EXP Pool " + ChatColor.AQUA +
                        + exp + "exp" + ChatColor.RED + " entfernt.");
            }
        } catch (UnknownPlayerException e) {
            throw new CommandException(e.getMessage());
        }
    }

    @Command(
            aliases = {"addlevel", "addlvl", "al"},
            desc = "Adds level to the hero, prof or skill of the player",
            usage = "<player> [-p <prof>] [-s <skill>] [-h] <level>",
            flags = "p:s:h",
            min = 2
    )
    @CommandPermissions("rcskills.admin.level.add")
    public void addLevel(CommandContext args, CommandSender sender) throws CommandException {

        try {
            Hero hero = plugin.getCharacterManager().getHero(args.getString(0));
            int level = args.getInteger(1);
            if (args.hasFlag('h')) {
                hero.getLevel().addLevel(level);
                sender.sendMessage(ChatColor.GREEN + "Du hast " + ChatColor.AQUA +
                        hero.getName() + " " + level + " level" + ChatColor.GREEN + " hinzugefügt.");
                hero.sendMessage(ChatColor.GREEN + "Ein Admin hat dir " + ChatColor.AQUA
                        + " " + level + " level" + ChatColor.GREEN + " hinzugefügt.");
            }
            if (args.hasFlag('p')) {
                Profession profession = ProfessionUtil.getProfessionFromArgs(hero, args.getFlag('p'), hero.getProfessions());
                profession.getLevel().addLevel(level);
                sender.sendMessage(ChatColor.GREEN + "Du hast " + ChatColor.AQUA +
                        hero.getName() + "'s " + ChatColor.GREEN + "Spezialisierung " + ChatColor.AQUA + profession.getName()
                        + level + " level" + ChatColor.GREEN + " hinzugefügt.");
                hero.sendMessage(ChatColor.GREEN + "Ein Admin hat deiner Spezialisierung " + ChatColor.AQUA + profession.getFriendlyName()
                        + " " + level + " level" + ChatColor.GREEN + " hinzugefügt.");
            }
            if (args.hasFlag('s')) {
                Skill skill = SkillUtil.getSkillFromArgs(hero, args.getFlag('s'));
                if (skill instanceof Levelable) {
                    ((Levelable) skill).getLevel().addLevel(level);
                    sender.sendMessage(ChatColor.GREEN + "Du hast " + ChatColor.AQUA +
                            hero.getName() + "'s " + ChatColor.GREEN + "Skill " + ChatColor.AQUA + skill.getName()
                            + level + " level" + ChatColor.GREEN + " hinzugefügt.");
                    hero.sendMessage(ChatColor.GREEN + "Ein Admin hat deinem Skill " + ChatColor.AQUA + skill.getFriendlyName()
                            + " " + level + " level" + ChatColor.GREEN + " hinzugefügt.");
                } else {
                    throw new CommandException("Der Skill " + skill.getName() + " ist kein Levelbarer Skill.");
                }
            }

            if (!args.hasFlag('p') && !args.hasFlag('s') && !args.hasFlag('h')) {
                throw new CommandException("Du kannst dem EXP Pool des Spielers keine Level hinzufügen.");
            }
        } catch (UnknownPlayerException e) {
            throw new CommandException(e.getMessage());
        }
    }

    @Command(
            aliases = {"removelevel", "removelvl", "rl"},
            desc = "Removes level to the hero, prof or skill of the player",
            usage = "<player> [-p <prof>] [-s <skill>] [-h] <level>",
            flags = "p:s:h",
            min = 2
    )
    @CommandPermissions("rcskills.admin.level.remove")
    public void removeLevel(CommandContext args, CommandSender sender) throws CommandException {

        try {
            Hero hero = plugin.getCharacterManager().getHero(args.getString(0));
            int level = args.getInteger(1);
            if (args.hasFlag('h')) {
                hero.getLevel().removeLevel(level);
                sender.sendMessage(ChatColor.RED + "Du hast " + ChatColor.AQUA +
                        hero.getName() + " " + level + "level" + ChatColor.RED + " entfernt.");
                hero.sendMessage(ChatColor.RED + "Ein Admin hat dir " + ChatColor.AQUA
                        + " " + level + "level" + ChatColor.RED + " entfernt.");
            }
            if (args.hasFlag('p')) {
                Profession profession = ProfessionUtil.getProfessionFromArgs(hero, args.getFlag('p'), hero.getProfessions());
                profession.getLevel().removeLevel(level);
                sender.sendMessage(ChatColor.RED + "Du hast " + ChatColor.AQUA +
                        hero.getName() + "'s " + ChatColor.RED + "Spezialisierung " + ChatColor.AQUA + profession.getName()
                        + level + "level" + ChatColor.RED + " entfernt.");
                hero.sendMessage(ChatColor.RED + "Ein Admin hat deiner Spezialisierung " + ChatColor.AQUA + profession.getFriendlyName()
                        + " " + level + "level" + ChatColor.RED + " entfernt.");
            }
            if (args.hasFlag('s')) {
                Skill skill = SkillUtil.getSkillFromArgs(hero, args.getFlag('s'));
                if (skill instanceof Levelable) {
                    ((Levelable) skill).getLevel().removeLevel(level);
                    sender.sendMessage(ChatColor.RED + "Du hast " + ChatColor.AQUA +
                            hero.getName() + "'s " + ChatColor.RED + "Skill " + ChatColor.AQUA + skill.getName()
                            + level + "level" + ChatColor.RED + " entfernt.");
                    hero.sendMessage(ChatColor.RED + "Ein Admin hat deinem Skill " + ChatColor.AQUA + skill.getFriendlyName()
                            + " " + level + "level" + ChatColor.RED + " entfernt.");
                } else {
                    throw new CommandException("Der Skill " + skill.getName() + " ist kein Levelbarer Skill.");
                }
            }

            if (!args.hasFlag('p') && !args.hasFlag('s') && !args.hasFlag('h')) {
                throw new CommandException("Du kannst dem EXP Pool des Spielers keine Level entfernen.");
            }
        } catch (UnknownPlayerException e) {
            throw new CommandException(e.getMessage());
        }
    }

    @Command(
            aliases = "purge",
            desc = "Removes all references from a player from the database.",
            min = 1,
            flags = "f"
    )
    @CommandPermissions("rcskills.admin.purge")
    public void purge(CommandContext args, CommandSender sender) throws CommandException {

        try {
            Hero hero = plugin.getCharacterManager().getHero(args.getString(0));
            if (args.hasFlag('f')) {
                purgeHero(sender, hero);
            } else {
                new QueuedCaptchaCommand(sender, this, "purgeHero", sender, hero);
            }
        } catch (UnknownPlayerException | NoSuchMethodException e) {
            throw new CommandException(e.getMessage());
        }
    }

    private void purgeHero(CommandSender sender, Hero hero) {

        // kick the player if he is online
        if (hero.getPlayer() != null) {
            hero.getPlayer().kickPlayer("Dein RPG Profil wird zurück gesetzt bitte warte kurz.");
        }
        // this will delete all references to the object
        Ebean.find(THero.class, hero.getId()).delete();
        // remove the player from cache
        plugin.getCharacterManager().clearCacheOf(hero);
        sender.sendMessage(ChatColor.GREEN + "Alle Daten von " + hero.getName() + " wurden erfolgreich gelöscht.");
    }
}
