package de.raidcraft.skills.actionapi.trigger;

import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.HealAction;
import de.raidcraft.skills.api.events.RCEntityDeathEvent;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.HandlerList;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HeroTrigger extends Trigger implements Listener {

    public HeroTrigger() {
        super("hero", "death");
    }

    @Information(
            value = "hero.death",
            desc = "Called when a hero dies. Can be cancelled. Trigger will only match if it was not cancelled.",
            conf = {
                    "heal: amount in percentage of max health (auto cancels event)",
                    "source: optionale Quelle der Heilung",
                    "cancel: cancels the death event (default: false)",
                    "bypass: set to true if you want to bypass the trigger check"
            }
    )
    @EventHandler
    public void onHeroDeath(RCEntityDeathEvent event) {

        if (!(event.getCharacter() instanceof Hero)) return;

        Hero hero = (Hero) event.getCharacter();

        informListeners("death", hero.getPlayer(), config -> {
            try {
                if (config.isDouble("heal")) {
                    hero.heal(hero.getMaxHealth() * config.getDouble("heal", 1.0));
                    event.setCancelled(true);
                } else if (config.isSet("cancel")) {
                    event.setCancelled(config.getBoolean("cancel", false));
                }
                return config.getBoolean("bypass", false) || !event.isCancelled();
            } catch (CombatException e) {
                hero.sendMessage(ChatColor.RED + e.getMessage());
                return false;
            }
        });
    }
}
