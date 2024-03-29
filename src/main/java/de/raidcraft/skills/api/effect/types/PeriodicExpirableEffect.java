package de.raidcraft.skills.api.effect.types;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.IgnoredEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.util.ConfigUtil;
import de.raidcraft.util.TimeUtil;

/**
 * @author Silthus
 */
@IgnoredEffect
public abstract class PeriodicExpirableEffect<S> extends PeriodicEffect<S> {

    protected long duration;
    protected long remainingTicks = 0;

    public PeriodicExpirableEffect(S source, CharacterTemplate target, EffectData data) {

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

    @Override
    public double getPriority() {

        if (super.getPriority() < 0) {
            return super.getPriority();
        }
        return super.getPriority() + getRemainingTicks();
    }

    public long getRemainingTicks() {

        return remainingTicks;
    }

    public int getTickCount() {

        return (int) (getDuration() / getInterval());
    }

    public long getDuration() {

        return duration;
    }

    public void setDuration(double time) {

        duration = TimeUtil.secondsToTicks(time);
        if (getDuration() == duration) return;
        if (duration <= 0) {
            stopTask();
            return;
        }
        stopTask();
        startTask();
    }

    @Override
    public void startTask() {

        if (!isStarted()) {
            super.startTask();
            this.remainingTicks = getDuration();
            // add the effect to the display
            if (getTarget() instanceof Hero) {
                ((Hero) getTarget()).getUserInterface().addEffect(this, (int) TimeUtil.ticksToSeconds(getDuration()));
            }
        }
    }

    @Override
    public void renew() throws CombatException {

        if (isStarted()) {
            // reset the remaining ticks
            this.remainingTicks = getDuration();
            // this is a periodic effect and runs forever until cancelled so simply call renew
            super.renew();
            // renew the display of the effect
            if (getTarget() instanceof Hero) {
                ((Hero) getTarget()).getUserInterface().renewEffect(this, (int) TimeUtil.ticksToSeconds(getDuration()));
            }
        }
    }

    @Override
    public void run() {

        if (this.remainingTicks > 0) {
            super.run();
            // set the ticks that remain in this task
            // we want them outside the catch to make sure failed attempts count as ticked
            this.remainingTicks -= getInterval();
        } else {
            // lets cancel the task if the effect expired
            try {
                remove();
                // remove the effect display
                if (getTarget() instanceof Hero) {
                    ((Hero) getTarget()).getUserInterface().removeEffect(this);
                }
            } catch (CombatException e) {
                warn(e.getMessage());
            }
        }
    }
}
