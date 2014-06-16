package de.raidcraft.skills.bindings;

import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Philip
 */
public class BindListener implements Listener {

    private final SkillsPlugin plugin;

    public BindListener(SkillsPlugin plugin) {

        this.plugin = plugin;

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Hero hero = plugin.getCharacterManager().getHero(player);
        Material material = player.getItemInHand().getType();

        if (hero.getBindings().isEmpty() || material == null || material.isBlock())
            return;


        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {

            if (Material.BOW.equals(material)) {

                switchBoundSkill(hero, material, !player.isSneaking());
            } else {

                use(hero, hero.getBindings().getSkillAction(material));
            }

            event.setCancelled(true);
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if (Material.BOW.equals(material)) {

                use(hero, hero.getBindings().getSkillAction(material));
                event.setCancelled(event.getAction() == Action.RIGHT_CLICK_BLOCK);
            } else {

                switchBoundSkill(hero, material, !player.isSneaking());
                event.setCancelled(true);
            }
        }
    }

    private void use(Hero hero, SkillAction skillAction) {

        if (skillAction == null)
            return;

        try {

            skillAction.run();

        } catch (CombatException e) {

            // dont spam the player with global cooldown
            if (e.getType() == CombatException.Type.ON_GLOBAL_COOLDOWN) {

                return;
            }
            hero.sendMessage(ChatColor.RED + e.getMessage());
        }
    }

    private void switchBoundSkill(Hero hero, Material material, boolean forward) {

        SkillAction newSkillAction = hero.getBindings().switchSkill(material, forward);

        if (newSkillAction != null) {
            hero.sendMessage(ChatColor.DARK_GRAY + "Gewählter Skill: " + newSkillAction.getSkill().getFriendlyName());
        }

    }
}
