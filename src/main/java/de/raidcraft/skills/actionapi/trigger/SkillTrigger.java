package de.raidcraft.skills.actionapi.trigger;

import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.skills.api.events.RCSkillLevelEvent;
import de.raidcraft.skills.api.skill.PlayerCastSkillEvent;
import de.raidcraft.skills.api.skill.PlayerUnlockSkillEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author mdoering
 */
public class SkillTrigger extends Trigger implements Listener {

    public SkillTrigger() {

        super("skill", "unlock", "level", "use");
    }

    @Information(
            value = "skill.use",
            desc = "Is triggered when the player casts the given skill.",
            conf = {
                    "skill: <displayName>"
            }
    )
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onSkillCast(PlayerCastSkillEvent event) {

        informListeners("use", event.getPlayer(),
                config -> !config.isSet("skill")
                        || event.getSkill().getName().equalsIgnoreCase(config.getString("skill"))
        );
    }

    @Information(
            value = "skill.unlock",
            desc = "Is triggered when the player unlocks the given skill.",
            conf = {
                    "skill: <displayName>"
            }
    )
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onSkillGain(PlayerUnlockSkillEvent event) {

        informListeners("unlock", event.getPlayer(),
                config -> !config.isSet("skill")
                        || event.getSkill().getName().equalsIgnoreCase(config.getString("skill"))
        );
    }

    @Information(
            value = "skill.level",
            desc = "Is triggered when the player levels the given skill above the defined level.",
            conf = {
                    "skill: <displayName>",
                    "[level]"
            }
    )
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLevelGain(RCSkillLevelEvent event) {

        informListeners("level", event.getHero().getPlayer(), config -> {

            if (config.isSet("skill") && !config.getString("skill").equalsIgnoreCase(event.getSource().getName())) {
                return false;
            }
            return !config.isSet("level") || config.getInt("level") < event.getNewLevel();
        });
    }
}
