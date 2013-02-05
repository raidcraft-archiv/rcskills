package de.raidcraft.skills.api.resource;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public interface Resource {

    public Hero getHero();

    public Profession getProfession();

    public VisualResourceType getType();

    public String getName();

    public String getFriendlyName();

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

    public void regen();

    public void save();
}
