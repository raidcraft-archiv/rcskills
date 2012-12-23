package de.raidcraft.skills;

import com.avaje.ebean.Ebean;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.creature.Creature;
import de.raidcraft.skills.hero.SimpleHero;
import de.raidcraft.skills.tables.THero;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
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

    public Hero getHero(String name) throws UnknownPlayerException {

        Hero hero;
        if (!heroes.containsKey(name)) {
            // lets try bukkit to autocomplete the name
            Player player = Bukkit.getPlayer(name);
            if (player != null) name = player.getName();

            THero heroTable = Ebean.find(THero.class).where().eq("player", name).findUnique();
            if (heroTable == null) {
                // create a new entry
                heroTable = new THero();
                heroTable.setPlayer(name);
                heroTable.setExp(0);
                heroTable.setLevel(0);
                heroTable.setHealth(20);
                Database.save(heroTable);
            }
            hero = new SimpleHero(heroTable);
            heroes.put(hero.getName(), hero);
        } else {
            hero = heroes.get(name);
        }
        return hero;
    }

    public Hero getHero(Player player) {

        try {
            return getHero(player.getName());
        } catch (UnknownPlayerException e) {
            // will never be thrown
            e.printStackTrace();
        }
        return null;
    }

    public CharacterTemplate getCharacter(LivingEntity entity) {

        if (entity instanceof Player) {
            return getHero((Player) entity);
        }

        if (!characters.containsKey(entity.getUniqueId())) {
            characters.put(entity.getUniqueId(), new Creature(entity));
        }
        return characters.get(entity.getUniqueId());
    }

    public void clearCacheOf(CharacterTemplate character) {

        characters.remove(character.getEntity().getUniqueId());
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
        // lets set the health and damage of the entity
        character.setHealth(plugin.getDamageManager().getCreatureHealth(character.getEntity().getType()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {

        // save the hero first
        getHero(event.getPlayer()).save();
        // lets clear the cache for the hero
        heroes.remove(event.getPlayer().getName());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        getHero(event.getPlayer()).reset();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {

        // init once to set the health from the db and so on
        getHero(event.getPlayer());
    }
}
