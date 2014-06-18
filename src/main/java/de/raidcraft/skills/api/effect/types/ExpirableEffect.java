package de.raidcraft.skills.api.effect.types;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.util.ConfigUtil;
import de.raidcraft.util.TimeUtil;
import org.bukkit.Bukkit;

/**
 * @author Silthus
 */
public abstract class ExpirableEffect<S> extends ScheduledEffect<S> {

    protected long duration = 0;
    private long startTime;

    public ExpirableEffect(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        load(data);
    }

    private void load(EffectData data) {

        if (getSource() instanceof Skill) {
            duration = TimeUtil.secondsToTicks(ConfigUtil.getTotalValue((Skill) getSource(), data.getEffectDuration()));
        } else {
            duration = TimeUtil.secondsToTicks(data.getEffectDuration().getInt("base", 0));
        }
    }

    public double getRemainingDuration() {

        long runningTimeMillis = System.currentTimeMillis() - startTime;
        return TimeUtil.ticksToSeconds(getDuration()) - TimeUtil.millisToSeconds(runningTimeMillis);
    }

    public long getDuration() {

        return duration;
    }

    public void setDuration(double time) {

        duration = TimeUtil.secondsToTicks(time);
        stopTask();
        startTask();
    }

    @Override
    public void startTask() {

        // lets run a task to remove this effect after the given duration
        setTask(Bukkit.getScheduler().runTaskLater(
                RaidCraft.getComponent(SkillsPlugin.class),
                this,
                getDuration()));
        startTime = System.currentTimeMillis();
    }

    @Override
    public void apply() throws CombatException {

        startTask();
        super.apply();
        // lets add the effect to the display
        if (getTarget() instanceof Hero) {
            ((Hero) getTarget()).getUserInterface().addEffect(this, (int) TimeUtil.ticksToSeconds(getDuration()));
        }
    }

    @Override
    public void remove() throws CombatException {

        if (isStarted()) {
            stopTask();
            super.remove();
            // lets remove the effect from the display
            if (getTarget() instanceof Hero) {
                ((Hero) getTarget()).getUserInterface().removeEffect(this);
            }
        }
    }

    @Override
    public void renew() throws CombatException {

        stopTask();
        startTask();
        super.renew();
    }

    @Override
    public void run() {

        try {
            // this is called when the task is scheduled to be removed
            remove();
        } catch (CombatException e) {
            warn(e.getMessage());
        }
    }
}
