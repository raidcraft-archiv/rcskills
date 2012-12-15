package de.raidcraft.skills.api.combat;

import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.skills.SkillsPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public final class DamageManager {

    private static final String CONFIG_NAME = "damages.yml";

    private final SkillsPlugin plugin;
    private final SimpleConfiguration config;
    private Map<EntityType, Integer> creatureHealth;
    private Map<EntityType, Integer> creatureDamage;
    private Map<Material, Integer> itemDamage;
    private Map<EntityDamageEvent.DamageCause, Double> environmentalDamage;
    private Map<ProjectileType, Integer> projectileDamage;

    public DamageManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        this.config = new SimpleConfiguration(plugin, CONFIG_NAME);
        this.config.load();
        loadConfig();
    }

    private void loadConfig() {

        this.creatureHealth = loadEnumMap("creature-health", EntityType.class, 20);
        this.creatureDamage = loadEnumMap("creature-damage", EntityType.class, 10);
        this.itemDamage = loadEnumMap("item-damage", Material.class, 2);
        this.environmentalDamage = loadEnumMap("environmental-damage", EntityDamageEvent.DamageCause.class, 0.0);
        this.projectileDamage = loadEnumMap("projectile-damage", ProjectileType.class, 0);
    }

    public int getCreatureHealth(EntityType type) {

        if (creatureHealth.containsKey(type)) {
            return creatureHealth.get(type);
        }
        return 20;
    }

    public int getCreatureDamage(EntityType type) {

        if (creatureDamage.containsKey(type)) {
            creatureDamage.get(type);
        }
        return 10;
    }

    public int getItemDamage(Material type) {

        if (itemDamage.containsKey(type)) {
            return itemDamage.get(type);
        }
        return 2;
    }

    public double getEnvironmentalDamage(EntityDamageEvent.DamageCause type) {

        if (environmentalDamage.containsKey(type)) {
            return environmentalDamage.get(type);
        }
        return 0.0;
    }

    public int getProjectileDamage(ProjectileType type) {

        if (projectileDamage.containsKey(type)) {
            return projectileDamage.get(type);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    private <K extends Enum<K>, V> Map<K, V> loadEnumMap(String path, Class<K> enumType, V def) {

        Map<K, V> map = new EnumMap<>(enumType);
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section != null) {
            Set<String> keys = section.getKeys(false);
            if (keys != null) {
                for (String key : keys) {
                    try {
                        K type = K.valueOf(enumType, key);
                        if (type == null) {
                            throw new Exception("Invalid key type (" + key + ") found in damages.yml.");
                        }
                        V value = (V) section.get(key, def);
                        map.put(type, value);
                    } catch (ClassCastException e) {
                        plugin.getLogger().warning("Invalid value type (" + key + ") in damages config!");
                    } catch (Exception e) {
                        plugin.getLogger().warning(e.getMessage());
                    }
                }
            }
        }
        return map;
    }
}
