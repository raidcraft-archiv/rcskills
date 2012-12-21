package de.raidcraft.skills.api.combat.effect;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Silthus
 */
public abstract class AbstractTimedEffect<S> extends AbstractEffect<S> implements ScheduledEffect<S> {

    private BukkitTask task = null;
    protected long duration = 0;

    public AbstractTimedEffect(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        load(data);
    }

    private void load(EffectData data) {

        this.duration = data.getEffectDuration();
        if (getSource() instanceof Hero) {
            Hero hero = (Hero) getSource();
            this.duration += (data.getEffectDurationLevelModifier() * hero.getLevel().getLevel())
                    + (data.getEffectDurationProfLevelModifier() * hero.getSelectedProfession().getLevel().getLevel());
        }
    }

    protected abstract void remove(CharacterTemplate target) throws CombatException;

    protected abstract void renew(CharacterTemplate target) throws CombatException;

    @Override
    public BukkitTask getTask() {

        return task;
    }

    protected void setTask(BukkitTask task) {

        this.task = task;
    }

    public long getDuration() {

        return duration;
    }

    @Override
    public boolean isStarted() {

        return task != null;
    }

    @Override
    public void startTask() {

        // lets run a task to remove this effect after the given duration
        this.task = Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(SkillsPlugin.class), this, getDuration());
    }

    @Override
    public void stopTask() {

        if (isStarted()) {
            this.task.cancel();
            this.task = null;
        }
    }

    @Override
    public void renew() throws CombatException {

        stopTask();
        startTask();
        renew(getTarget());
        if (getSource() instanceof Hero) {
            ((Hero) getSource()).debug("You->" + getTarget().getName() + ": renewed effect - " + getName());
        }
        if (getTarget() instanceof Hero && getSource() instanceof CharacterTemplate) {
            ((Hero) getTarget()).debug(((CharacterTemplate) getSource()).getName() + "->You: renewed effect - " + getName());
        }
    }

    @Override
    public void apply() throws CombatException {

        startTask();
        super.apply();
    }

    @Override
    public void remove() throws CombatException {

        if (isStarted()) {
            stopTask();
            remove(getTarget());
            getTarget().removeEffect(this);
            if (getSource() instanceof Hero) {
                ((Hero) getSource()).debug("You->" + getTarget().getName() + ": removed effect - " + getName());
            }
            if (getTarget() instanceof Hero && getSource() instanceof CharacterTemplate) {
                ((Hero) getTarget()).debug(((CharacterTemplate) getSource()).getName() + "->You: removed effect - " + getName());
            }
        }
    }

    @Override
    public void run() {

        try {
            // this is called when the task is scheduled to be removed
            remove();
            this.task = null;
        } catch (CombatException e) {
            if (getSource() instanceof Hero) {
                ((Hero) getSource()).sendMessage(ChatColor.RED + e.getMessage());
            }
        }
    }
}
