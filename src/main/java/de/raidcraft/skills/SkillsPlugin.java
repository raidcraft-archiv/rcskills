package de.raidcraft.skills;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.persistance.SkillData;
import de.raidcraft.skills.api.persistance.StorageType;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.config.ProfessionConfig;
import de.raidcraft.skills.config.SkillConfig;
import de.raidcraft.skills.hero.HeroManager;
import de.raidcraft.skills.hero.RCHero;
import de.raidcraft.skills.professions.ProfessionManager;
import de.raidcraft.skills.skills.SkillManager;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroProfession;
import de.raidcraft.skills.tables.THeroSkill;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class SkillsPlugin extends BasePlugin implements Component, Listener {

    private SkillManager skillManager;
    private ProfessionManager professionManager;
    private HeroManager heroManager;
    private LocalConfiguration configuration;
    private ProfessionConfig professionConfig;

    @Override
    public void enable() {

        // create the config
        this.configuration = configure(new LocalConfiguration(this));
        // register our events
        registerEvents(this);
        // the skill manager takes care of all skills currently loaded
        this.skillManager = new SkillManager(this);
        this.professionManager = new ProfessionManager(this);
        this.heroManager = new HeroManager(this);
        // register ourself as a RPG Component
        RaidCraft.registerComponent(SkillsPlugin.class, this);
    }

    @Override
    public void disable() {


    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> classes = new ArrayList<>();
        classes.add(THero.class);
        classes.add(THeroProfession.class);
        classes.add(THeroSkill.class);
        return classes;
    }

    public SkillManager getSkillManager() {

        return skillManager;
    }

    public ProfessionManager getProfessionManager() {

        return professionManager;
    }

    public HeroManager getHeroManager() {

        return heroManager;
    }

    public LocalConfiguration getLocalConfiguration() {

        return configuration;
    }

    public ProfessionConfig getProfessionConfig(Hero hero, String name) throws UnknownProfessionException {

        return new ProfessionConfig(this, hero, name);
    }

    public Hero getHero(String name) throws UnknownPlayerException, UnknownProfessionException {

        return getHeroManager().getHero(name);
    }

    public SkillData getSkillConfig(Hero hero, String name) {

        return new SkillConfig(this, hero, name);
    }

    public static class LocalConfiguration extends ConfigurationBase {

        @Setting("op-all-permissions")
        public boolean allow_op;
        @Setting("storage-type")
        public StorageType storage_type;
        @Setting("config-type")
        public StorageType config_type;
        @Setting("player-max-level")
        public int player_max_level;

        public LocalConfiguration(BasePlugin plugin) {

            super(plugin, "config.yml");
        }
    }

    /*///////////////////////////////////////////////////////////////
    //          All Bukkit Events are handled here
    ///////////////////////////////////////////////////////////////*/

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

        try {
            RCPlayer player = RaidCraft.getPlayer(event.getPlayer());
            Skill skill = player.getComponent(RCHero.class).getSkill("test");
            player.sendMessage("SkillId: " + skill.getId());
            player.sendMessage("Name: " + skill.getName());
            player.sendMessage("Description: " + skill.getDescription());
            player.sendMessage(skill.getUsage());
            if (skill instanceof Level) {
                Level level = (Level) skill;
                player.sendMessage("Level: " + level.getLevel());
                player.sendMessage("Exp: " + level.getExp() + "/" + level.getMaxExp());
                player.sendMessage("MaxLevel: " + level.getMaxLevel());
            }
        } catch (UnknownSkillException e) {
            getLogger().info(e.getMessage());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {

        RaidCraft.getPlayer(event.getPlayer()).getComponent(RCHero.class).saveSkills();
    }
}
