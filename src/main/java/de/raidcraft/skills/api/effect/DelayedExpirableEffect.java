package de.raidcraft.skills.api.effect;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.LevelableSkill;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Silthus
 */
public abstract class DelayedExpirableEffect<S> extends ScheduledEffect<S> {

    private BukkitTask removalTask = null;
    protected long delay = 0;
    protected long duration = 0;

    public DelayedExpirableEffect(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        load(data);
    }

    private void load(EffectData data) {

        delay = data.getEffectDelay();
        duration = data.getEffectDuration();
        if (getSource() instanceof Hero) {
            Hero hero = (Hero) getSource();
            delay += (data.getEffectDelayLevelModifier() * hero.getLevel().getLevel());

            duration += (data.getEffectDurationLevelModifier() * hero.getLevel().getLevel());
        }
        if (getSource() instanceof Skill) {
            delay += data.getEffectDelayLevelModifier() * ((Skill) getSource()).getHero().getLevel().getLevel();
            delay += data.getEffectDelayProfLevelModifier() * ((Skill) getSource()).getProfession().getLevel().getLevel();

            duration += data.getEffectDurationLevelModifier() * ((Skill) getSource()).getHero().getLevel().getLevel();
            duration += data.getEffectDurationProfLevelModifier() * ((Skill) getSource()).getProfession().getLevel().getLevel();
        }
        if (getSource() instanceof LevelableSkill) {
            delay += data.getEffectDelaySkillLevelModifier() * ((LevelableSkill) getSource()).getLevel().getLevel();

            duration += data.getEffectDurationSkillLevelModifier() * ((LevelableSkill) getSource()).getLevel().getLevel();
        }
    }

    public BukkitTask getRemovalTask() {

        return removalTask;
    }

    public void setRemovalTask(BukkitTask task) {

        this.removalTask = task;
    }

    private void stopRemovalTask() {

        if (removalTask != null) {
            removalTask.cancel();
            removalTask = null;
        }
    }

    private boolean isRemovalScheduled() {

        return removalTask != null && !isStarted();
    }

    public long getDelay() {

        return delay;
    }

    public long getDuration() {

        return duration;
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
