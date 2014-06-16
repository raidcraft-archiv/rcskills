package de.raidcraft.skills.api.effect.types;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.util.ConfigUtil;
import de.raidcraft.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Silthus
 */
public abstract class DelayedExpirableEffect<S> extends ScheduledEffect<S> {

    protected long delay = 0;
    protected long duration = 0;
    private BukkitTask removalTask = null;

    public DelayedExpirableEffect(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        load(data);
    }

    private void load(EffectData data) {

        if (getSource() instanceof Skill) {
            delay = TimeUtil.secondsToTicks(ConfigUtil.getTotalValue((Skill) getSource(), data.getEffectDelay()));
            duration = TimeUtil.secondsToTicks(ConfigUtil.getTotalValue((Skill) getSource(), data.getEffectDuration()));
        } else {
            delay = TimeUtil.secondsToTicks(data.getEffectDelay().getInt("base", 0));
            duration = TimeUtil.secondsToTicks(data.getEffectDuration().getInt("base", 0));
        }
    }

    public BukkitTask getRemovalTask() {

        return removalTask;
    }

    public void setRemovalTask(BukkitTask task) {

        this.removalTask = task;
    }

    @Override
    public void apply() throws CombatException {

        if (!isRemovalScheduled() && !isStarted()) {
            startTask();
        } else if (!isRemovalScheduled() && isStarted()) {
            // stop the applying task
            stopTask();
            // stop the removal task
            stopRemovalTask();
            // start the task that will the remove the applied effect
            setRemovalTask(Bukkit.getScheduler().runTaskLater(
                    RaidCraft.getComponent(SkillsPlugin.class),
                    this,
                    getDuration()
            ));
            // apply the effect
            super.apply();
        }
    }

    private boolean isRemovalScheduled() {

        return removalTask != null && !isStarted();
    }

    @Override
    public void startTask() {

        if (!isStarted()) {
            // schedule the task that delays the applying of the effect
            setTask(Bukkit.getScheduler().runTaskLater(
                    RaidCraft.getComponent(SkillsPlugin.class),
                    this,
                    getDelay()
            ));
        }
        if (!isRemovalScheduled()) {
            // also schedule the task that removes the effect again
            setRemovalTask(Bukkit.getScheduler().runTaskLater(
                    RaidCraft.getComponent(SkillsPlugin.class),
                    this,
                    getDelay() + getDuration()
            ));
        }
    }

    private void stopRemovalTask() {

        if (removalTask != null) {
            removalTask.cancel();
            removalTask = null;
        }
    }

    public long getDuration() {

        return duration;
    }

    public long getDelay() {

        return delay;
    }

    @Override
    public void remove() throws CombatException {

        if (isStarted() && !isRemovalScheduled()) {
            // the effect has not been applied yet
            stopTask();
            stopRemovalTask();
        } else if (isRemovalScheduled()) {
            // remove the effect before it is scheduled to be removed
            stopRemovalTask();
            super.remove();
        }
    }

    @Override
    public void renew() throws CombatException {

        if (isStarted() && !isRemovalScheduled()) {
            // reapply the apply countdown
            stopTask();
            stopRemovalTask();
            startTask();
        } else if (isRemovalScheduled()) {
            // reapply the removal task
            stopRemovalTask();
            setRemovalTask(Bukkit.getScheduler().runTaskLater(
                    RaidCraft.getComponent(SkillsPlugin.class),
                    this,
                    getDuration()
            ));
            super.renew();
        }
    }

    @Override
    public void run() {

        try {
            if (!isRemovalScheduled() && isStarted()) {
                // lets apply the effect
                // stop the start task
                stopTask();
                super.apply();
            } else if (isRemovalScheduled()) {
                stopRemovalTask();
                super.remove();
            }
        } catch (CombatException e) {
            warn(e.getMessage());
        }
    }
}
