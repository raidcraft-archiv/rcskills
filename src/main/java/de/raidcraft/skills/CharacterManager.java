package de.raidcraft.skills;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.character.SkilledCharacter;
import de.raidcraft.skills.api.events.RCEntityDeathEvent;
import de.raidcraft.skills.api.events.RE_PlayerStatusChangedEvent;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.creature.Creature;
import de.raidcraft.skills.effects.Summoned;
import de.raidcraft.skills.hero.SimpleHero;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroExpPool;
import de.raidcraft.skills.trigger.InvalidationTrigger;
import de.raidcraft.skills.util.HeroUtil;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author Silthus
 */
public final class CharacterManager implements Listener, Component {

    private final SkillsPlugin plugin;
    private final Map<UUID, Hero> heroes = new HashMap<>();
    private final Map<UUID, CharacterTemplate> characters = new HashMap<>();
    private final Map<Class<? extends CharacterTemplate>, Constructor<? extends CharacterTemplate>> cachedClasses = new HashMap<>();
    private final Set<String> pausedExpPlayers = new HashSet<>();
    private final Map<UUID, BukkitTask> queuedLoggedOutHeroes = new HashMap<>();
    private final Map<String, BukkitTask> queuedPvPToggle = new CaseInsensitiveMap<>();

    protected CharacterManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        RaidCraft.registerComponent(CharacterManager.class, this);
        plugin.registerEvents(this);
        startRefreshTask();
        startValidationTask();
    }

    private void startRefreshTask() {

        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                    @Override
                    public void run() {

                        for (Hero hero : getCachedHeroes()) {
                            if (hero.isOnline()) {
                                hero.getUserInterface().refresh();
                            }
                        }
                    }
                },
                plugin.getCommonConfig().userinterface_refresh_interval,
                plugin.getCommonConfig().userinterface_refresh_interval
        );
    }

    private void startValidationTask() {

        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                    @Override
                    public void run() {

                        for (CharacterTemplate character : new ArrayList<>(characters.values())) {
                            if (character.getEntity() == null || !character.getEntity().isValid()) {
                                TriggerManager.callSafeTrigger(new InvalidationTrigger(character));
                                if (character.getEntity() != null) characters.remove(character.getEntity().getUniqueId());
                            }
                        }
                    }
                },
                plugin.getCommonConfig().character_invalidation_interval,
                plugin.getCommonConfig().character_invalidation_interval
        );
    }

    public Collection<Hero> getCachedHeroes() {

        return heroes.values();
    }

    public static void refreshPlayerTag(CharacterTemplate template) {

        if (!(template instanceof Hero) || !((Hero) template).isOnline()) {
            return;
        }
        // lets refresh all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            RaidCraft.callEvent(new RE_PlayerStatusChangedEvent(player));
        }
    }

    public boolean isPvPToggleQueued(final Hero hero) {

        return queuedPvPToggle.containsKey(hero.getName());
    }

    public void queuePvPToggle(final Hero hero, final boolean enabled) {

        removeQueuedPvPToggle(hero);
        queuedPvPToggle.put(hero.getName(), Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {

                hero.setPvPEnabled(enabled);
            }
        }, (long) (plugin.getCommonConfig().pvp_toggle_delay * 20)));
    }

    public void removeQueuedPvPToggle(Hero hero) {

        BukkitTask task = queuedPvPToggle.remove(hero.getName());
        if (task != null) {
            task.cancel();
        }
    }

    public void pausePlayerExpUpdate(Player player) {

        pausedExpPlayers.add(player.getName().toLowerCase());
    }

    public void unpausePlayerExpUpdate(Player player) {

        pausedExpPlayers.remove(player.getName().toLowerCase());
    }

    // handle some tag api stuff here

    public boolean isPausingPlayerExpUpdate(Player player) {

        return pausedExpPlayers.contains(player.getName().toLowerCase());
    }

    public boolean isPlayerCached(UUID playerId) {

        return heroes.containsKey(playerId);
    }

    // tag api end

    @Deprecated
    public Hero getHero(String playerName) {

        return getHero(UUIDUtil.convertPlayer(playerName));
    }

    // TODO: add exception, and create not a new player if command wrong
    public Hero getHero(Player player) {

        if (player == null) {
            try {
                throw new UnknownPlayerException("getHero: Player is null");
            } catch (UnknownPlayerException e) {
                e.printStackTrace();
            }
            return null;
        }
        UUID player_id = player.getUniqueId();
        Hero hero = heroes.get(player_id);
        if (hero == null) {
            BukkitTask task = queuedLoggedOutHeroes.remove(player_id);
            if (task != null) {
                task.cancel();
            }
            // try to load hero
            THero heroTable = RaidCraft.getDatabase(SkillsPlugin.class).find(THero.class)
                    .where().eq("player_id", player_id).findUnique();
            // create a new entry if not exists
            if (heroTable == null) {
                heroTable = new THero();
                heroTable.setPlayerId(player_id);
                heroTable.setHealth(20);
                heroTable.setExp(0);
                heroTable.setLevel(0);
                plugin.getDatabase().save(heroTable);
            }

            THeroExpPool pool = plugin.getDatabase().find(THeroExpPool.class)
                    .where().eq("player_id", player_id).findUnique();
            // also create a new exp pool for the hero
            if (pool == null) {
                pool = new THeroExpPool();
                pool.setPlayerId(player_id);
                plugin.getDatabase().save(pool);
            }
            pool.setHeroId(heroTable.getId());
            plugin.getDatabase().update(pool);

            heroTable.setExpPool(pool);
            plugin.getDatabase().update(heroTable);

            hero = new SimpleHero(player, heroTable);
            heroes.put(player_id, hero);
            hero.checkArmor();
            hero.checkWeapons();
        }
        return hero;
    }

    public Hero getHero(UUID player_id) {

        return getHero(Bukkit.getPlayer(player_id));
    }

    /**
     * Spawns a new entity with a custom defined class.
     * @param entityType to spawn
     * @param location to spawn the entity at
     * @param creatureClazz that defines the entity
     * @param args to pass to the constructor
     * @param <T> type of the entity to spawn
     * @return spawned entity of the defined class
     */
    @SuppressWarnings("unchecked")
    public <T extends CharacterTemplate> T spawnCharacter(EntityType entityType, Location location, Class<T> creatureClazz, Object... args) {

        return wrapCharacter((LivingEntity) location.getWorld().spawnEntity(location, entityType), creatureClazz, args);
    }

    @SuppressWarnings("unchecked")
    public <T extends CharacterTemplate> T wrapCharacter(LivingEntity entity, Class<T> creatureClazz, Object... args) {

        // at this point the spawnEntity event was called but we dont always handle it, so lets check if we have it cached
        if (characters.containsKey(entity.getUniqueId())) {
            if (creatureClazz.isInstance(characters.get(entity.getUniqueId()))) {
                return (T) characters.get(entity.getUniqueId());
            }
            clearCacheOf(characters.remove(entity.getUniqueId()));
        }

        if (!cachedClasses.containsKey(creatureClazz)) {
            // lets find the matching constructor
            for (Constructor constructor : creatureClazz.getDeclaredConstructors()) {
                boolean match = true;
                if (constructor.getParameterTypes().length != args.length + 1) {
                    continue;
                }
                if (!LivingEntity.class.isAssignableFrom(constructor.getParameterTypes()[0])) {
                    continue;
                }
                for (int i = 1; i < args.length; i++) {
                    if (!constructor.getParameterTypes()[i].isAssignableFrom(args[i].getClass())) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    cachedClasses.put(creatureClazz, constructor);
                }
            }
        }
        Constructor<T> constructor = (Constructor<T>) cachedClasses.get(creatureClazz);
        // lets do some reflection to instantiate the custom class
        constructor.setAccessible(true);
        try {
            // we need to construct a new array or the constructor will handle our args as array
            Object[] objects = new Object[args.length + 1];
            objects[0] = entity;
            System.arraycopy(args, 0, objects, 1, objects.length - 1);
            // we also pass in the living entity
            T character = constructor.newInstance(objects);
            character.updateEntity(entity);
            characters.put(character.getEntity().getUniqueId(), character);
            return character;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @EventHandler(ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {

        // remove the cached entities
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof Player && !entity.hasMetadata("NPC")) {
                continue;
            }
            if (entity instanceof LivingEntity && !entity.hasMetadata("NPC")) {
                clearCacheOf(getCharacter((LivingEntity) entity));
            }
        }
    }

    /**
     * This methods removes the character from the cache in this class.
     * Do NOT use this to clear heroes from the cache! Use the {@link HeroUtil#clearCache(de.raidcraft.skills.api.hero.Hero)}
     * method instead!!!
     * @param character to clear the cache for
     */
    public void clearCacheOf(CharacterTemplate character) {

        if (character instanceof Hero) {
            BukkitTask task = queuedLoggedOutHeroes.remove(((Hero) character).getPlayer().getUniqueId());
            if (task != null) {
                task.cancel();
            }
            HeroUtil.clearCache((Hero) character);
            heroes.remove(((Hero) character).getPlayer().getUniqueId());
            return;
        }
        LivingEntity entity = character.getEntity();
        if (entity != null) characters.remove(entity.getUniqueId());
        character.leaveParty();
        if (character instanceof SkilledCharacter) {
            for (Object ability : ((SkilledCharacter) character).getAbilties()) {
                if (ability instanceof Triggered) {
                    TriggerManager.unregisterListeners((Triggered) ability);
                }
            }
        }
        plugin.getSkillManager().clearSkillCache(character.getName());
    }

    @Nullable
    public CharacterTemplate getCharacter(UUID uuid) {

        return characters.get(uuid);
    }

    public CharacterTemplate getCharacter(LivingEntity entity) {

        if (entity == null || entity.hasMetadata("NPC")) {
            return null;
        }
        if (entity instanceof Player) {
            return getHero((Player) entity);
        }

        CharacterTemplate creature;
        if (!characters.containsKey(entity.getUniqueId())) {
            creature = new Creature(entity);
            // load all custom properties
            DamageManager damageManager = plugin.getDamageManager();
            EntityType entityType = creature.getEntity().getType();
            // lets set the health and damage of the entity
            int creatureHealth = damageManager.getCreatureHealth(entityType);
            creature.setMaxHealth(creatureHealth);
            creature.setHealth(creatureHealth);
            creature.setDamage(damageManager.getCreatureDamage(entityType));
            creature.setInCombat(false);
            // cache the character
            characters.put(entity.getUniqueId(), creature);
        } else {
            return getCharacter(entity.getUniqueId());
        }
        return creature;
    }

    /*/////////////////////////////////////////////////////////////////////////
    //    Bukkit Events are called beyond this line - put your buckets on!
    /////////////////////////////////////////////////////////////////////////*/

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDeath(final EntityDeathEvent event) {

        if (event.getEntity() instanceof Player) {
            return;
        }
        final CharacterTemplate character = plugin.getCharacterManager().getCharacter(event.getEntity());
        if (character.hasEffect(Summoned.class)) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }
        // dispatch a task that does this with 1 tick delay in order to allow the event to clear properly
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {

                character.clearEffects();
                if (!(character instanceof Hero)) {
                    // lets remove that poor character from our cache... may he Rest in Peace :*(
                    clearCacheOf(character);
                }
            }
        }, 1L);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(RCEntityDeathEvent event) {

        if (event.getCharacter() instanceof Hero) {
            event.getCharacter().getParty().sendMessage(ChatColor.RED + event.getCharacter().getName() + " ist gestorben.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {

        Hero hero = getHero(event.getPlayer());
        Scoreboards.updatePlayerTeam(hero);
        queueHeroLogout(hero);
    }

    public void queueHeroLogout(final Hero hero) {

        hero.save();
        BukkitTask task = queuedLoggedOutHeroes.remove(hero.getPlayer().getUniqueId());
        if (task != null) {
            task.cancel();
        }
        if (plugin.getCommonConfig().hero_cache_timeout > 0) {
            task = Bukkit.getScheduler().runTaskLater(plugin, () -> {

                clearCacheOf(hero);
            }, plugin.getCommonConfig().hero_cache_timeout * 20);
            queuedLoggedOutHeroes.put(hero.getPlayer().getUniqueId(), task);
        } else {
            clearCacheOf(hero);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {

        queueHeroLogout(getHero(event.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        Hero hero = getHero(event.getPlayer());
        hero.updateEntity(event.getPlayer());
        hero.reset();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {

        Hero hero = getHero(event.getPlayer());
        hero.updateEntity(event.getPlayer());
        hero.updatePermissions();
        Scoreboards.updatePlayerTeam(hero);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onWorldChange(PlayerChangedWorldEvent event) {

        Hero hero = getHero(event.getPlayer());
        hero.updateEntity(event.getPlayer());
        hero.save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onHealthChange(EntityRegainHealthEvent event) {

        // lets always cancel this on the lowest level to allow other handles to overwrite
        switch (event.getRegainReason()) {

            case EATING:
            case REGEN:
            case SATIATED:
                event.setCancelled(true);
                break;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onExpGain(PlayerExpChangeEvent event) {

        if (!pausedExpPlayers.contains(event.getPlayer().getName().toLowerCase())) {
            event.setAmount(0);
        }
    }
}