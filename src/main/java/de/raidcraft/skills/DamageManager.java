package de.raidcraft.skills;

import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.ProjectileType;
import de.raidcraft.skills.api.combat.action.EnvironmentAttack;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.util.ConfigUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Map;

/**
 * @author Silthus
 */
public final class DamageManager implements Listener {

    private static final String CONFIG_NAME = "damages.yml";

    private final SkillsPlugin plugin;
    private final SimpleConfiguration config;
    private Map<EntityType, Integer> creatureHealth;
    private Map<EntityType, Integer> creatureDamage;
    private Map<Material, Integer> itemDamage;
    private Map<EntityDamageEvent.DamageCause, Double> environmentalDamage;
    private Map<ProjectileType, Integer> projectileDamage;

    protected DamageManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        this.config = new SimpleConfiguration(plugin, CONFIG_NAME);
        this.config.load();
        loadConfig();
        plugin.registerEvents(this);
    }

    public void reload() {

        config.reload();
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

    /*/////////////////////////////////////////////////////////////////////////
    //    Bukkit Events are called beyond this line - put your buckets on!
    /////////////////////////////////////////////////////////////////////////*/

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {

        if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
            return;
        }
        if (event.getDamage() == 0) {
            return;
        }
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        if (event instanceof EntityDamageByEntityEvent) {
            return;
        }

        CharacterTemplate character = plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity());

        try {
            if (environmentalDamage.containsKey(event.getCause())) {
                int damage = event.getDamage();
                damage += damage * environmentalDamage.get(event.getCause());
                EnvironmentAttack attack = new EnvironmentAttack(event, damage);
                event.setCancelled(true);
                attack.run();
            } else {
                // simply issue an environment attack with no modified damage
                EnvironmentAttack attack = new EnvironmentAttack(event, event.getDamage());
                event.setCancelled(true);
                attack.run();
            }
        } catch (CombatException e) {
            if (character instanceof Hero) {
                ((Hero) character).sendMessage(ChatColor.RED + e.getMessage());
            }
        }
    }
}
