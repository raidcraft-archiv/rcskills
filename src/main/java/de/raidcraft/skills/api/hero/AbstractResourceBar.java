package de.raidcraft.skills.api.hero;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Silthus
 */
public abstract class AbstractResourceBar implements ResourceBar {

    private final Hero hero;
    private final ResourceType type;
    private final ConfigurationSection config;
    private BukkitTask task;
    private long regenInterval = 20;
    private double regenPercent = 0.03;
    private int current;
    private boolean enabled = true;

    public AbstractResourceBar(Hero hero, ResourceType type, ConfigurationSection config) {

        this.hero = hero;
        this.type = type;
        this.config = config;
        // lets start the task
        startTask();
    }

    private void startTask() {

        task = Bukkit.getScheduler().runTaskTimer(RaidCraft.getComponent(SkillsPlugin.class), new Runnable() {
            @Override
            public void run() {

                regen();
            }
        }, getRegenInterval(), getRegenInterval());
    }

    @Override
    public void destroy() {

        task.cancel();
    }

    @Override
    public Hero getHero() {

        return hero;
    }

    @Override
    public ResourceType getType() {

        return type;
    }

    @Override
    public String getName() {

        return type.getName();
    }

    @Override
    public int getCurrent() {

        return current;
    }

    @Override
    public void setCurrent(int current) {

        if (current < getMin()) current = getMin();
        if (current > getMax()) current = getMax();
        this.current = current;
    }

    @Override
    public boolean isEnabled() {

        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {

        this.enabled = enabled;
    }

    @Override
    public long getRegenInterval() {

        return regenInterval;
    }

    @Override
    public void setRegenInterval(long interval) {

        this.regenInterval = interval;
        // we need to change the task
        task.cancel();
        startTask();
    }

    @Override
    public double getRegenPercent() {

        return regenPercent;
    }

    @Override
    public void setRegenPercent(double percent) {

        this.regenPercent = percent;
    }

    @Override
    public int getMax() {

        int max = config.getInt("base", 100);
        if (getHero().getLevel() != null) {
            max += config.getDouble("level-modifier", 0.0) * getHero().getLevel().getLevel();
        }
        if (getHero().getPrimaryProfession() != null) {
            max += config.getDouble("prof-level-modifier", 0.0) * getHero().getPrimaryProfession().getLevel().getLevel();
        }
        return max;
    }

    @Override
    public boolean isMax() {

        return getMax() == getCurrent();
    }

    @Override
    public boolean isMin() {

        return getMin() == getCurrent();
    }

    @Override
    public void regen() {

        if (!isEnabled()) {
            return;
        }
        if (isMax() && getRegenPercent() > 0) {
            return;
        }
        if (isMin() && getRegenPercent() < 0) {
            return;
        }

        setCurrent((int) (getCurrent() + getMax() * getRegenPercent()));
    }

    @Override
    public String draw() {

        StringBuilder resourceBar = new StringBuilder(getFilledColor() + getName() + ": ");

        resourceBar.append(String.valueOf(ChatColor.RED)).append("[").append(getFilledColor());
        int percent = (int) ((getCurrent() / getMax()) * 100.0);
        int progress = percent / 2;
        for (int i = 0; i < progress; i++) {
            resourceBar.append('|');
        }
        resourceBar.append(getUnfilledColor());
        for (int i = 0; i < 50 - progress; i++) {
            resourceBar.append('|');
        }
        resourceBar.append(ChatColor.RED).append(']');

        return String.valueOf(resourceBar) + " - " + getFilledColor() + percent + "%";
    }
}
