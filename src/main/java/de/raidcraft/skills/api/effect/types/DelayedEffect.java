package de.raidcraft.skills.api.effect.types;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.IgnoredEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.util.ConfigUtil;
import de.raidcraft.util.TimeUtil;
import org.bukkit.Bukkit;

/**
 * @author Silthus
 */
@IgnoredEffect
public abstract class DelayedEffect<S> extends ScheduledEffect<S> {

    protected long delay = 0;

    public DelayedEffect(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        load(data);
    }

    private void load(EffectData data) {

        if (getSource() instanceof Skill) {
            delay = TimeUtil.secondsToTicks(ConfigUtil.getTotalValue((Skill) getSource(), data.getEffectDelay()));
        } else {
            delay = TimeUtil.secondsToTicks(data.getEffectDelay().getInt("base", 0));
        }
    }

    public long getDelay() {

        return delay;
    }

    @Override
    public void startTask() {

        setTask(Bukkit.getScheduler().runTaskLater(
                RaidCraft.getComponent(SkillsPlugin.class),
                this,
                getDelay()
        ));
    }

    @Override
    public void apply() throws CombatException {

        if (isStarted()) {
            renew();
        } else {
            // only start the task and dont apply yet
            startTask();
        }
    }

    @Override
    public void remove() throws CombatException {

        if (isStarted()) {
            stopTask();
            super.remove();
        } else {
            // this means the effect was already applied
            super.remove();
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
            super.apply();
            stopTask();
        } catch (CombatException e) {
            warn(e.getMessage());
        }
    }
}
