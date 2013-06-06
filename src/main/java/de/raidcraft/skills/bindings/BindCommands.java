package de.raidcraft.skills.bindings;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.CommandTriggered;
import de.raidcraft.skills.util.SkillUtil;
import de.raidcraft.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BindCommands {

    private final SkillsPlugin plugin;

    public BindCommands(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = "bind",
            desc = "Binds a skill to an item",
            flags = "x"
    )
    @CommandPermissions("rcskills.player.bind")
    public void bind(CommandContext args, CommandSender sender) throws CommandException {

        if (!(sender instanceof Player)) {
            return;
        }

        Hero hero;
        hero = plugin.getCharacterManager().getHero((Player) sender);

        if (hero.getPlayer().getItemInHand() == null || hero.getPlayer().getItemInHand().getTypeId() == 0) {
            throw new CommandException("Du kannst Skills nur an Items binden.");
        }

        BindManager bindManager = RaidCraft.getComponent(SkillsPlugin.class).getBindManager();
        if (args.hasFlag('x')) {
            if (bindManager.addBinding(hero, hero.getItemTypeInHand(), null)) {
                hero.sendMessage(ChatColor.DARK_GREEN + "Du hast das Item erfolgreich mit einem Platzhalter belegt.");
                return;
            }
            throw new CommandException("Du hast bereits einen Platzhalter an dieses Item gebunden.");
        }

        if (args.argsLength() < 1) {
            throw new CommandException("Du musst einen Skill angeben, den du binden willst: /bind <skill>");
        }

        // lets parse the argument for a valid spell
        Skill skill = SkillUtil.getSkillFromArgs(hero, args.getString(0));

        if (!(skill instanceof CommandTriggered)) {
            throw new CommandException("Du kannst diesen Skill nicht binden.");
        }

        if (bindManager.addBinding(hero, hero.getPlayer().getItemInHand().getType(), skill, new CommandContext(args.getSlice(1)))) {
            hero.sendMessage(ChatColor.DARK_GREEN + "Der Skill " + skill.getFriendlyName() + " wurde an dieses Item gebunden!");
            return;
        }
        throw new CommandException("Dieser Skill ist bereits an dieses Item gebunden!");
    }

    @Command(
            aliases = "unbind",
            desc = "Unbind all skills on an item",
            flags = "a"
    )
    @CommandPermissions("rcskills.player.unbind")
    public void unbind(CommandContext args, CommandSender sender) throws CommandException {

        if (!(sender instanceof Player)) {
            return;
        }

        Hero hero;
        hero = plugin.getCharacterManager().getHero((Player) sender);

        BindManager bindManager = plugin.getBindManager();
        if (args.hasFlag('a')) {
            // unbind all spells
            for (BoundItem item : new ArrayList<>(bindManager.getBoundItems(hero.getName()))) {
                bindManager.removeBindings(hero, item.getItem());
            }
            hero.sendMessage(ChatColor.DARK_GREEN + "Alle deine gebunden Skills wurden von den Items entfernt.");
            return;
        }

        if (hero.getPlayer().getItemInHand() == null) {
            throw new CommandException("Kein Item in der Hand.");
        }

        if (bindManager.removeBindings(hero, hero.getPlayer().getItemInHand().getType())) {
            hero.sendMessage(ChatColor.DARK_GREEN + "Alle Skills auf diesem Item wurden entfernt.");
            return;
        }
        throw new CommandException("An dieses Item sind keine Skills gebunden!");
    }

    @Command(
            aliases = "autobind",
            desc = "Autobind all skills to items"
    )
    @CommandPermissions("rcskills.player.unbind")
    public void autobind(CommandContext args, CommandSender sender) throws CommandException {

        if (!(sender instanceof Player)) {
            return;
        }

        int usedSlots = 0;
        boolean noItem = false;

        Hero hero = plugin.getCharacterManager().getHero((Player) sender);
        Map <Material, List<Skill>> assignments = new HashMap<>();
        Map<EffectType, Integer> slots = new EnumMap<>(EffectType.class);

        for (Skill skill : hero.getSkills()) {
            // lets check if the player has this skill unlocked
            if (!(skill instanceof CommandTriggered) || !skill.isUnlocked() || !skill.isActive()) {
                continue;
            }

            for (EffectType type : skill.getTypes()) {

                int slot = -1;
                if (slots.containsKey(type)) {
                    slot = slots.get(type);
                } else {
                    if (usedSlots < 9) {
                        slot = usedSlots;
                        slots.put(type, slot);
                    }
                }

                if (slot > -1) {
                    noItem = bindSkill(slot, skill, assignments);
                    usedSlots++;
                    break;
                }
            }
        }

        if (noItem) {
            hero.sendMessage(ChatColor.RED + "Du hast nicht genug Items in der Inventarleiste um alle Skilltypen zu binden!");
        }
        if (assignments.size() == 0) {
            throw new CommandException("Du hast keine Skills zum binden!");
        }

        hero.sendMessage(ChatColor.DARK_GREEN + "Es wurden folgende Skills an Items gebunden:");

        String bindingText = "";
        for (Map.Entry<Material, List<Skill>> entry : assignments.entrySet()) {
            // remove old bindings
            RaidCraft.getComponent(SkillsPlugin.class).getBindManager().removeBindings(hero, entry.getKey());
            bindingText = ChatColor.GOLD + ItemUtils.getFriendlyName(entry.getKey(), ItemUtils.Language.GERMAN) + ChatColor.WHITE + ": ";

            // add new binding
            boolean colorToggle = true;
            for (Skill sk : entry.getValue()) {
                RaidCraft.getComponent(SkillsPlugin.class).getBindManager().addBinding(hero, entry.getKey(), sk);
                if (colorToggle) {
                    bindingText += ChatColor.WHITE;
                    colorToggle = false;
                } else {
                    bindingText += ChatColor.DARK_GRAY;
                    colorToggle = true;
                }
                bindingText += sk.getFriendlyName() + ", ";
            }
            hero.sendMessage(bindingText);
        }
    }

    private boolean bindSkill(int slot, Skill skill, Map<Material, List<Skill>> boundSkills) {

        ItemStack item = skill.getHolder().getPlayer().getInventory().getItem(slot);
        if (item == null || item.getTypeId() == 0) {
            return true;
        }
        if (!boundSkills.containsKey(item.getType())) {
            boundSkills.put(item.getType(), new ArrayList<Skill>());
        }
        boundSkills.get(item.getType()).add(skill);
        return false;
    }
}
