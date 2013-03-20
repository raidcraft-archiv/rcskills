package de.raidcraft.skills;

import com.avaje.ebean.Ebean;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.creature.Creature;
import de.raidcraft.skills.hero.SimpleHero;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroExpPool;
import de.raidcraft.skills.util.HeroUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Silthus
 */
public final class CharacterManager implements Listener {

    private final SkillsPlugin plugin;
    private final Map<String, Hero> heroes = new HashMap<>();
    private final Map<UUID, CharacterTemplate> characters = new HashMap<>();

    protected CharacterManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(this);
        startTasks();
    }

    public void reload() {

        for (Hero hero : heroes.values()) {
            hero.clearEffects();
            hero.save();
        }
        heroes.clear();
        for (CharacterTemplate character : characters.values()) {
            character.clearEffects();
        }
        characters.clear();
    }

    public void startTasks() {

        startUserInterfaceRefreshTask();
    }

    private void startUserInterfaceRefreshTask() {

        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {

                for (Player player : Bukkit.getOnlinePlayers()) {
                    getHero(player).getUserInterface().refresh();
                }
            }
        }, plugin.getCommonConfig().interface_update_interval, plugin.getCommonConfig().interface_update_interval);
    }

    public Hero getHero(String name, boolean cache) throws UnknownPlayerException {

        // lets try bukkit to autocomplete the name
        Player player = Bukkit.getPlayer(name);
        THero heroTable = null;
        if (player != null) {
            name = player.getName();
        } else {
            // try to find a match in the db
            heroTable = Ebean.find(THero.class).where().like("player", name).findUnique();
            if (heroTable == null) throw new UnknownPlayerException("Es gibt keinen Spieler mit dem Namen: " + name);
        }
//        name = name.toLowerCase();

        Hero hero;
        if (!heroes.containsKey(name)) {

            if (heroTable == null) heroTable = Ebean.find(THero.class).where().eq("player", name).findUnique();
            if (heroTable == null) {
                // create a new entry
                heroTable = new THero();
                heroTable.setPlayer(name);
                heroTable.setHealth(20);
                heroTable.setExp(0);
                heroTable.setLevel(0);
                Database.save(heroTable);
                // also create a new exp pool for the hero
                THeroExpPool pool = Ebean.find(THeroExpPool.class).where().eq("player", name).findUnique();
                if (pool == null) {
                    pool = new THeroExpPool();
                    pool.setPlayer(name);
                    pool.setHeroId(heroTable.getId());
                    Database.save(pool);
                }
                heroTable.setExpPool(pool);
                Database.save(heroTable);
            }
            hero = new SimpleHero(heroTable);
            if (cache) heroes.put(hero.getName().toLowerCase(), hero);
        } else {
            hero = heroes.get(name);
        }
        return hero;
    }

    public Hero getHero(String name) throws UnknownPlayerException {

        return getHero(name, true);
    }

    public Hero getHero(Player player, boolean cache) {

        try {
            return getHero(player.getName(), cache);
        } catch (UnknownPlayerException e) {
            // will never be thrown
            e.printStackTrace();
        }
        return null;
    }

    public Hero getHero(Player player) {

        return getHero(player, true);
    }

    public CharacterTemplate getCharacter(LivingEntity entity, boolean cache) {

        if (entity instanceof Player) {
            return getHero((Player) entity);
        }

        CharacterTemplate creature;
        if (!characters.containsKey(entity.getUniqueId())) {
            creature = new Creature(entity);
            if (cache) characters.put(entity.getUniqueId(), creature);
        } else {
            creature = characters.get(entity.getUniqueId());
        }
        return creature;
    }

    public CharacterTemplate getCharacter(LivingEntity entity) {

        return getCharacter(entity, true);
    }

    public void clearCacheOf(CharacterTemplate character) {

        LivingEntity entity = character.getEntity();
        if (entity != null) characters.remove(entity.getUniqueId());
        heroes.remove(character.getName());
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
        character.setHealth(damageManager.getCreatureHealth(entityType));
        character.setDamage(damageManager.getCreatureDamage(entityType));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {

        CharacterTemplate character = plugin.getCharacterManager().getCharacter(event.getEntity());
        character.clearEffects();
        if (!(character instanceof Hero)) {
            // lets remove that poor character from our cache... may he Rest in Peace :*(
            clearCacheOf(character);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {

        HeroUtil.clearCache(getHero(event.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        getHero(event.getPlayer()).reset();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {

        // init once to set the health from the db and so on
        Hero hero = plugin.getCharacterManager().getHero(event.getPlayer());
        hero.getUserInterface().refresh();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerGainExp(PlayerExpChangeEvent event) {

        // TODO: somehow manage the minecraft exp for enchanting and stuff
        event.setAmount(0);
    }
}
