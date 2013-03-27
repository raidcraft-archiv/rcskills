package de.raidcraft.skills;

import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.config.CustomConfig;
import de.raidcraft.skills.util.ItemUtil;
import de.raidcraft.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public final class WeaponManager implements Triggered {

    private static final String CONFIG_NAME = "weapons";
    private final SkillsPlugin plugin;
    // maps the weapon (itemId) to the min/max damage (key/value)
    private final Map<Integer, DefaultWeaponConfig> defaultWeaponMinMaxDamage = new HashMap<>();

    protected WeaponManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        TriggerManager.registerListeners(this);
        load();
    }

    private void load() {

        defaultWeaponMinMaxDamage.clear();
        ConfigurationSection config = CustomConfig.getConfig(CONFIG_NAME).getSafeConfigSection("weapons");
        Set<String> keys = config.getKeys(false);
        if (keys == null || keys.size() < 1) {
            plugin.getLogger().warning("No weapons configured in custom weapons.yml config.");
            return;
        }
        for (String key : keys) {
            Material item = ItemUtils.getItem(key);
            if (item != null && ItemUtil.isWeapon(item)) {
                int minDamage = config.getInt(key + ".min", 0);
                int maxDamage = config.getInt(key + ".max", 0);
                double swingTime = config.getDouble(key + ".swing-time", 1.5);
                defaultWeaponMinMaxDamage.put(item.getId(), new DefaultWeaponConfig(minDamage, maxDamage, swingTime));
            } else {
                plugin.getLogger().warning("Wrong weapon item configured in custom config: " + config.getName() + " - " + key);
            }
        }
    }

    public void reload() {

        CustomConfig.getConfig(CONFIG_NAME).reload();
        load();
    }

    public DefaultWeaponConfig getDefaultMinMaxDamage(int itemid) {

        return defaultWeaponMinMaxDamage.get(itemid);
    }

    public static class DefaultWeaponConfig {

        private final int minDamage;
        private final int maxDamage;
        private final double swingTime;

        public DefaultWeaponConfig(int minDamage, int maxDamage, double swingTime) {

            this.minDamage = minDamage;
            this.maxDamage = maxDamage;
            this.swingTime = swingTime;
        }

        public int getMinDamage() {

            return minDamage;
        }

        public int getMaxDamage() {

            return maxDamage;
        }

        public double getSwingTime() {

            return swingTime;
        }
    }
}
