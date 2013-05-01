package de.raidcraft.skills.logging;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Bean;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.ExpPool;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.LevelableSkill;
import org.bukkit.Bukkit;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
@Entity
@Table(name = "skills_log_exp")
public class ExpLogger implements Bean {

    private static final Map<String, Map<ExpType, Integer>> expCache = new HashMap<>();

    public static void log(Levelable levelable, int expGained) {

        String player;
        ExpType type;
        if (levelable instanceof LevelableSkill) {
            player = ((LevelableSkill) levelable).getHolder().getName();
            type = ExpType.SKILL;
        } else if (levelable instanceof Profession) {
            player = ((Profession) levelable).getHero().getName();
            type = ExpType.PROFESSION;
        } else if (levelable instanceof Hero) {
            if (((Hero) levelable).getExpPool() instanceof ExpPool) {
                type = ExpType.EXP_POOL;
            } else {
                type = ExpType.HERO;
            }
            player = levelable.getName();
        } else {
            return;
        }
        player = player.toLowerCase();
        if (!expCache.containsKey(player)) {
            expCache.put(player, new HashMap<ExpType, Integer>());
        }
        if (expCache.get(player).containsKey(type)) {
            expGained += expCache.get(player).get(type);
        }
        expCache.get(player).put(type, expGained);
    }

    public static void startTask(SkillsPlugin plugin) {

        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {

                for (Map.Entry<String, Map<ExpType, Integer>> entry : expCache.entrySet()) {
                    for (Map.Entry<ExpType, Integer> typeEntry : entry.getValue().entrySet()) {
                        ExpLogger log = new ExpLogger();
                        log.setPlayer(entry.getKey());
                        log.setGainedExp(typeEntry.getValue());
                        log.setType(typeEntry.getKey().name());
                        log.setTimestamp(new Timestamp(System.currentTimeMillis()));
                        RaidCraft.getDatabase(SkillsPlugin.class).save(log);
                    }
                }
                expCache.clear();
            }
        }, plugin.getCommonConfig().log_interval * 20, plugin.getCommonConfig().log_interval * 20);
    }

    public enum ExpType {

        SKILL,
        HERO,
        PROFESSION,
        EXP_POOL
    }

    @Id
    private int id;
    private String player;
    private String type;
    private int gainedExp;
    private Timestamp timestamp;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getPlayer() {

        return player;
    }

    public void setPlayer(String player) {

        this.player = player;
    }

    public int getGainedExp() {

        return gainedExp;
    }

    public void setGainedExp(int gainedExp) {

        this.gainedExp = gainedExp;
    }

    public Timestamp getTimestamp() {

        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {

        this.timestamp = timestamp;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }
}
