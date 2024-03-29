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
    private Map<EntityType, Double> creatureDamage;
    private Map<Material, Double> itemDamage;
    private Map<EntityDamageEvent.DamageCause, Double> environmentalDamage;
    private Map<ProjectileType, Double> projectileDamage;

    protected DamageManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        this.config = plugin.configure(new SimpleConfiguration<>(plugin, CONFIG_NAME));
        loadConfig();
        plugin.registerEvents(this);
    }

    private void loadConfig() {

        this.creatureHealth = ConfigUtil.loadEnumMap(config.getConfigurationSection("creature-health"), EntityType.class, 20);
        this.creatureDamage = ConfigUtil.loadEnumMap(config.getConfigurationSection("creature-damage"), EntityType.class, 10.0);
        this.itemDamage = ConfigUtil.loadEnumMap(config.getConfigurationSection("item-damage"), Material.class, 2.0);
        this.environmentalDamage = ConfigUtil.loadEnumMap(config.getConfigurationSection("environmental-damage"), EntityDamageEvent.DamageCause.class, 0.0);
        this.projectileDamage = ConfigUtil.loadEnumMap(config.getConfigurationSection("projectile-damage"), ProjectileType.class, 0.0);
    }

    public int getCreatureHealth(EntityType type) {

        if (creatureHealth.containsKey(type)) {
            return creatureHealth.get(type);
        }
        return 20;
    }

    public double getCreatureDamage(EntityType type) {

        if (creatureDamage.containsKey(type)) {
            return creatureDamage.get(type);
        }
        return 10.0;
    }

    public double getItemDamage(Material type) {

        if (itemDamage.containsKey(type)) {
            return itemDamage.get(type);
        }
        return 2.0;
    }

    public double getEnvironmentalDamage(EntityDamageEvent.DamageCause type) {

        if (environmentalDamage.containsKey(type)) {
            return environmentalDamage.get(type);
        }
        return 0.0;
    }

    public double getProjectileDamage(ProjectileType type) {

        if (projectileDamage.containsKey(type)) {
            return projectileDamage.get(type);
        }
        return 0.0;
    }

    /*/////////////////////////////////////////////////////////////////////////
    //    Bukkit Events are called beyond this line - put your buckets on!
    /////////////////////////////////////////////////////////////////////////*/

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {

        if (event.getEntity().hasMetadata("NPC")) {
            return;
        }
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
        if (event.getCause() == EntityDamageEvent.DamageCause.WITHER) {
            event.setCancelled(true);
            return;
        }

        CharacterTemplate character = plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity());

        try {
            double damage = event.getDamage();
            if (environmentalDamage.containsKey(event.getCause())) {
                if (plugin.getCommonConfig().environment_damage_in_percent) {
                    switch (event.getCause()) {

                        case FALL:
                            // the minecraft fall damage is caluclate like so: fall_damage = number of blocks - 3
                            double height = damage;
                            damage = (int) (character.getMaxHealth() * (environmentalDamage.get(event.getCause()) * height));
                            break;
                        case BLOCK_EXPLOSION:
                        case ENTITY_EXPLOSION:
                            // explosions are measured by power and how far the entity is away from the center
                            // a list of the power of each explosion is here http://www.minecraftwiki.net/wiki/Explosion#Properties
                            // default damage is as follows: 97 (charged creeper), 65 (TNT), 49 (creepers), 17 (fireballs)
                            // (1 x 1 + 1) defines the range the entity is away and how much the power gets reduced
                            // damage = (1 × 1 + 1) × 8 × power + 1
                            float power = (float) ((damage - 1) / 8);
                            damage = (int) (character.getMaxHealth() * (environmentalDamage.get(event.getCause()) * power));
                            break;
                        default:
                            damage = (int) (character.getMaxHealth() * environmentalDamage.get(event.getCause()));
                            break;
                    }
                } else {
                    damage = (int) (damage * environmentalDamage.get(event.getCause()));
                }
            }
            event.setCancelled(true);
            if (damage > 0) {
                EnvironmentAttack attack = new EnvironmentAttack(event, (int) damage);
                attack.run();
            }
        } catch (CombatException e) {
            if (character instanceof Hero) {
                ((Hero) character).sendMessage(ChatColor.RED + e.getMessage());
            }
        }
    }
}
