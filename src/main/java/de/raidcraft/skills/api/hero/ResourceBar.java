package de.raidcraft.skills.api.hero;

import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public interface ResourceBar {

    public Hero getHero();

    public String getName();

    public ChatColor getFilledColor();

    public ChatColor getUnfilledColor();

    public int getDefault();

    public int getMin();

    public boolean isMin();

    public int getCurrent();

    public void setCurrent(int current);

    public int getMax();

    public boolean isMax();

    public boolean isEnabled();

    public void setEnabled(boolean enabled);

    public long getRegenInterval();

    public void setRegenInterval(long interval);

    public double getRegenPercent();

    public void setRegenPercent(double percent);

    public void destroy();

    public void regen();

    public String draw();
}
