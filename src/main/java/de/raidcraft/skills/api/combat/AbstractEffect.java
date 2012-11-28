package de.raidcraft.skills.api.combat;

/**
 * @author Silthus
 */
public abstract class AbstractEffect implements Effect {

    private int duration = 0;
    private int delay = 0;
    private int interval = 0;

    @Override
    public Effect setDuration(int duration) {

        this.duration = duration;
        return this;
    }

    @Override
    public int getDuration() {

        return duration;
    }

    @Override
    public Effect setDelay(int delay) {

        this.delay = delay;
        return this;
    }

    @Override
    public int getDelay() {

        return delay;
    }

    @Override
    public Effect setInterval(int interval) {

        this.interval = interval;
        return this;
    }

    @Override
    public int getInterval() {

        return interval;
    }
}
