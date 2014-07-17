package de.raidcraft.skills.actionapi.trigger;

import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.skills.api.events.RCSkillLevelEvent;
import de.raidcraft.skills.api.skill.PlayerCastSkillEvent;
import de.raidcraft.skills.api.skill.PlayerUnlockSkillEvent;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.TriggerPriority;
import de.raidcraft.skills.api.trigger.Triggered;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author mdoering
 */
public class SkillTrigger extends Trigger implements Listener, Triggered {

    public SkillTrigger() {

        super("skill", "unlock", "level", "cast");
    }

    @TriggerHandler(ignoreCancelled = true, filterTargets = false, priority = TriggerPriority.MONITOR)
    public void onSkillCast(PlayerCastSkillEvent event) {

        informListeners("cast", event.getPlayer(),
                config -> !config.isSet("skill")
                        || event.getSkill().getName().equalsIgnoreCase(config.getString("skill")));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onSkillGain(PlayerUnlockSkillEvent event) {

        informListeners("unlock", event.getPlayer(),
                config -> !config.isSet("skill")
                        || event.getSkill().getName().equalsIgnoreCase(config.getString("skill")));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLevelGain(RCSkillLevelEvent event) {

        informListeners("level", event.getHero().getPlayer(), config -> {

            if (config.isSet("skill") && !config.getString("skill").equalsIgnoreCase(event.getSource().getName())) {
                return false;
            }
            if (config.isSet("level") && !(config.getInt("level") < event.getNewLevel())) {
                return false;
            }
            return true;
        });
    }
}
