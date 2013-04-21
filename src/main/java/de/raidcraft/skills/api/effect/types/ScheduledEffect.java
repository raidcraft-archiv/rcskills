package de.raidcraft.skills.api.effect.types;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.AbstractEffect;
import de.raidcraft.skills.api.effect.Scheduled;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Silthus
 */
public abstract class ScheduledEffect<S> extends AbstractEffect<S> implements Scheduled {

    private BukkitTask task;

    public ScheduledEffect(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    public final BukkitTask getTask() {

        return task;
    }

    @Override
    public final void setTask(BukkitTask task) {

        this.task = task;
    }

    @Override
    public final boolean isStarted() {

        return task != null;
    }

    @Override
    public final void stopTask() {

        if (isStarted()) {
            task.cancel();
            task = null;
        }
    }
}
