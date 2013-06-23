package de.raidcraft.skills.api.effect.types;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.ambient.AmbientEffect;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.EffectEffectStage;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.util.ConfigUtil;
import de.raidcraft.util.TimeUtil;
import org.bukkit.Bukkit;

import java.util.List;

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
                firstTick = true;
                super.apply();
            } else {
                // a periodic effects apply method is called everytime the effect ticks
                tick(getTarget());
                // lets play some visual effects
                List<AmbientEffect> effects = visualEffects.get(EffectEffectStage.TICK);
                if (effects != null) {
                    for (AmbientEffect effect : effects) {
                        effect.run(getTarget().getEntity().getLocation());
                    }
                }
            }
        } catch (CombatException e) {
            warn(e.getMessage());
        }
    }
}
