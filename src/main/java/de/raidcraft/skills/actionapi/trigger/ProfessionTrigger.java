package de.raidcraft.skills.actionapi.trigger;

import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.api.events.RCPlayerChangedProfessionEvent;
import de.raidcraft.skills.api.events.RCProfessionLevelEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author mdoering
 */
public class ProfessionTrigger extends Trigger implements Listener {

    public ProfessionTrigger() {

        super("profession", "level", "change");
    }

    @Information(
            value = "profession.level",
            desc = "Is triggered when the hero levels the given or any profession.",
            conf = {
                    "level: [minimal level of the profession]",
                    "profession: [profession that is leveled]",
            }
    )
    @EventHandler(ignoreCancelled = true)
    public void onLevelUp(RCProfessionLevelEvent event) {

        informListeners("level", event.getHero().getPlayer(), config -> {
            if (config.isSet("profession")) {
                if (!event.getSource().getName().equalsIgnoreCase(config.getString("profession"))) {
                    return false;
                }
            }
            if (config.isSet("level")) {
                if (event.getNewLevel() < config.getInt("level")) {
                    return false;
                }
            }
            return true;
        });
    }

    @Information(
            value = "profession.change",
            desc = "Is triggered when the hero changed the profession.",
            conf = {
                    "profession: [profession that was changed to]",
            }
    )
    @EventHandler(ignoreCancelled = true)
    public void onUnlock(RCPlayerChangedProfessionEvent event) {

        informListeners("change", event.getPlayer(), config -> {
            if (config.isSet("profession")) {
                if (!event.getNewProfession().equalsIgnoreCase(config.getString("profession"))) {
                    return false;
                }
            }
            return true;
        });
    }
}
