package de.raidcraft.skills.api.effect;

import org.bukkit.scheduler.BukkitTask;

/**
 * @author Silthus
 */
public interface Scheduled extends Runnable {

    public BukkitTask getTask();

    public void setTask(BukkitTask task);

    public boolean isStarted();

    public void startTask();

    public void stopTask();
}
