package de.raidcraft.skills;

import com.avaje.ebean.Ebean;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.hero.SimpleHero;
import de.raidcraft.skills.tables.THero;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class HeroManager {

    private final SkillsPlugin plugin;
    private final Map<String, Hero> heroes = new HashMap<>();

    public HeroManager(SkillsPlugin plugin) {

        this.plugin = plugin;
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
}
