package de.raidcraft.skills.commands;

import com.avaje.ebean.EbeanServer;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.util.StringUtil;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.ambient.AmbientEffect;
import de.raidcraft.api.commands.QueuedCaptchaCommand;
import de.raidcraft.api.items.EquipmentSlot;
import de.raidcraft.api.language.TranslationProvider;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.effect.Stackable;
import de.raidcraft.skills.api.effect.types.ExpirableEffect;
import de.raidcraft.skills.api.effect.types.PeriodicEffect;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Attribute;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.AbilityEffectStage;
import de.raidcraft.skills.api.skill.EffectEffectStage;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.CommandTriggered;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroProfession;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.skills.util.HeroUtil;
import de.raidcraft.skills.util.ProfessionUtil;
import de.raidcraft.skills.util.SkillUtil;
import de.raidcraft.util.PastebinPoster;
import de.raidcraft.util.TimeUtil;
import de.raidcraft.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public class AdminCommands {

    private final SkillsPlugin plugin;
    private final TranslationProvider tr;

    public AdminCommands(SkillsPlugin plugin) {

        this.plugin = plugin;
        this.tr = plugin.getTranslationProvider();
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
            desc = "Will dump the current player object, all skills and effects to pastebin."
    )
    @CommandPermissions("rcskills.admin.debug")
    public void debug(CommandContext args, final CommandSender sender) throws CommandException {

        sender.sendMessage("Du hast die permission testpermission: " + sender.hasPermission("testpermission"));

        Hero hero;
        if (args.argsLength() > 1) {
            hero = plugin.getCharacterManager().getHero(UUIDUtil.convertPlayer(args.getString(0)));
        } else {
            hero = plugin.getCharacterManager().getHero((Player) sender);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(hero.getName()).append("\n");
        sb.append("Entity ID: ").append(hero.getEntity().getEntityId()).append(" : ").append(hero.getEntity().getUniqueId()).append("\n");
        sb.append("First Login: ").append(new Timestamp(hero.getPlayer().getFirstPlayed())).append("\n");
        sb.append("Level: ").append(hero.getPlayerLevel()).append("\n");
        sb.append("PvP: ").append(hero.isPvPEnabled() ? "on" : "off").append("\n");
        sb.append("In Combat: ").append(hero.isInCombat()).append("\n");
        sb.append("Health: ").append(hero.getHealth()).append("/").append(hero.getMaxHealth()).append("\n");
        sb.append("Default Health: ").append(hero.getDefaultHealth()).append("\n");
        sb.append("Armor Value: ").append(hero.getTotalArmorValue()).append("\n");
        sb.append("Equipment: \n");
        sb.append("\tHead: ").append(hero.getArmor(EquipmentSlot.HEAD)).append("\n");
        sb.append("\tChest: ").append(hero.getArmor(EquipmentSlot.CHEST)).append("\n");
        sb.append("\tLegs: ").append(hero.getArmor(EquipmentSlot.LEGS)).append("\n");
        sb.append("\tFeet: ").append(hero.getArmor(EquipmentSlot.FEET)).append("\n");
        sb.append("\tOne Handed: ").append(hero.getWeapon(EquipmentSlot.ONE_HANDED)).append("\n");
        sb.append("\tTwo Handed: ").append(hero.getWeapon(EquipmentSlot.TWO_HANDED)).append("\n");
        sb.append("\tHands: ").append(hero.getWeapon(EquipmentSlot.HANDS)).append("\n");
        sb.append("\tShield Weapon: ").append(hero.getWeapon(EquipmentSlot.SHIELD_HAND)).append("\n");
        sb.append("\tShield Armor: ").append(hero.getArmor(EquipmentSlot.SHIELD_HAND)).append("\n");
        sb.append("Attributes: \n");
        for (Attribute attribute : hero.getAttributes()) {
            sb.append("\t").append(attribute.getName()).append("[").append(attribute.getType()).append("]: ")
                    .append(attribute.getCurrentValue()).append("[").append(attribute.getBaseValue()).append("]\n");
            sb.append("\t\tBonus Damage: \n");
            for (EffectType type : EffectType.values()) {
                if (attribute.getBonusDamage(type) != 0) {
                    sb.append("\t\t\t").append(type).append(": ").append(attribute.getBonusDamage(type)).append("\n");
                }
            }
        }
        sb.append("Profession:\n");
        for (Profession profession : hero.getProfessions()) {
            sb.append("\t").append(profession.getFriendlyName()).append("[").append(profession.getName()).append("#").append(profession.getId()).append("]: \n");
            sb.append("\t\tLevel: ").append(profession.getAttachedLevel().getLevel()).append("/").append(profession.getMaxLevel()).append("\n");
            sb.append("\t\tPath: ").append(profession.getPath().getFriendlyName()).append("[").append(profession.getPath().getName()).append("]\n");
            if (profession.hasParent()) {
                sb.append("\t\tParent: ").append(profession.getParent().getFriendlyName()).append("[").append(profession.getParent().getName()).append("]\n");
            }
            sb.append("\t\tChildren: ").append(StringUtil.joinString(profession.getChildren(), ",", 0));
            sb.append("\tSkills: \n").append(renderSkills(profession.getSkills())).append("\n");
        }
        sb.append("Skills:\n").append(renderSkills(hero.getSkills())).append("\n");
        sb.append("Active Effects: \n");
        for (Effect effect : hero.getEffects()) {
            sb.append("\t").append(effect.getFriendlyName()).append("[").append(effect.getName()).append("]").append("\n");
            sb.append("\t\tDescription: ").append(effect.getDescription()).append("\n");
            sb.append("\t\tTriggered: ").append(effect instanceof Triggered).append("\n");
            sb.append("\t\tStackable: ").append(effect instanceof Stackable).append("\n");
            sb.append("\t\tDamage: ").append(effect.getDamage()).append("\n");
            if (effect instanceof Stackable) {
                sb.append("\t\tStacks: ").append(((Stackable) effect).getStacks()).append("/").append(((Stackable) effect).getMaxStacks()).append("\n");
            }
            sb.append("\t\tEffect Types: ").append(StringUtil.joinString(effect.getTypes(), ", ", 0)).append("\n");
            // sb.append("\t\tEffect Elements: ").append(StringUtil.joinString(effect.getElements(), ", ", 0)).append("\n");
            if (effect instanceof ExpirableEffect) {
                sb.append("\t\tDuration: ").append(TimeUtil.secondsToTicks(((ExpirableEffect) effect).getRemainingDuration())).append("/").append(((ExpirableEffect) effect).getDuration()).append("\n");
            }
            if (effect instanceof PeriodicEffect) {
                sb.append("\t\tInterval").append(((PeriodicEffect) effect).getInterval()).append("\n");
            }
            sb.append("\t\tAmbient Effects:\n");
            for (EffectEffectStage stage : EffectEffectStage.values()) {
                List<AmbientEffect> ambientEffects = effect.getAmbientEffects(stage);
                if (!ambientEffects.isEmpty()) {
                    sb.append("\t\t\t").append(stage).append(": \n");
                    for (AmbientEffect ambientEffect : ambientEffects) {
                        sb.append("\t\t\t").append(ambientEffect.toString()).append("\n");
                    }
                }
            }
        }
        // lets send it to pastebin
        sender.sendMessage(ChatColor.YELLOW + "Pasting the debug output to pastebin...");
        PastebinPoster.paste(sb.toString(), new PastebinPoster.PasteCallback() {
            @Override
            public void handleSuccess(String url) {

                sender.sendMessage(ChatColor.GREEN + "Hero debug was pasted to: " + url);
            }

            @Override
            public void handleError(String err) {

                sender.sendMessage(ChatColor.RED + "Error pasting hero debug output to pastebin!");
            }
        });
    }

    private String renderSkills(Collection<Skill> skills) {

        StringBuilder sb = new StringBuilder();
        for (Skill skill : skills) {
            sb.append("\t").append(skill.getFriendlyName()).append("[").append(skill.getName()).append("#").append(skill.getId()).append("]: \n");
            sb.append("\t\tEnabled: ").append(skill.isEnabled()).append("\n");
            sb.append("\t\tActive: ").append(skill.isActive()).append("\n");
            sb.append("\t\tUnlocked: ").append(skill.isUnlocked()).append("\n");
            sb.append("\t\tProfession: ").append(skill.getProfession().getFriendlyName()).append("[").append(skill.getProfession().getName()).append("]").append("\n");
            sb.append("\t\tDescription: ").append(skill.getDescription()).append("\n");
            sb.append("\t\tUsage: ").append(Arrays.toString(skill.getUsage())).append("\n");
            sb.append("\t\t" + "Levelable: ").append(skill instanceof Levelable).append("\n");
            if (skill instanceof Levelable) {
                sb.append("\t\tLevel: ").append(((Levelable) skill).getAttachedLevel().getLevel()).append("/").append(((Levelable) skill).getMaxLevel()).append("\n");
            }
            sb.append("\t\tTriggered: ").append(skill instanceof Triggered).append("\n");
            sb.append("\t\tCommand Triggered: ").append(skill instanceof CommandTriggered).append("\n");
            sb.append("\t\tEXP per Use: ").append(skill.getUseExp()).append("\n");
            sb.append("\t\tEffect Types: ").append(StringUtil.joinString(skill.getTypes(), ", ", 0)).append("\n");
            // sb.append("\t\tEffect Elements: ").append(StringUtil.joinString(skill.getElements(), ", ", 0)).append("\n");
            sb.append("\t\tCooldown: ").append(skill.getRemainingCooldown()).append("/").append(skill.getConfiguredCooldown()).append("\n");
            sb.append("\t\tCast Time: ").append(skill.getTotalCastTime()).append("\n");
            sb.append("\t\tRange: ").append(skill.getTotalRange()).append("\n");
            sb.append("\t\tAmbient Effects:\n");
            for (AbilityEffectStage stage : AbilityEffectStage.values()) {
                List<AmbientEffect> ambientEffects = skill.getAmbientEffects(stage);
                if (!ambientEffects.isEmpty()) {
                    sb.append("\t\t\t").append(stage).append(": \n");
                    for (AmbientEffect effect : ambientEffects) {
                        sb.append("\t\t\t").append(effect.toString()).append("\n");
                    }
                }
            }
        }
        return sb.toString();
    }

    @Command(
            aliases = "maxout",
            desc = "Maxes our all of the player levels",
            usage = "<player>",
            min = 1
    )
    @CommandPermissions("rcskills.admin.maxout")
    public void maxOutAll(CommandContext args, CommandSender sender) throws CommandException {


        Hero hero = plugin.getCharacterManager().getHero(UUIDUtil.convertPlayer(args.getString(0)));
        HeroUtil.maxOutAll(hero);
        tr.msg(sender, "commands.max-out",
                "All skills and classes of %s have been maxed out.", hero.getName());
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
            Hero hero = plugin.getCharacterManager().getHero(UUIDUtil.convertPlayer(args.getString(0)));
            Skill skill = plugin.getSkillManager().getSkill(hero, hero.getVirtualProfession(), args.getString(1));
            if (skill.isUnlocked()) {
                throw new CommandException(tr.tr(sender, "commands.skill.duplicate", "The player already has the skill."));
            }
            hero.addSkill(skill);
            tr.msg(sender, "commands.skill.add", "You gave {1} the skill {2} ({3}).",
                    hero.getName(), skill.getFriendlyName(), skill.getName());
        } catch (UnknownSkillException e) {
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
            Hero hero = plugin.getCharacterManager().getHero(UUIDUtil.convertPlayer(args.getString(0)));
            Skill skill = plugin.getSkillManager()
                    .getSkill(hero, hero.getVirtualProfession(), args.getString(1));
            if (!skill.isUnlocked()) {
                throw new CommandException("Der Spieler hat den Skill nicht.");
            }
            hero.removeSkill(skill);
            sender.sendMessage(ChatColor.RED + "Du hast " + ChatColor.AQUA + hero.getName() + ChatColor.RED + " den Skill "
                    + ChatColor.AQUA + skill.getName() + ChatColor.RED + " entfernt.");
        } catch (UnknownSkillException e) {
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

        Hero hero = plugin.getCharacterManager().getHero(UUIDUtil.convertPlayer(args.getString(0)));
        int exp = args.getInteger(1);
        if (args.hasFlag('h')) {
            hero.getAttachedLevel().addExp(exp);
            sender.sendMessage(ChatColor.GREEN + "Du hast " + ChatColor.AQUA +
                    hero.getName() + " " + exp + "xp" + ChatColor.GREEN + " hinzugefügt.");
            hero.sendMessage(ChatColor.GREEN + "Ein Admin hat dir " + ChatColor.AQUA
                    + " " + exp + "exp" + ChatColor.GREEN + " hinzugefügt.");
        }
        if (args.hasFlag('p')) {
            Profession profession = ProfessionUtil.getProfessionFromArgs(hero, args.getFlag('p'), hero.getProfessions());
            profession.getAttachedLevel().addExp(exp);
            sender.sendMessage(ChatColor.GREEN + "Du hast " + ChatColor.AQUA +
                    hero.getName() + "'s " + ChatColor.GREEN + "Spezialisierung " + ChatColor.AQUA + profession.getName()
                    + exp + "xp" + ChatColor.GREEN + " hinzugefügt.");
            hero.sendMessage(ChatColor.GREEN + "Ein Admin hat deiner Spezialisierung " + ChatColor.AQUA + profession.getFriendlyName()
                    + " " + exp + "exp" + ChatColor.GREEN + " hinzugefügt.");
        }
        if (args.hasFlag('s')) {
            Skill skill = SkillUtil.getSkillFromArgs(hero, args.getFlag('s'));
            if (skill.isLevelable()) {
                ((Levelable) skill).getAttachedLevel().addExp(exp);
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
                    +exp + "exp" + ChatColor.GREEN + " hinzugefügt.");
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

        Hero hero = plugin.getCharacterManager().getHero(UUIDUtil.convertPlayer(args.getString(0)));
        int exp = args.getInteger(1);
        if (args.hasFlag('h')) {
            hero.getAttachedLevel().removeExp(exp);
            sender.sendMessage(ChatColor.RED + "Du hast " + ChatColor.AQUA +
                    hero.getName() + " " + exp + "xp" + ChatColor.RED + " entfernt.");
            hero.sendMessage(ChatColor.RED + "Ein Admin hat dir " + ChatColor.AQUA
                    + " " + exp + "exp" + ChatColor.RED + " entfernt.");
        }
        if (args.hasFlag('p')) {
            Profession profession = ProfessionUtil.getProfessionFromArgs(hero, args.getFlag('p'), hero.getProfessions());
            profession.getAttachedLevel().removeExp(exp);
            sender.sendMessage(ChatColor.RED + "Du hast " + ChatColor.AQUA +
                    hero.getName() + "'s " + ChatColor.RED + "Spezialisierung " + ChatColor.AQUA + profession.getName()
                    + exp + "xp" + ChatColor.RED + " entfernt.");
            hero.sendMessage(ChatColor.RED + "Ein Admin hat deiner Spezialisierung " + ChatColor.AQUA + profession.getFriendlyName()
                    + " " + exp + "exp" + ChatColor.RED + " entfernt.");
        }
        if (args.hasFlag('s')) {
            Skill skill = SkillUtil.getSkillFromArgs(hero, args.getFlag('s'));
            if (skill.isLevelable()) {
                ((Levelable) skill).getAttachedLevel().removeExp(exp);
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
                    +exp + "exp" + ChatColor.RED + " entfernt.");
        }
    }

    @Command(
            aliases = {"setlevel", "setlvl", "sl"},
            desc = "Sets level to the hero, prof or skill of the player",
            usage = "<player> [-p <prof>] [-s <skill>] [-h] <level>",
            flags = "p:s:h",
            min = 2
    )
    @CommandPermissions("rcskills.admin.level.set")
    public void setLevel(CommandContext args, CommandSender sender) throws CommandException {

        Hero hero = plugin.getCharacterManager().getHero(UUIDUtil.convertPlayer(args.getString(0)));
        int level = args.getInteger(1);
        if (args.hasFlag('h')) {
            hero.getAttachedLevel().setLevel(level);
            sender.sendMessage(ChatColor.GREEN + "Du hast " + ChatColor.AQUA +
                    hero.getName() + " " + level + " level" + ChatColor.GREEN + " hinzugefügt.");
            hero.sendMessage(ChatColor.GREEN + "Ein Admin hat dir " + ChatColor.AQUA
                    + " " + level + " level" + ChatColor.GREEN + " hinzugefügt.");
        }
        if (args.hasFlag('p')) {
            Profession profession = ProfessionUtil.getProfessionFromArgs(hero, args.getFlag('p'), plugin.getProfessionManager().getAllProfessions(hero));
            profession.getAttachedLevel().setLevel(level);
            sender.sendMessage(ChatColor.GREEN + "Du hast " + ChatColor.AQUA +
                    hero.getName() + "'s " + ChatColor.GREEN + "Spezialisierung " + ChatColor.AQUA + profession.getName()
                    + level + " level" + ChatColor.GREEN + " hinzugefügt.");
            hero.sendMessage(ChatColor.GREEN + "Ein Admin hat deiner Spezialisierung " + ChatColor.AQUA + profession.getFriendlyName()
                    + " " + level + " level" + ChatColor.GREEN + " hinzugefügt.");
        }
        if (args.hasFlag('s')) {
            Skill skill = SkillUtil.getSkillFromArgs(hero, args.getFlag('s'));
            if (skill.isLevelable()) {
                ((Levelable) skill).getAttachedLevel().setLevel(level);
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

        Hero hero = plugin.getCharacterManager().getHero(UUIDUtil.convertPlayer(args.getString(0)));
        int level = args.getInteger(1);
        if (args.hasFlag('h')) {
            hero.getAttachedLevel().addLevel(level);
            sender.sendMessage(ChatColor.GREEN + "Du hast " + ChatColor.AQUA +
                    hero.getName() + " " + level + " level" + ChatColor.GREEN + " hinzugefügt.");
            hero.sendMessage(ChatColor.GREEN + "Ein Admin hat dir " + ChatColor.AQUA
                    + " " + level + " level" + ChatColor.GREEN + " hinzugefügt.");
        }
        if (args.hasFlag('p')) {
            Profession profession = ProfessionUtil.getProfessionFromArgs(hero, args.getFlag('p'), plugin.getProfessionManager().getAllProfessions(hero));
            profession.getAttachedLevel().addLevel(level);
            sender.sendMessage(ChatColor.GREEN + "Du hast " + ChatColor.AQUA +
                    hero.getName() + "'s " + ChatColor.GREEN + "Spezialisierung " + ChatColor.AQUA + profession.getName()
                    + level + " level" + ChatColor.GREEN + " hinzugefügt.");
            hero.sendMessage(ChatColor.GREEN + "Ein Admin hat deiner Spezialisierung " + ChatColor.AQUA + profession.getFriendlyName()
                    + " " + level + " level" + ChatColor.GREEN + " hinzugefügt.");
        }
        if (args.hasFlag('s')) {
            Skill skill = SkillUtil.getSkillFromArgs(hero, args.getFlag('s'));
            if (skill.isLevelable()) {
                ((Levelable) skill).getAttachedLevel().addLevel(level);
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

        Hero hero = plugin.getCharacterManager().getHero(UUIDUtil.convertPlayer(args.getString(0)));
        int level = args.getInteger(1);
        if (args.hasFlag('h')) {
            hero.getAttachedLevel().removeLevel(level);
            sender.sendMessage(ChatColor.RED + "Du hast " + ChatColor.AQUA +
                    hero.getName() + " " + level + "level" + ChatColor.RED + " entfernt.");
            hero.sendMessage(ChatColor.RED + "Ein Admin hat dir " + ChatColor.AQUA
                    + " " + level + "level" + ChatColor.RED + " entfernt.");
        }
        if (args.hasFlag('p')) {
            Profession profession = ProfessionUtil.getProfessionFromArgs(hero, args.getFlag('p'), plugin.getProfessionManager().getAllProfessions(hero));
            profession.getAttachedLevel().removeLevel(level);
            sender.sendMessage(ChatColor.RED + "Du hast " + ChatColor.AQUA +
                    hero.getName() + "'s " + ChatColor.RED + "Spezialisierung " + ChatColor.AQUA + profession.getName()
                    + level + "level" + ChatColor.RED + " entfernt.");
            hero.sendMessage(ChatColor.RED + "Ein Admin hat deiner Spezialisierung " + ChatColor.AQUA + profession.getFriendlyName()
                    + " " + level + "level" + ChatColor.RED + " entfernt.");
        }
        if (args.hasFlag('s')) {
            Skill skill = SkillUtil.getSkillFromArgs(hero, args.getFlag('s'));
            if (skill.isLevelable()) {
                ((Levelable) skill).getAttachedLevel().removeLevel(level);
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
            Hero hero = plugin.getCharacterManager().getHero(UUIDUtil.convertPlayer(args.getString(0)));
            if (args.hasFlag('f')) {
                purgeHero(sender, hero);
            } else {
                new QueuedCaptchaCommand(sender, this, "purgeHero", sender, hero);
            }
        } catch (NoSuchMethodException e) {
            throw new CommandException(e.getMessage());
        }
    }

    private void purgeHero(CommandSender sender, Hero hero) {

        // kick the player if he is online
        // kicking will clear the cache of that player
        if (hero.getPlayer() != null) {
            hero.getPlayer().kickPlayer("Dein RPG Profil wird zurück gesetzt bitte warte kurz.");
        }
        plugin.getCharacterManager().clearCacheOf(hero);
        // this will delete all references to the object
        THero tHero = RaidCraft.getDatabase(SkillsPlugin.class).find(THero.class, hero.getId());
        if (tHero != null) RaidCraft.getDatabase(SkillsPlugin.class).delete(tHero);
        // remove the player from cache
        sender.sendMessage(ChatColor.GREEN + "Alle Daten von " + hero.getName() + " wurden erfolgreich gelöscht.");
    }

    @Command(
            aliases = "kick",
            desc = "Kicks the player and clears his cache",
            min = 1
    )
    @CommandPermissions("rcskills.admin.kick")
    public void kick(CommandContext args, CommandSender sender) throws CommandException {

        final Hero hero = plugin.getCharacterManager().getHero(UUIDUtil.convertPlayer(args.getString(0)));
        hero.getPlayer().kickPlayer("Dein RPG Profil Cache wird geleert.");
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {

                plugin.getCharacterManager().clearCacheOf(hero);
            }
        }, 5L);
    }

    @Command(
            aliases = "reset",
            desc = "Resets all effects and cooldowns of the player.",
            flags = "p:ecr"
    )
    @CommandPermissions("rcskills.admin.reset")
    public void reset(CommandContext args, CommandSender sender) {

        Hero hero;
        if (args.hasFlag('p')) {
            hero = plugin.getCharacterManager().getHero(UUIDUtil.convertPlayer(args.getFlag('p')));
            sender.sendMessage(ChatColor.GREEN + "Die " +
                            (args.hasFlag('r') ? "Resourcen " : "") +
                            (args.hasFlag('e') ? " Effekte " : "") +
                            (args.hasFlag('c') ? " Cooldowns " : "") +
                            "von " + hero.getName() + " wurden zurückgesetzt."
            );
        } else {
            hero = plugin.getCharacterManager().getHero((Player) sender);
        }
        // reset all resources
        if (args.hasFlag('r')) {
            hero.reset();
            hero.sendMessage(ChatColor.AQUA + "Alle Resourcen (inkl. Leben) wurden zurückgesetzt.");
        }
        if (args.hasFlag('e')) {
            hero.clearEffects();
            hero.sendMessage(ChatColor.AQUA + "Alle Effekte wurden entfernt.");
        }
        // reset all skill cooldowns
        if (args.hasFlag('c')) {
            hero.getSkills().stream().filter(Skill::isOnCooldown).forEach(s -> {
                s.setLastCast(null);
                hero.sendMessage(ChatColor.AQUA + "Cooldown von " + s + " wurde zurückgesetzt.");
            });
        }
    }

    @Command(
            aliases = {"cleanup", "prune", "clean"},
            desc = "Cleans the database from old skills and professions.",
            flags = "sp"
    )
    @CommandPermissions("rcskills.admin.cleanup")
    public void cleanup(CommandContext args, CommandSender sender) throws CommandException {

        EbeanServer database = plugin.getDatabase();
        if (!args.hasFlag('s') && !args.hasFlag('p')) {
            throw new CommandException("You need to specify at least one flag: -p to clean professions and -s to clean skills");
        }
        if (args.hasFlag('s')) {
            Set<String> loadedSkills = plugin.getSkillManager().getSkillFactories().keySet();
            List<THeroSkill> databaseSkills = database.find(THeroSkill.class).findList();
            List<THeroSkill> obsoleteSkills = databaseSkills.stream()
                    .filter(dbSkill -> !loadedSkills.contains(dbSkill.getName()))
                    .collect(Collectors.toList());
            sender.sendMessage(ChatColor.DARK_RED + "The following skills would be deleted: ");
            String distinctSkills = obsoleteSkills.stream()
                    .map(THeroSkill::getName)
                    .distinct()
                    .collect(Collectors.joining(","));
            sender.sendMessage(ChatColor.YELLOW + distinctSkills);
            sender.sendMessage(ChatColor.DARK_RED + "The following players would be affected: ");
            String heroes = obsoleteSkills.stream()
                    .filter(tHeroSkill -> tHeroSkill.getHero() != null)
                    .map(skill -> skill.getHero().getPlayer())
                    .distinct()
                    .limit(20)
                    .collect(Collectors.joining(","));
            sender.sendMessage(ChatColor.AQUA + heroes);
            try {
                new QueuedCaptchaCommand(sender, this, "pruneDatabase", obsoleteSkills);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                throw new CommandException(e.getMessage());
            }
        } else if (args.hasFlag('p')) {
            Set<String> loadedProfessions = plugin.getProfessionManager().getProfessionFactories().keySet();
            List<THeroProfession> databaseProfessions = database.find(THeroProfession.class).findList();
            List<THeroProfession> obsoleteProfessions = databaseProfessions.stream()
                    .filter(dbProfession -> !loadedProfessions.contains(dbProfession.getName()))
                    .collect(Collectors.toList());
            sender.sendMessage(ChatColor.DARK_RED + "The following professions would be deleted: ");
            String distinctSkills = obsoleteProfessions.stream()
                    .map(THeroProfession::getName)
                    .distinct()
                    .collect(Collectors.joining(","));
            sender.sendMessage(ChatColor.RED + distinctSkills);
            sender.sendMessage(ChatColor.DARK_RED + "The following players would be affected: ");
            String heroes = obsoleteProfessions.stream()
                    .map(professions -> professions.getHero().getPlayer())
                    .distinct()
                    .limit(20)
                    .collect(Collectors.joining(","));
            sender.sendMessage(ChatColor.AQUA + heroes);
            sender.sendMessage(ChatColor.DARK_RED + "The following skills would also be deleted: ");
            String skills = obsoleteProfessions.stream()
                    .map(THeroProfession::getSkills)
                    .flatMap(Collection::stream)
                    .map(THeroSkill::getName)
                    .distinct()
                    .collect(Collectors.joining(","));
            sender.sendMessage(ChatColor.YELLOW + skills);
            try {
                new QueuedCaptchaCommand(sender, this, "pruneDatabase", obsoleteProfessions);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                throw new CommandException(e.getMessage());
            }
        }
    }

    private void pruneDatabase(Collection<Object> elements) {

        plugin.getDatabase().delete(elements);
    }
}
