package de.raidcraft.skills.api.combat.effect;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.PeriodicEffectData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public abstract class AbstractPeriodicEffect<S, T> extends AbstractTimedEffect<S, T> implements PeriodicEffect<S, T> {

    private int taskId = -1;
    private final PeriodicEffectData data;
    private long remainingTicks;
    private boolean started = false;

    protected AbstractPeriodicEffect(S source, T target, PeriodicEffectData data) {

        super(source, target, data);
        this.data = data;
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
    public int getDelay() {

        if (getSource() instanceof Hero) {
            Hero hero = (Hero) getSource();
            return (int) (data.getEffectDelay()
                    + (data.getEffectDelayLevelModifier() * hero.getLevel().getLevel())
                    + (data.getEffectDelayProfLevelModifier() * hero.getSelectedProfession().getLevel().getLevel()));
        }
        return data.getEffectDelay();
    }

    @Override
    public int getInterval() {

        if (getSource() instanceof Hero) {
            Hero hero = (Hero) getSource();
            return (int) (data.getEffectInterval()
                    + (data.getEffectIntervalLevelModifier() * hero.getLevel().getLevel())
                    + (data.getEffectIntervalProfLevelModifier() * hero.getSelectedProfession().getLevel().getLevel()));
        }
        return data.getEffectInterval();
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
                started = true;
            } else {
                RaidCraft.getComponent(SkillsPlugin.class).getLogger().warning("Effect(" + getName() + ") interval must be over 0!");
            }
        }
    }

    @Override
    public void run() {

        if (started && this.remainingTicks > 0) {
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
        if (started && this.remainingTicks <= 0) {
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
