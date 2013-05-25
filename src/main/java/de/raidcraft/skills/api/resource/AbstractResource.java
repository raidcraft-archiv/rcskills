package de.raidcraft.skills.api.resource;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.ResourceData;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.trigger.ResourceChangeTrigger;
import de.raidcraft.skills.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Silthus
 */
public abstract class AbstractResource implements Resource {

    private final String name;
    private final String friendlyName;
    private final ResourceData data;
    private final Profession profession;
    private final ConfigurationSection config;
    private final ConfigurationSection inCombatRegen;
    private final ConfigurationSection outOfCombatRegen;
    private final Set<VisualResourceType> types = new HashSet<>();
    private BukkitTask task;
    private long regenInterval;
    private double regenValue;
    private int current;
    private boolean enabled = false;

    public AbstractResource(ResourceData data, Profession profession, ConfigurationSection config) {

        this.name = config.getName().toLowerCase().replace(" ", "-").trim();
        this.friendlyName = config.getString("name", name);
        this.data = data;
        this.profession = profession;
        this.config = config;
        for (String type : config.getStringList("types")) {
            types.add(VisualResourceType.fromString(type));
        }
        // lets set the current value
        setCurrent((data.getValue() == 0 ? getDefault() : data.getValue()));
        for (VisualResourceType type : getTypes()) {
            type.update(this);
        }
        // lets get some default values from the config
        this.inCombatRegen = config.getConfigurationSection("in-combat-regen");
        this.outOfCombatRegen = config.getConfigurationSection("out-of-combat-regen");
        regenInterval = (long) config.getDouble("interval", 3) * 20;
        // lets start the task
        startTask();
    }

    private double getCombatRegenValue() {

        return ConfigUtil.getTotalValue(getProfession(), inCombatRegen);
    }

    private boolean isInCombatRegenInPercent() {

        return inCombatRegen.getBoolean("percent", true);
    }

    private double getOutOfCombatRegenValue() {

        return ConfigUtil.getTotalValue(getProfession(), outOfCombatRegen);
    }

    private boolean isOutOfCombatRegenInPercent() {

        return outOfCombatRegen.getBoolean("percent", true);
    }

    private void startTask() {

        if (getRegenInterval() < 1 || !enabled) {
            return;
        }
        task = Bukkit.getScheduler().runTaskTimer(RaidCraft.getComponent(SkillsPlugin.class), new Runnable() {
            @Override
            public void run() {

                regen();
            }
        }, getRegenInterval(), getRegenInterval());
    }

    public ConfigurationSection getConfig() {

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
    public Set<VisualResourceType> getTypes() {

        return types;
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

        boolean update = this.current != current && isEnabled();
        this.current = current;

        if (update) {
            for (VisualResourceType type : getTypes()) {
                type.update(this);
            }
        }
    }

    @Override
    public boolean isEnabled() {

        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {

        this.enabled = enabled;
        if (enabled) {
            startTask();
        } else {
            destroy();
        }
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
    public double getRegenValue() {

        return regenValue;
    }

    @Override
    public void setRegenValue(double percent) {

        this.regenValue = percent;
    }

    @Override
    public int getMax() {

        return (int) ConfigUtil.getTotalValue(profession, config.getConfigurationSection("max"));
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
    public int getDefault() {

        return config.getInt("default", 0);
    }

    @Override
    public void regen() {

        if (!isEnabled() || !profession.isActive()) {
            return;
        }
        if (isMax() && getRegenValue() > 0) {
            return;
        }
        if (isMin() && getRegenValue() < 0) {
            return;
        }

        int newValue = getCurrent();
        if (getHero().isInCombat()) {
            if (isInCombatRegenInPercent()) {
                newValue += getMax() * getCombatRegenValue();
            } else {
                newValue += getCombatRegenValue();
            }
        } else {
            if (isOutOfCombatRegenInPercent()) {
                newValue += getMax() * getOutOfCombatRegenValue();
            } else {
                newValue += getOutOfCombatRegenValue();
            }
        }
        if (newValue == getCurrent()) {
            return;
        }
        setCurrent(newValue);
    }

    @Override
    public void destroy() {

        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public void save() {

        data.setValue(getCurrent());
        // dont save when the player is in a blacklist world
        if (RaidCraft.getComponent(SkillsPlugin.class).isSavingWorld(getHero().getPlayer().getWorld().getName())) {
            RaidCraft.getDatabase(SkillsPlugin.class).save(data);
        }
    }
}
