package de.raidcraft.skills.api.ui;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Silthus
 */
public abstract class RefreshingDisplay implements Runnable {

    private final BukkitTask task;
    private final UserInterface userInterface;
    private int remainingDuration;

    public RefreshingDisplay(UserInterface userInterface, int duration) {

        this.userInterface = userInterface;
        this.remainingDuration = duration;
        task = Bukkit.getScheduler().runTaskTimer(RaidCraft.getComponent(SkillsPlugin.class), this, 1, 20);
    }

    public abstract OfflinePlayer getScoreName();

    public void setRemainingDuration(int remainingDuration) {

        this.remainingDuration = remainingDuration;
    }

    public int getRemainingDuration() {

        return remainingDuration;
    }

    @Override
    public void run() {

        if (userInterface == null) {
            return;
        }
        if (remainingDuration < 1) {
            cancel();
            return;
        }
        if (!userInterface.getHero().isOnline()) {
            cancel();
            return;
        }
        userInterface.updateSidebarScore(getScoreName(), remainingDuration);
        remainingDuration--;
    }

    private void cancel() {

        task.cancel();
        userInterface.removeSidebarScore(getScoreName());
    }
}
