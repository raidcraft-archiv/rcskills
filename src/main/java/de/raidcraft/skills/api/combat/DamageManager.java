package de.raidcraft.skills.api.combat;

import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.util.ConfigUtil;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Map;

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

        this.creatureHealth = ConfigUtil.loadEnumMap(config.getConfigurationSection("creature-health"), EntityType.class, 20);
        this.creatureDamage = ConfigUtil.loadEnumMap(config.getConfigurationSection("creature-damage"), EntityType.class, 10);
        this.itemDamage = ConfigUtil.loadEnumMap(config.getConfigurationSection("item-damage"), Material.class, 2);
        this.environmentalDamage = ConfigUtil.loadEnumMap(config.getConfigurationSection("environmental-damage"), EntityDamageEvent.DamageCause.class, 0.0);
        this.projectileDamage = ConfigUtil.loadEnumMap(config.getConfigurationSection("projectile-damage"), ProjectileType.class, 0);
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
}
