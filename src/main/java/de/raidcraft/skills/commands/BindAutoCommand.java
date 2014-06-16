package de.raidcraft.skills.commands;

import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.CommandTriggered;
import de.raidcraft.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Auto bind all skills to items.
 */
public class BindAutoCommand implements CommandExecutor {

    private final SkillsPlugin plugin;

    public BindAutoCommand(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    /**
     * Executes the command to auto bind all skills to items.
     *
     * @param sender The source of the command
     * @param cmd    The command which was executed
     * @param label  The alias of the command which was used
     * @param args   Passed command arguments
     *
     * @return true if success, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler können dieses Befehl nutzen.");
            return true;
        }

        Player player = (Player) sender;
        Hero hero = plugin.getCharacterManager().getHero(player);
        Map<Material, List<Skill>> assignments = new HashMap<>();
        Map<EffectType, Integer> slots = new EnumMap<>(EffectType.class);
        int usedSlots = 0;
        int usedItems = 0;
        boolean noItem = false;

        for (Skill skill : hero.getSkills()) {
            // lets check if the player has this skill unlocked
            if (!(skill instanceof CommandTriggered) || !skill.isUnlocked() || !skill.isActive() || !skill.getSkillProperties().isCastable()) {
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
                    if (!noItem) usedItems++;
                    usedSlots++;
                    break;
                }
            }
        }

        if (assignments.size() == 0 && !noItem) {

            player.sendMessage(ChatColor.YELLOW + "Du verfügst über keine Fähigkeiten die gebunden werden können.");
            return true;
        }
        if (usedItems < 1) {

            player.sendMessage(ChatColor.YELLOW + "Du benötigst Gegenstände in der Inventarleise um Fähigkeiten automatisch binden zu können.");
            return true;
        }
        if (noItem) {
            player.sendMessage(ChatColor.YELLOW + "Du hast nicht genug Gegenstände in der Inventarleise um alle Fähigkeiten zu binden.");
        }

        player.sendMessage(ChatColor.DARK_GREEN + "Folgende Fähigkeiten konnten erfolgreich an Gegenstände gebunden werden:");
        StringBuilder stringBuilder = new StringBuilder();
        boolean colorToggle = false;

        for (Map.Entry<Material, List<Skill>> entry : assignments.entrySet()) {

            hero.getBindings().remove(entry.getKey());
            stringBuilder.append(ChatColor.GOLD);
            stringBuilder.append(ItemUtils.getFriendlyName(entry.getKey(), ItemUtils.Language.GERMAN));
            stringBuilder.append(ChatColor.YELLOW);
            stringBuilder.append(": ");

            Skill lastSkill = entry.getValue().get(entry.getValue().size() - 1);
            colorToggle = !colorToggle;
            for (Skill skill : entry.getValue()) {

                hero.getBindings().add(entry.getKey(), skill, null);
                colorToggle = !colorToggle;
                stringBuilder.append(colorToggle ? ChatColor.GRAY : ChatColor.DARK_GRAY);
                stringBuilder.append(skill.getFriendlyName());
                if (!lastSkill.equals(skill)) {
                    stringBuilder.append(ChatColor.YELLOW);
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append("\n");
        }

        player.sendMessage(stringBuilder.toString());

        return true;
    }

    private boolean bindSkill(int slot, Skill skill, Map<Material, List<Skill>> boundSkills) {

        ItemStack item = skill.getHolder().getPlayer().getInventory().getItem(slot);
        if (item == null || item.getType().equals(Material.AIR)) {
            return true;
        }
        if (!boundSkills.containsKey(item.getType())) {
            boundSkills.put(item.getType(), new ArrayList<>());
        }
        boundSkills.get(item.getType()).add(skill);
        return false;
    }
}
