package de.raidcraft.skills.api.effect.types;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.util.ConfigUtil;
import de.raidcraft.skills.util.TimeUtil;

/**
 * @author Silthus
 */
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

        return super.getPriority() + getRemainingTicks();
    }

    public long getDuration() {

        return duration;
    }

    public long getRemainingTicks() {

        return remainingTicks;
    }

    public int getTickCount() {

        return (int) (getDuration() / getInterval());
    }

    @Override
    public void startTask() {

        if (!isStarted()) {
            super.startTask();
            this.remainingTicks = getDuration();
        }
    }

    @Override
    public void renew() throws CombatException {

        if (isStarted()) {
            // reset the remaining ticks
            this.remainingTicks = getDuration();
            // this is a periodic effect and runs forever until cancelled so simply call renew
            super.renew();
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
            } catch (CombatException e) {
                warn(e.getMessage());
            }
        }
    }
}
