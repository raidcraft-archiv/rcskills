package de.raidcraft.skills.api.effect;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.util.ConfigUtil;
import de.raidcraft.skills.util.TimeUtil;
import org.bukkit.Bukkit;

/**
 * @author Silthus
 */
public abstract class PeriodicEffect<S> extends ScheduledEffect<S> {

    protected long delay;
    protected long interval;
    protected boolean firstTick = false;

    public PeriodicEffect(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        load(data);
    }

    private void load(EffectData data) {

        if (getSource() instanceof Skill) {
            delay = TimeUtil.secondsToTicks(ConfigUtil.getTotalValue((Skill) getSource(), data.getEffectDelay()));
            interval = TimeUtil.secondsToTicks(ConfigUtil.getTotalValue((Skill) getSource(), data.getEffectInterval()));
        } else {
            delay = TimeUtil.secondsToTicks(data.getEffectDelay().getInt("base", 0));
            interval = TimeUtil.secondsToTicks(data.getEffectInterval().getInt("base", 0));
        }
    }

    public long getDelay() {

        return delay;
    }

    public long getInterval() {

        return interval;
    }

    @Override
    public void startTask() {

        if (!isStarted()) {
            setTask(Bukkit.getScheduler().runTaskTimer(
                    RaidCraft.getComponent(SkillsPlugin.class),
                    this,
                    getDelay(),
                    getInterval()
            ));
            firstTick = false;
        }
    }

    @Override
    public void apply() throws CombatException {

        // dont start if interval is not configured
        if (getInterval() < 1) {
            remove();
        }
        if (!isStarted()) {
            // lets start the task
            startTask();
        }
    }

    @Override
    public void remove() throws CombatException {

        if (isStarted()) {
            stopTask();
            super.remove();
        }
    }

    @Override
    public void renew() throws CombatException {

        if (isStarted()) {
            // this is a periodic effect and runs forever until cancelled so simply call renew
            super.renew();
        }
    }

    protected abstract void tick(CharacterTemplate target) throws CombatException;

    @Override
    public void run() {

        try {
            if (!firstTick) {
                super.apply();
                firstTick = true;
            }
            // a periodic effects apply method is called everytime the effect ticks
            tick(getTarget());
            debug("effect ticked");
        } catch (CombatException e) {
            warn(e.getMessage());
        }
    }
}
