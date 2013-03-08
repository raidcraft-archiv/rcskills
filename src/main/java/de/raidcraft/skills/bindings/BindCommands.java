package de.raidcraft.skills.bindings;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
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
            min = 1
    )
    @CommandPermissions("rcskills.player.bind")
    public void bind(CommandContext args, CommandSender sender) throws CommandException {

        if (!(sender instanceof Player)) {
            return;
        }

        Hero hero;
        hero = plugin.getCharacterManager().getHero((Player) sender);

        if(hero.getPlayer().getItemInHand() == null) {
            throw new CommandException("Kein Item in der Hand.");
        }

        // lets parse the argument for a valid spell
        Skill skill = SkillUtil.getSkillFromArgs(hero, args.getString(0));

        if (!(skill instanceof CommandTriggered)) {
            throw new CommandException("Du kannst diesen Skill nicht binden.");
        }

        if(BindManager.INST.addBinding(hero, hero.getPlayer().getItemInHand().getType(), skill)) {
            hero.sendMessage(ChatColor.DARK_GREEN + "Der Skill " + skill.getFriendlyName() + " wurde an dieses Item gebunden!");
            return;
        }
        throw new CommandException("Dieser Skill ist bereits an dieses Item gebunden!");
    }

    @Command(
            aliases = "unbind",
            desc = "Unbind all skills on an item"
    )
         @CommandPermissions("rcskills.player.unbind")
         public void unbind(CommandContext args, CommandSender sender) throws CommandException {

        if (!(sender instanceof Player)) {
            return;
        }

        Hero hero;
        hero = plugin.getCharacterManager().getHero((Player) sender);

        if(hero.getPlayer().getItemInHand() == null) {
            throw new CommandException("Kein Item in der Hand.");
        }

        if(BindManager.INST.removeBindings(hero, hero.getPlayer().getItemInHand().getType())) {
            hero.sendMessage(ChatColor.DARK_GREEN + "Alle Skills auf diesem Item wurden entfernt!");
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

        Hero hero;
        hero = plugin.getCharacterManager().getHero((Player) sender);
        boolean noItem = false;
        Map<Material, List<Skill>> assignments = new HashMap<>();
        for(Skill skill : hero.getSkills()) {
            if (!(skill instanceof CommandTriggered)) {
                continue;
            }

            ItemStack item;
            for(EffectType type : skill.getTypes()) {
                // damage spells (item in slot 0)
                if(type == EffectType.DAMAGING || type == EffectType.HARMFUL) {
                    item = hero.getPlayer().getInventory().getItem(0);
                    if(item != null && item.getType() != Material.AIR) {
                        if(!assignments.containsKey(item.getType())) {
                            assignments.put(item.getType(), new ArrayList<Skill>());
                        }
                        assignments.get(item.getType()).add(skill);
                    }
                    else {
                        noItem = true;
                    }
                    break;
                }

                // protection spells (item in slot 1)
                if(type == EffectType.PROTECTION) {
                    item = hero.getPlayer().getInventory().getItem(1);
                    if(item != null && item.getType() != Material.AIR) {
                        if(!assignments.containsKey(item.getType())) {
                            assignments.put(item.getType(), new ArrayList<Skill>());
                        }
                        assignments.get(item.getType()).add(skill);
                    }
                    else {
                        noItem = true;
                    }
                    break;
                }

                // helpful spells (item in slot 2)
                if(type == EffectType.HELPFUL) {
                    item = hero.getPlayer().getInventory().getItem(2);
                    if(item != null && item.getType() != Material.AIR) {
                        if(!assignments.containsKey(item.getType())) {
                            assignments.put(item.getType(), new ArrayList<Skill>());
                        }
                        assignments.get(item.getType()).add(skill);
                    }
                    else {
                        noItem = true;
                    }
                    break;
                }
            }
        }

        if(noItem) {
            hero.sendMessage(ChatColor.RED + "Du hast nicht genug Items in der Inventarleiste um alle Skilltypen zu binden!");
        }
        if(assignments.size() == 0) {
            throw new CommandException("Du hast keine Skills zum binden!");
        }

        hero.sendMessage(ChatColor.DARK_GREEN + "Es wurden folgende Skills an Items gebunden:");

        String bindingText = "";
        for(Map.Entry<Material, List<Skill>> entry : assignments.entrySet()) {
            // remove old bindings
            BindManager.INST.removeBindings(hero, entry.getKey());
            bindingText = ChatColor.GOLD + ItemUtils.getFriendlyName(entry.getKey(), ItemUtils.Language.GERMAN) + ChatColor.WHITE + ": ";

            // add new binding
            boolean colorToggle = true;
            for(Skill sk : entry.getValue()) {
                BindManager.INST.addBinding(hero, entry.getKey(), sk);
                if(colorToggle) {
                    bindingText += ChatColor.WHITE;
                    colorToggle = false;
                }
                else {
                    bindingText += ChatColor.DARK_GRAY;
                    colorToggle = true;
                }
                bindingText += sk.getFriendlyName() + ", ";
            }
            hero.sendMessage(bindingText);
        }
    }
}
