package de.raidcraft.skills.api.combat;

import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import net.minecraft.server.EntityLiving;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Silthus
 */
public final class CombatManager implements Listener {

    private static final Random RANDOM = new Random();

    private final SkillsPlugin plugin;
    private final Map<EntityType, Integer> entityDamage = new EnumMap<>(EntityType.class);
    private final Map<EntityType, Integer> entityHealth = new EnumMap<>(EntityType.class);
    private final Map<LivingEntity, Set<Effect>> appliedEffects = new HashMap<>();
    private final List<SourcedCallback> rangeCallbacks = new ArrayList<>();
    // reflection field of the NMS EntityLiving class
    private Field nmsHealth = null;

    public CombatManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(this);
        loadEntityConfig();
        try {
            // make the health field in NMS accessible
            this.nmsHealth = EntityLiving.class.getDeclaredField("health");
            this.nmsHealth.setAccessible(true);
        } catch (NoSuchFieldException ignored) { }
    }

    private void loadEntityConfig() {

        // TODO: replace with the DamageManager
        // lets load the health and damage values from the configs
        File file = new File(plugin.getDataFolder(), "entity-config.yml");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (EntityType type : EntityType.values()) {

                if (type != null && type.getEntityClass() != null) {
                    // dont write stuff for non LivingEntities
                    if (LivingEntity.class.isAssignableFrom(type.getEntityClass())) {
                        ConfigurationSection section = config.getConfigurationSection(type.name());
                        if (!config.isConfigurationSection(type.name())) {
                            section = config.createSection(type.name());
                        }
                        // create some defaults if they dont exist
                        if (!section.isSet("damage")) section.set("damage", 0);
                        if (!section.isSet("health")) section.set("health", 0);

                        int damage = section.getInt("damage", 0);
                        int health = section.getInt("health", 0);
                        if (damage > 0) {
                            entityDamage.put(type, damage);
                        }
                        if (health > 0) {
                            entityHealth.put(type, health);
                        }
                    }
                }
            }
            // save the config with the written defaults (if any)
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Error when handling Entity Config: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*//////////////////////////////////////////////////////////////////////////////
    //      Hooked Bukkit events for handling all the combat stuff
    //////////////////////////////////////////////////////////////////////////////*/

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {

        // we need to remove entites that died from the effect list
        if (appliedEffects.containsKey(event.getEntity())) {
            appliedEffects.remove(event.getEntity());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        // check if the entity was damaged by a projectile
        if ((event.getDamager() instanceof Projectile)) {
            // and go thru all registered callbacks
            for (SourcedCallback sourcedCallback : new ArrayList<>(rangeCallbacks)) {
                if (sourcedCallback.getSource().equals(((Projectile) event.getDamager()).getShooter())) {
                    try {
                        // the shooter is our source so lets call back and remove
                        sourcedCallback.getCallback().run((LivingEntity) entity);
                        rangeCallbacks.remove(sourcedCallback);
                    } catch (CombatException e) {
                        // print to console
                        plugin.getLogger().info(e.getMessage());
                    }
                }
            }

        }

        // handle all damage done to heroes
        if (event.getEntity() instanceof Player) {
            damageHero(event, plugin.getHeroManager().getHero((Player) event.getEntity()));
        // modify damage done by players to any non heroes
        } else if (event.getDamager() instanceof Player
                && event.getEntity() instanceof LivingEntity) {
            // get the attacker hero
            Hero attacker = plugin.getHeroManager().getHero((Player) event.getDamager());
            int damage = attacker.getDamage();
            if (damage > 0) {
                event.setDamage(damage);
            }
            attacker.debug(damage + " damage inflicted - EVENT");
        }
    }

    private void damageHero(EntityDamageByEntityEvent event, Hero victim) {

        int oldHealth = victim.getHealth();
        int newHealth = oldHealth;

        // lets modify the damage done by creatures to players
        if (event.getDamager() instanceof Creature) {
            if (entityDamage.containsKey(event.getDamager().getType())) {
                newHealth = oldHealth - entityDamage.get(event.getDamager().getType());
            }
        } else if (event.getDamager() instanceof Player) {
            Hero attacker = plugin.getHeroManager().getHero((Player) event.getDamager());
            newHealth = oldHealth - attacker.getDamage();
        }

        // set the damage to 0 and remove virtual lives from the player
        event.setDamage(0);
        if (newHealth <= 0) {
            victim.setHealth(0);
            victim.kill((LivingEntity) event.getDamager());
        } else {
            victim.setHealth(newHealth);
        }
        victim.debug(oldHealth - newHealth + " damage taken - EVENT");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntitySpawn(CreatureSpawnEvent event) {

        // lets set the health and damage of the entity
        if (entityHealth.containsKey(event.getEntityType())) {
            setHealth(event.getEntity(), entityHealth.get(event.getEntityType()));
        }
    }

    public void addEffect(final Effect effect, final Hero source, final LivingEntity target) {

        if (!appliedEffects.containsKey(target)) {
            appliedEffects.put(target, new HashSet<Effect>());
        }
        final Set<Effect> effects = appliedEffects.get(target);
        // check for already existing effects of the same type
        if (effects.contains(effect)) {
            // lets cancel the old effect first
            for (Effect e : effects) {
                if (e.equals(effect)) {
                    // lets check if the effect is stronger and if yes cancel the old one
                    if (effect.getPriority() > e.getPriority()) {
                        source.debug("removed weaker effect " + e.getName() + ":" + e.getPriority());
                        Bukkit.getScheduler().cancelTask(e.getTaskId());
                    } else {
                        // tell the hero that a stronger effect of the same type is active
                        source.sendMessage(ChatColor.RED + "Es ist bereits ein stärkerer Effect vom selben Typ aktiv.");
                        return;
                    }
                }
            }
        }
        // apply the effect to the target and start the scheduler
        effect.setTaskId(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {

                try {
                    effect.apply(source, target);
                    effect.increaseDuration();
                    source.debug("applied effect: " + effect.getName() + ":" + effect.getPriority());
                } catch (CombatException e) {
                    // TODO: catch exception
                }
            }
        }, effect.getDelay(), effect.getInterval()));
        // start the cancel task if the duration is > -1
        if (effect.getTotalDuration() > -1) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {

                    Bukkit.getScheduler().cancelTask(effect.getTaskId());
                    effects.remove(effect);
                    source.debug("removed effect - ended: " + effect.getName() + ":" + effect.getPriority());
                }
                // we choose this values because we want to cancel after the effect ticked at least once
            }, effect.getTotalDuration() + effect.getDelay() + effect.getInterval());
        }
        // add the new effect to our applied list
        effects.add(effect);
    }

    public void damageEntity(LivingEntity source, LivingEntity target, int damage, Callback callback) throws CombatException {

        damageEntity(source, target, damage);
        // we need to check if it is a projectile or not
        if (callback instanceof RangedCallback) {
            castRangeAttack(source, callback);
        } else {
            // lets call it directly
            callback.run(target);
        }
    }

    public void castRangeAttack(LivingEntity source, Callback callback) {

        // lets add it to the listener
        final SourcedCallback cb = new SourcedCallback(source, callback);
        rangeCallbacks.add(cb);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {

                rangeCallbacks.remove(cb);
            }
        }, plugin.getCommonConfig().callback_purge_time);
    }

    public void knockBack(LivingEntity attacker, LivingEntity target, double power) {

        // knocks back the target based on the attackers center position
        Location knockBackCenter = attacker.getLocation();
        double xOff = target.getLocation().getX() - knockBackCenter.getX();
        double yOff = target.getLocation().getY() - knockBackCenter.getY();
        double zOff = target.getLocation().getZ() - knockBackCenter.getZ();
        // power is the velocity applied to the target
        // a power of 0.4 is a player jumping
        target.setVelocity(new Vector(xOff, yOff, zOff).normalize().multiply(power));
    }

    public void damageEntity(LivingEntity attacker, LivingEntity target, int damage) throws CombatException {

        damageEntity(attacker, target, damage, EntityDamageEvent.DamageCause.CUSTOM);
    }

    public void damageEntity(LivingEntity attacker, LivingEntity target, int damage, EntityDamageEvent.DamageCause cause) throws CombatException {

        if (target.getHealth() <= 0) {
            throw new CombatException("Ziel ist bereits tot.");
        }

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(attacker, target, cause, damage);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            throw new CombatException("Ziel kann nicht angegriffen werden!");
        }

        int oldHealth = target.getHealth();
        int newHealth = oldHealth - event.getDamage();
        if (newHealth < 0) {
            newHealth = 0;
        }

        target.setLastDamageCause(event);
        target.setLastDamage(event.getDamage());
        target.playEffect(EntityEffect.HURT);
        // set the health of the target
        setHealth(target, newHealth);

        if (attacker instanceof Player) {
            plugin.getHeroManager().getHero((Player) attacker).debug(oldHealth - newHealth + " damage inflicted");
        } else if (target instanceof Player) {
            plugin.getHeroManager().getHero((Player) target).debug(oldHealth - newHealth + " damage taken");
        }

        if (newHealth <= 0) {

            // play the death sound
            target.getWorld().playSound(target.getLocation(), getDeathSound(target.getType()), 1.0F, getSoundStrength(target));
            // play the death effect
            target.playEffect(EntityEffect.DEATH);
        } else {

            if (target instanceof Wolf) {
                Wolf wolf = (Wolf) target;
                wolf.setAngry(true);
                wolf.setTarget(attacker);
            } else if (target instanceof PigZombie) {
                PigZombie pigZombie = (PigZombie) target;
                pigZombie.setAngry(true);
                pigZombie.setTarget(attacker);
            } else if (target instanceof Creature) {
                ((Creature) target).setTarget(attacker);
            }
        }
    }

    private void setHealth(LivingEntity entity, int amount) {

        // lets do some relfection action
        try {
            nmsHealth.setInt(((CraftLivingEntity) entity).getHandle(), amount);
        } catch (IllegalAccessException e) {
            entity.setHealth(amount);
            e.printStackTrace();
        }
    }

    private float getSoundStrength(LivingEntity target) {

        if (!(target instanceof Ageable)) {
            return 1.0F;
        }
        if (((Ageable) target).isAdult()) {
            return (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2F + 1.0F;
        } else {
            return (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2F + 1.5F;
        }
    }

    private Sound getDeathSound(EntityType type) {

        switch (type) {

            case COW:
                return Sound.COW_IDLE;
            case BLAZE:
                return Sound.BLAZE_DEATH;
            case CHICKEN:
                return Sound.CHICKEN_HURT;
            case CREEPER:
                return Sound.CREEPER_DEATH;
            case SKELETON:
                return Sound.SKELETON_DEATH;
            case IRON_GOLEM:
                return Sound.IRONGOLEM_DEATH;
            case GHAST:
                return Sound.GHAST_DEATH;
            case PIG:
                return Sound.PIG_DEATH;
            case OCELOT:
                return Sound.CAT_HIT;
            case SHEEP:
                return Sound.SHEEP_IDLE;
            case SPIDER:
            case CAVE_SPIDER:
                return Sound.SPIDER_DEATH;
            case WOLF:
                return Sound.WOLF_DEATH;
            case ZOMBIE:
                return Sound.ZOMBIE_DEATH;
            case PIG_ZOMBIE:
                return Sound.ZOMBIE_PIG_DEATH;
            default:
                return Sound.HURT_FLESH;
        }
    }
}
