package de.raidcraft.skills.api.combat;

import de.raidcraft.skills.api.skill.Skill;

/**
 * @author Silthus
 */
public abstract class AbstractEffect implements Effect {

    private final Skill skill;
    private int taskId;
    private int duration = 0;
    private int delay = 0;
    private int interval = 0;

    protected AbstractEffect(Skill skill) {

        this.skill = skill;
        this.duration = skill.getTotalEffectDuration();
        this.delay = skill.getTotalEffectDelay();
        this.interval = skill.getTotalEffectInterval();
    }

    @Override
    public Skill getSkill() {

        return skill;
    }

    @Override
    public final int getTaskId() {

        return taskId;
    }

    @Override
    public final void setTaskId(int taskId) {

        this.taskId = taskId;
    }

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

    private String convertName(String name) {

        return name.toLowerCase().replace(" ", "-").trim();
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Effect
                && convertName(obj.getClass().getAnnotation(EffectInformation.class).name())
                .equals(convertName(getClass().getAnnotation(EffectInformation.class).name()));
    }
}
