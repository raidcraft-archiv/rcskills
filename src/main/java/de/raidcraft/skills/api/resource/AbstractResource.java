package de.raidcraft.skills.api.resource;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Database;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.ResourceData;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.trigger.ResourceChangeTrigger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Silthus
 */
public abstract class AbstractResource implements Resource {

    private final String name;
    private final String friendlyName;
    private final ResourceData data;
    private final Profession profession;
    private final ConfigurationSection config;
    private final VisualResourceType type;
    private BukkitTask task;
    private long regenInterval = 20;
    private double regenPercent = 0.03;
    private int current;
    private boolean enabled = true;

    public AbstractResource(ResourceData data, Profession profession, ConfigurationSection config) {

        this.name = config.getName().toLowerCase().replace(" ", "-").trim();
        this.friendlyName = config.getString("name", name);
        this.data = data;
        this.profession = profession;
        this.config = config;
        this.type = VisualResourceType.fromString(config.getString("type", "text"));
        // lets set the current value
        setCurrent((data.getValue() == 0 ? getDefault() : data.getValue()));
        // lets get some default values from the config
        regenPercent = config.getDouble("regen.base", 0.03);
        regenInterval = config.getInt("regen.interval", 1) * 20;
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

    protected ConfigurationSection getConfig() {

        return config;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getFriendlyName() {

        return friendlyName;
    }

    @Override
    public Hero getHero() {

        return getProfession().getHero();
    }

    @Override
    public Profession getProfession() {

        return profession;
    }

    @Override
    public VisualResourceType getType() {

        return type;
    }

    @Override
    public int getCurrent() {

        return current;
    }

    @Override
    public void setCurrent(int current) {

        if (current < getMin()) current = getMin();
        if (current > getMax()) current = getMax();
        // lets fire the trigger
        ResourceChangeTrigger trigger = TriggerManager.callSafeTrigger(new ResourceChangeTrigger(getHero(), this, current));
        if (trigger.isCancelled()) {
            return;
        }
        // update the value if it changed in the trigger
        current = trigger.getNewValue();

        boolean update = this.current != current;
        this.current = current;

        if (update) {
            getType().update(this);
        }
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

        int max = config.getInt("max.base", 100);
        if (getProfession().getAttachedLevel() != null) {
            max += config.getDouble("max.level-modifier", 0.0) * getHero().getAttachedLevel().getLevel();
        }
        if (getProfession() != null && getProfession().getAttachedLevel() != null) {
            max += config.getDouble("max.prof-level-modifier", 0.0) * getProfession().getAttachedLevel().getLevel();
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
    public int getMin() {

        return config.getInt("min", 0);
    }

    @Override
    public ChatColor getFilledColor() {

        String string = config.getString("color.filled", "BLUE");
        ChatColor color = ChatColor.valueOf(string);
        if (color == null) color = ChatColor.getByChar(string);
        if (color == null) color = ChatColor.BLUE;
        return color;
    }

    @Override
    public ChatColor getUnfilledColor() {

        String string = config.getString("color.unfilled", "DARK_RED");
        ChatColor color = ChatColor.valueOf(string);
        if (color == null) color = ChatColor.getByChar(string);
        if (color == null) color = ChatColor.DARK_RED;
        return color;
    }

    @Override
    public int getDefault() {

        return config.getInt("default", 0);
    }

    @Override
    public void regen() {

        if (!isEnabled() || !profession.isActive()) {
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
    public void destroy() {

        task.cancel();
    }

    @Override
    public void save() {

        // dont save when the player is in a blacklist world
        if (!RaidCraft.getComponent(SkillsPlugin.class).isSavingWorld(getHero().getPlayer().getWorld().getName())) {
            return;
        }

        data.setValue(getCurrent());
        Database.save(data);
    }
}
