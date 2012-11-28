package de.raidcraft.skills;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.commands.CastCommand;
import de.raidcraft.skills.commands.SkillsCommand;
import de.raidcraft.skills.skills.magic.Fireball;
import de.raidcraft.skills.skills.misc.PermissionSkill;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroProfession;
import de.raidcraft.skills.tables.THeroSkill;
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

    @Override
    public void enable() {

        // create the config
        this.configuration = configure(new LocalConfiguration(this));
        // register our events
        registerEvents(this);
        // the skill manager takes care of all skills currently loaded
        this.skillManager = new SkillManager(this);
        // register our inhouse skills
        registerSkills();
        // these managers can only be loaded after the skill manager
        this.professionManager = new ProfessionManager(this);
        this.heroManager = new HeroManager(this);
        // and commands gogogo
        registerCommands(SkillsCommand.class);
        registerCommands(CastCommand.class);
        // register ourself as a RPG Component
        RaidCraft.registerComponent(SkillsPlugin.class, this);
    }

    @Override
    public void disable() {


    }

    private void registerSkills() {

        SkillManager m = getSkillManager();
        m.registerSkill(Fireball.class);
        m.registerSkill(PermissionSkill.class);
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

    public LocalConfiguration getCommonConfig() {

        return configuration;
    }

    public static class LocalConfiguration extends ConfigurationBase {

        @Setting("op-all-permissions")
        public boolean allow_op;
        @Setting("max-player-level")
        public int max_player_level;

        public LocalConfiguration(BasePlugin plugin) {

            super(plugin, "config.yml");
        }
    }

    /*///////////////////////////////////////////////////////////////
    //          All Bukkit Events are handled here
    ///////////////////////////////////////////////////////////////*/

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {

        try {
            getHeroManager().getHero(event.getPlayer()).save();
        } catch (UnknownProfessionException e) {
            getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
    }
}
