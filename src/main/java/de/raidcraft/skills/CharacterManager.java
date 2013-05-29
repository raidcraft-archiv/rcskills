package de.raidcraft.skills;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.character.SkilledCharacter;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.api.ui.BukkitUserInterface;
import de.raidcraft.skills.creature.Creature;
import de.raidcraft.skills.hero.SimpleHero;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroExpPool;
import de.raidcraft.skills.util.HeroUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author Silthus
 */
public final class CharacterManager implements Listener {

    private final SkillsPlugin plugin;
    private final Map<String, Hero> heroes = new HashMap<>();
    private final Map<UUID, CharacterTemplate> characters = new HashMap<>();
    private final Map<Class<? extends CharacterTemplate>, Constructor<? extends CharacterTemplate>> cachedClasses = new HashMap<>();
    private final Set<String> pausedExpPlayers = new HashSet<>();

    private final Map<String, BukkitTask> queuedLoggedOutHeroes = new HashMap<>();

    protected CharacterManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(this);
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                RaidCraft.getComponent(SkillsPlugin.class),
                ConnectionSide.SERVER_SIDE,
                Packets.Server.SET_EXPERIENCE
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {

                if (pausedExpPlayers.contains(event.getPlayer().getName().toLowerCase())) {
                    return;
                }
                Hero hero = getHero(event.getPlayer());
                if (hero.getUserInterface() instanceof BukkitUserInterface) {
                    PacketContainer packetContainer = event.getPacket().deepClone();
                    ((BukkitUserInterface) hero.getUserInterface()).modifyExperiencePacket(packetContainer);
                    event.setPacket(packetContainer);
                }
            }
        });
    }

    public void pausePlayerExpUpdate(Player player) {

        pausedExpPlayers.add(player.getName().toLowerCase());
    }

    public void unpausePlayerExpUpdate(Player player) {

        pausedExpPlayers.remove(player.getName().toLowerCase());
    }

    public void reload() {

        for (Hero hero : heroes.values()) {
            hero.clearEffects();
            clearCacheOf(hero);
        }
        heroes.clear();
        for (CharacterTemplate character : characters.values()) {
            character.clearEffects();
        }
        characters.clear();
    }

    public Collection<Hero> getCachedHeroes() {

        return heroes.values();
    }

    public Hero getHero(Player player, String name) throws UnknownPlayerException {

        THero heroTable = null;
        if (player != null) {
            name = player.getName();
        } else {
            // try to find a match in the db
            heroTable = RaidCraft.getDatabase(SkillsPlugin.class).find(THero.class).where().like("player", name).findUnique();
            if (heroTable == null) throw new UnknownPlayerException("Es gibt keinen Spieler mit dem Namen: " + name);
        }
        name = name.toLowerCase();

        Hero hero;
        if (!heroes.containsKey(name)) {

            if (heroTable == null) heroTable = RaidCraft.getDatabase(SkillsPlugin.class).find(THero.class).where().eq("player", name).findUnique();
            if (heroTable == null) {
                // create a new entry
                heroTable = new THero();
                heroTable.setPlayer(name);
                heroTable.setHealth(20);
                heroTable.setExp(0);
                heroTable.setLevel(0);
                RaidCraft.getDatabase(SkillsPlugin.class).save(heroTable);
                // also create a new exp pool for the hero
                THeroExpPool pool = RaidCraft.getDatabase(SkillsPlugin.class).find(THeroExpPool.class).where().eq("player", name).findUnique();
                if (pool == null) {
                    pool = new THeroExpPool();
                    pool.setPlayer(name);
                    pool.setHeroId(heroTable.getId());
                    RaidCraft.getDatabase(SkillsPlugin.class).save(pool);
                }
                heroTable.setExpPool(pool);
                RaidCraft.getDatabase(SkillsPlugin.class).update(heroTable);
            }
            hero = new SimpleHero(player, heroTable);
            heroes.put(hero.getName().toLowerCase(), hero);
        } else {
            hero = heroes.get(name);
        }
        return hero;
    }

    public Hero getHero(String name) throws UnknownPlayerException {

        return getHero(Bukkit.getPlayer(name), name);
    }

    public Hero getHero(Player player) {

        try {
            return getHero(player, player.getName());
        } catch (UnknownPlayerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CharacterTemplate getCharacter(LivingEntity entity) {

        if (entity instanceof Player) {
            return getHero((Player) entity);
        }

        CharacterTemplate creature;
        if (!characters.containsKey(entity.getUniqueId())) {
            creature = new Creature(entity);
            characters.put(entity.getUniqueId(), creature);
        } else {
            creature = characters.get(entity.getUniqueId());
        }
        return creature;
    }

    /**
     * Spawns a new entity with a custom defined class.
     *
     * @param entityType to spawn
     * @param location to spawn the entity at
     * @param creatureClazz that defines the entity
     * @param args to pass to the constructor
     * @param <T> type of the entity to spawn
     * @return spawned entity of the defined class
     */
    @SuppressWarnings("unchecked")
    public <T extends CharacterTemplate> T spawnCharacter(EntityType entityType, Location location, Class<T> creatureClazz, Object... args) {

        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, entityType);
        // at this point the spawnEntity event was called
        // now lets remove the spawned entity from the UUID list and add our own
        characters.remove(entity.getUniqueId()).leaveParty();

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
            characters.put(character.getEntity().getUniqueId(), character);
            return character;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void queueHeroLogout(final Hero hero) {

        hero.save();
        BukkitTask task = queuedLoggedOutHeroes.remove(hero.getName().toLowerCase());
        if (task != null) {
            task.cancel();
        }
        if (plugin.getCommonConfig().hero_cache_timeout > 0) {
            task = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {

                    clearCacheOf(hero);
                }
            }, plugin.getCommonConfig().hero_cache_timeout * 20);
            queuedLoggedOutHeroes.put(hero.getName().toLowerCase(), task);
        } else {
            clearCacheOf(hero);
        }
    }

    /**
     * This methods removes the character from the cache in this class.
     * Do NOT use this to clear heroes from the cache! Use the {@link HeroUtil#clearCache(de.raidcraft.skills.api.hero.Hero)}
     * method instead!!!
     *
     * @param character to clear the cache for
     */
    public void clearCacheOf(CharacterTemplate character) {

        if (character instanceof Hero) {
            BukkitTask task = queuedLoggedOutHeroes.remove(character.getName().toLowerCase());
            if (task != null) {
                task.cancel();
            }
            HeroUtil.clearCache((Hero) character);
            heroes.remove(character.getName().toLowerCase());
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

    /*/////////////////////////////////////////////////////////////////////////
    //    Bukkit Events are called beyond this line - put your buckets on!
    /////////////////////////////////////////////////////////////////////////*/

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntitySpawn(CreatureSpawnEvent event) {

        CharacterTemplate character = getCharacter(event.getEntity());
        if (character instanceof Hero) {
            return;
        }
        DamageManager damageManager = plugin.getDamageManager();
        EntityType entityType = character.getEntity().getType();
        // lets set the health and damage of the entity
        int creatureHealth = damageManager.getCreatureHealth(entityType);
        character.setMaxHealth(creatureHealth);
        character.setHealth(creatureHealth);
        character.setDamage(damageManager.getCreatureDamage(entityType));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDeath(final EntityDeathEvent event) {

        // dispatch a task that does this with 1 tick delay in order to allow the event to clear properly
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {

                CharacterTemplate character = plugin.getCharacterManager().getCharacter(event.getEntity());
                character.clearEffects();
                if (!(character instanceof Hero)) {
                    // lets remove that poor character from our cache... may he Rest in Peace :*(
                    clearCacheOf(character);
                }
            }
        }, 1L);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {

        queueHeroLogout(getHero(event.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {

        queueHeroLogout(getHero(event.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        getHero(event.getPlayer()).reset();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {

        BukkitTask task = queuedLoggedOutHeroes.remove(event.getPlayer().getName().toLowerCase());
        if (task != null) {
            task.cancel();
        }
        // init once to set the health from the db and so on
        getHero(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onWorldChange(PlayerChangedWorldEvent event) {

        getHero(event.getPlayer()).save();
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
