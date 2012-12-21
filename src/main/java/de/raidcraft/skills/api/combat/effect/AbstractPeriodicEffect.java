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
public abstract class AbstractPeriodicEffect<S> extends AbstractDelayedEffect<S> {

    protected long interval;
    private long remainingTicks;

    public AbstractPeriodicEffect(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        load(data);
    }

    private void load(EffectData data) {

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
    public double getPriority() {

        return super.getPriority() + remainingTicks;
    }

    public long getInterval() {

        return interval;
    }

    @Override
    public void startTask() {

        setTask(Bukkit.getScheduler().runTaskTimer(
                RaidCraft.getComponent(SkillsPlugin.class),
                this,
                getDelay(),
                getInterval()
        ));
    }

    @Override
    public void renew() throws CombatException {

        // lets cancel the existing task and start a new task
        stopTask();
        startTask();
        renew(getTarget());
    }

    @Override
    public void apply() throws CombatException {

        if (!isStarted()) {
            if (getInterval() > 0) {
                startTask();
            } else {
                // only run once
                ((AbstractEffect)this).apply();
            }
        }
    }

    @Override
    public void remove() throws CombatException {

        if (isStarted()) {
            stopTask();
            remove(getTarget());
            // some debug messages
            if (getSource() instanceof Hero) {
                ((Hero) getSource()).debug("You->" + getTarget().getName() + ": removed effect - " + getName());
            }
            if (getTarget() instanceof Hero && getSource() instanceof CharacterTemplate) {
                ((Hero) getTarget()).debug(((CharacterTemplate) getSource()).getName() + "->You: removed effect - " + getName());
            }
        } else {
            super.remove();
        }
    }

    @Override
    public void run() {

        if (this.remainingTicks > 0) {
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
        if (this.remainingTicks <= 0) {
            try {
                remove();
            } catch (CombatException e) {
                if (getSource() instanceof Hero) {
                    ((Hero) getSource()).sendMessage(ChatColor.RED + e.getMessage());
                }
            }
        }
    }
}
