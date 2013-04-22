package de.raidcraft.skills.api.effect.common;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.effect.types.DelayedEffect;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Casttime",
        description = "Keeps track of the casttime for a char template",
        priority = -1.0
)
public class CastTime extends DelayedEffect<SkillAction> {

    private BukkitTask castBarTask;
    private boolean casted = false;

    public CastTime(SkillAction source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        setPriority(-1.0);
        delay = source.getCastTime();
        debug("started timer");
    }

    @Override
    public void apply() throws CombatException {

        super.apply();
        getSource().getSource().sendMessage("" + ChatColor.GRAY + ChatColor.ITALIC +
                "Wirke Zauber " + ChatColor.AQUA + getSource().getSkill().getFriendlyName()
                + ChatColor.GRAY + " in " + TimeUtil.ticksToSeconds(getDelay()) + "s");

        final long endTime = System.currentTimeMillis() + TimeUtil.secondsToMillis(TimeUtil.ticksToSeconds(delay));

        if (getTarget().getEntity() instanceof Player) {
            castBarTask = Bukkit.getScheduler().runTaskTimer(RaidCraft.getComponent(SkillsPlugin.class), new Runnable() {
                @Override
                public void run() {

                    try {
                        long currentTime = System.currentTimeMillis();
                        if (currentTime >= endTime) {
                            remove();
                            return;
                        }
                        ((Player) getTarget().getEntity()).setExp((currentTime / endTime));
                    } catch (CombatException e) {
                        castBarTask.cancel();
                    }
                }
            }, 1L, 1L);
        }
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

        if (castBarTask != null) {
            castBarTask.cancel();
            castBarTask = null;
        }
        if (!casted) {
            warn(getSource().getSource(), "Zauber " + getSource().getSkill().getFriendlyName() + " wurde unterbrochen.");
        }
    }
}
