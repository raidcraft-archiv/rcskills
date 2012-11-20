package de.raidcraft.skills;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.Levelable;
import de.raidcraft.skills.api.bukkit.BukkitListenerAdapter;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.persistance.StorageType;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.config.ProfessionConfig;
import de.raidcraft.skills.professions.ProfessionManager;
import de.raidcraft.skills.skills.SkillManager;
import de.raidcraft.skills.tables.skills.PermissionSkillsTable;
import de.raidcraft.skills.tables.skills.PlayerSkillsLevelTable;
import de.raidcraft.skills.tables.skills.PlayerSkillsTable;
import de.raidcraft.skills.tables.skills.SkillsTable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Silthus
 */
public class SkillsPlugin extends BasePlugin implements Component, Listener {

    private SkillManager skillManager;
    private ProfessionManager professionManager;
    private LocalConfiguration configuration;
    private ProfessionConfig professionConfig;

    @Override
    public void enable() {

        // create the config
        this.configuration = new LocalConfiguration(this);
        this.professionConfig = new ProfessionConfig(this);
        // lets register the database
        registerTable(SkillsTable.class, new SkillsTable());
        registerTable(PlayerSkillsTable.class, new PlayerSkillsTable());
        registerTable(PlayerSkillsLevelTable.class, new PlayerSkillsLevelTable());
        registerTable(PermissionSkillsTable.class, new PermissionSkillsTable());
        // register our events
        registerEvents(new BukkitListenerAdapter(this));
        registerEvents(this);
        // the skill manager takes care of all skills currently loaded
        this.skillManager = new SkillManager(this);
        this.professionManager = new ProfessionManager(this);
        // register ourself as a RPG Component
        RaidCraft.registerComponent(SkillsPlugin.class, this);
    }

    @Override
    public void disable() {


    }

    public SkillManager getSkillManager() {

        return skillManager;
    }

    public ProfessionManager getProfessionManager() {

        return professionManager;
    }

    public LocalConfiguration getLocalConfiguration() {

        return configuration;
    }

    public ProfessionConfig getProfessionConfig() {

        return professionConfig;
    }

    public static class LocalConfiguration extends ConfigurationBase {

        @Setting("op-all-permissions")
        public boolean allow_op;
        @Setting("storage-type")
        public StorageType storage_type;
        @Setting("config-type")
        public StorageType config_type;

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
            Skill skill = player.getComponent(RCHero.class).getSkill(1);
            player.sendMessage("SkillId: " + skill.getId());
            player.sendMessage("Name: " + skill.getName());
            player.sendMessage("Description: " + skill.getDescription());
            player.sendMessage(skill.getUsage());
            if (skill instanceof Levelable) {
                Levelable levelable = (Levelable) skill;
                player.sendMessage("Level: " + levelable.getLevel());
                player.sendMessage("Exp: " + levelable.getExp() + "/" + levelable.getMaxExp());
                player.sendMessage("MaxLevel: " + levelable.getMaxLevel());
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
