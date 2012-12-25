package de.raidcraft.skills.api.effect.common;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.attack.SkillAction;
import de.raidcraft.skills.api.effect.DelayedEffect;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.util.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Casttime",
        description = "Keeps track of the casttime for a char template",
        priority = -1.0
)
public class CastTime extends DelayedEffect<SkillAction> implements Listener {

    private boolean casted = false;

    public CastTime(SkillAction source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        setPriority(-1.0);
        delay = source.getSkill().getTotalCastTime();
        RaidCraft.getComponent(SkillsPlugin.class).registerEvents(this);
        debug("started timer");
    }

    @Override
    public void apply() throws CombatException {

        super.apply();
        getSource().getSource().sendMessage("" + ChatColor.GRAY + ChatColor.ITALIC +
                "Wirke Zauber " + ChatColor.AQUA + getSource().getSkill().getFriendlyName()
                + ChatColor.GRAY + " in " + TimeUtil.ticksToSeconds(getDelay()) + "s");
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        casted = true;
        remove();
        getSource().run();
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

        // nothing we need to do here
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        if (!casted) {
            warn(getSource().getSource(), "Zauber " + getSource().getSkill().getFriendlyName() + " wurde unterbrochen.");
        }
        PlayerInteractEvent.getHandlerList().unregister(this);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {

        if (isStarted()) {
            try {
                remove();
            } catch (CombatException e) {
                warn(getSource().getSource(), e.getMessage());
            }
        }
    }
}
