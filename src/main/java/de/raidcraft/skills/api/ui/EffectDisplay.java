package de.raidcraft.skills.api.ui;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.effect.Effect;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Score;

/**
 * @author Silthus
 */
public class EffectDisplay implements Runnable {

    private final Effect effect;
    private final BukkitTask task;
    private final Score score;
    private int remainingDuration;

    public EffectDisplay(Effect effect, Score score, int duration) {

        this.effect = effect;
        this.score = score;
        this.remainingDuration = duration;
        task = Bukkit.getScheduler().runTaskTimer(RaidCraft.getComponent(SkillsPlugin.class), this, 0, 20);
    }

    public Effect getEffect() {

        return effect;
    }

    public void setRemainingDuration(int remainingDuration) {

        this.remainingDuration = remainingDuration;
    }

    public int getRemainingDuration() {

        return remainingDuration;
    }

    @Override
    public void run() {

        if (remainingDuration < 1) {
            task.cancel();
            score.getScoreboard().resetScores(score.getPlayer());
            return;
        }
        score.setScore(remainingDuration);
        remainingDuration--;
    }
}
