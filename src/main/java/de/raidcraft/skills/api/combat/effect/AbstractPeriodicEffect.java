package de.raidcraft.skills.api.combat.effect;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public abstract class AbstractPeriodicEffect<S, T> extends AbstractTimedEffect<S, T> implements PeriodicEffect<S, T> {

    private int taskId = -1;
    private long delay;
    private long interval;
    private long duration;
    private long remainingTicks;
    private boolean started = false;

    protected AbstractPeriodicEffect(S source, T target, EffectData data) {

        super(source, target, data);
        load(data);

    }

    private void load(EffectData data) {

        // load the delay
        this.delay = data.getEffectDelay();
        if (getSource() instanceof Hero) {
            Hero hero = (Hero) getSource();
            this.delay += (data.getEffectDelayLevelModifier() * hero.getLevel().getLevel())
                    + (data.getEffectDelayProfLevelModifier() * hero.getSelectedProfession().getLevel().getLevel());
        }

        // load the interval
        this.interval = data.getEffectInterval();
        if (getSource() instanceof Hero) {
            Hero hero = (Hero) getSource();
            this.interval += (data.getEffectIntervalLevelModifier() * hero.getLevel().getLevel())
                    + (data.getEffectIntervalProfLevelModifier() * hero.getSelectedProfession().getLevel().getLevel());
        }

        // set the remaining ticks
        this.remainingTicks = getDuration() + getDelay();
    }

    @Override
    public int getTaskId() {

        return taskId;
    }

    @Override
    public double getPriority() {

        return super.getPriority() + remainingTicks;
    }

    @Override
    public long getDelay() {

        return delay;
    }

    @Override
    public long getInterval() {

        return interval;
    }

    @Override
    public void apply() throws CombatException {

        if (!started) {
            if (getInterval() > 0) {
                this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                        RaidCraft.getComponent(SkillsPlugin.class),
                        this,
                        getDelay(),
                        getInterval());
            } else {
                // only run once
                run();
            }
        }
    }

    @Override
    public void run() {

        if (!started || this.remainingTicks > 0) {
            started = true;
            try {
                apply(getTarget());
                // debug messages
                if (getSource() instanceof Hero && getTarget() instanceof CharacterTemplate) {
                    ((Hero) getSource()).debug("You->" + ((CharacterTemplate) getTarget()).getName() + ": " +
                            (remainingTicks == getDuration() + getDelay() ? "applied effect" : "effect ticked") +
                            " - " + getName());
                }
                if (getTarget() instanceof Hero && getSource() instanceof CharacterTemplate) {
                    ((Hero) getTarget()).debug(((CharacterTemplate) getSource()).getName() + "->You: " +
                            (remainingTicks == getDuration() + getDelay() ? "applied effect" : "effect ticked") +
                            " - " + getName());
                }
            } catch (CombatException e) {
                if (getSource() instanceof Hero) {
                    ((Hero) getSource()).sendMessage(ChatColor.RED + e.getMessage());
                }
            }
            // set the ticks that remain in this task
            // we want them outside the catch to make sure failed attempts count as ticked
            if (this.remainingTicks == getDuration() + getDelay()) {
                this.remainingTicks -= getDelay();
            } else {
                this.remainingTicks -= getInterval();
            }
        }

        // lets cancel the task if the effect expired
        if (started && this.remainingTicks <= 0 && getTaskId() > 0) {
            Bukkit.getScheduler().cancelTask(getTaskId());
            // some debug messages
            if (getSource() instanceof Hero && getTarget() instanceof CharacterTemplate) {
                ((Hero) getSource()).debug("You->" + ((CharacterTemplate) getTarget()).getName() + ": removed effect - " + getName());
            }
            if (getTarget() instanceof Hero && getSource() instanceof CharacterTemplate) {
                ((Hero) getTarget()).debug(((CharacterTemplate) getSource()).getName() + "->You: removed effect - " + getName());
            }
        }
    }
}
