package de.raidcraft.skills;

import com.avaje.ebean.Ebean;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
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

    public Hero getHero(String name) throws UnknownPlayerException, UnknownProfessionException {

        Hero hero;
        if (!heroes.containsKey(name)) {
            // lets try bukkit to autocomplete the name
            Player player = Bukkit.getPlayer(name);
            if (player != null) name = player.getName();

            hero = new SimpleHero(Ebean.find(THero.class).where().eq("player", name).findUnique());
            heroes.put(hero.getUserName(), hero);
        } else {
            hero = heroes.get(name);
        }
        return hero;
    }

    public Hero getHero(Player player) throws UnknownProfessionException {

        try {
            return getHero(player.getName());
        } catch (UnknownPlayerException e) {
            // will never be thrown
            e.printStackTrace();
        }
        return null;
    }
}
