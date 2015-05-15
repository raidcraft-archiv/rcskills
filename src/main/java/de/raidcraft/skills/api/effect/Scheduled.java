package de.raidcraft.skills.api.effect;

import org.bukkit.scheduler.BukkitTask;

/**
 * @author Silthus
 */
public interface Scheduled extends Runnable {

    BukkitTask getTask();

    void setTask(BukkitTask task);

    boolean isStarted();

    void startTask();

    void stopTask();
}
