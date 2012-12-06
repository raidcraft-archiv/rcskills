package de.raidcraft.skills.api.combat;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.skill.Skill;

/**
 * @author Silthus
 */
public abstract class AbstractEffect implements Effect {

    private final Skill skill;
    private final EffectInformation info;
    private int taskId;
    private double priority;
    // messures how long this effect was running for
    private int duration = 0;
    private int damage = 0;
    private int totalDuration = 0;
    private int delay = 0;
    private int interval = 0;

    protected AbstractEffect(Skill skill) {

        this.skill = skill;
        this.info = getClass().getAnnotation(EffectInformation.class);
        this.priority = skill.getEffectProperties().getEffectPriority();
        this.totalDuration = skill.getTotalEffectDuration();
        this.delay = skill.getTotalEffectDelay();
        this.interval = skill.getTotalEffectInterval();
    }

    @Override
    public String getName() {

        return convertName(info.name());
    }

    @Override
    public String getDescription() {

        return info.description();
    }

    @Override
    public final double getPriority() {

        // calculates the virtual strength of this effect
        // this a decision base for other effects to override this
        if (priority == RaidCraft.getComponent(SkillsPlugin.class).getCommonConfig().default_effect_priority) {
            return (interval * (totalDuration - duration)) * getDamage();
        } else {
            return priority;
        }
    }

    @Override
    public void increaseDuration() {

        if (duration == 0) {
            duration = delay;
        } else {
            duration += interval;
        }
    }

    @Override
    public int getDuration() {

        return duration;
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
    public int getDamage() {

        return damage;
    }

    @Override
    public void setDamage(int damage) {

        this.damage = damage;
    }

    @Override
    public Effect setTotalDuration(int duration) {

        this.totalDuration = duration;
        return this;
    }

    @Override
    public int getTotalDuration() {

        return totalDuration;
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

        // IMPORTANT: An effect has a skill but is compared by its name and not the skill
        // this is important for checking duplicate or stronger effects from different skills
        return obj instanceof Effect
                && ((Effect) obj).getName().equalsIgnoreCase(getName());
    }
}
