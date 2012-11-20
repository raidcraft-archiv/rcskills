package de.raidcraft.skills.hero;

import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.tables.PlayerTable;

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
            hero = new RCHero(Database.getTable(PlayerTable.class).getHeroData(name));
            heroes.put(hero.getName(), hero);
        } else {
            hero = heroes.get(name);
        }
        return hero;
    }
}
