package de.raidcraft.skills;

import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.Depend;
import com.zachsthings.libcomponents.InjectComponent;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;
import de.raidcraft.componentutils.database.Database;
import de.raidcraft.rcrpg.RaidCraft;
import de.raidcraft.rcrpg.api.Component;
import de.raidcraft.rcrpg.api.player.RCPlayer;
import de.raidcraft.skills.api.Levelable;
import de.raidcraft.skills.api.Skill;
import de.raidcraft.skills.api.SkillManager;
import de.raidcraft.skills.api.bukkit.BukkitListenerAdapter;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.tables.PermissionSkillsTable;
import de.raidcraft.skills.tables.PlayerSkillsLevelTable;
import de.raidcraft.skills.tables.PlayerSkillsTable;
import de.raidcraft.skills.tables.SkillsTable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Silthus
 */
@ComponentInformation(
        friendlyName = "RaidCraft Skill System",
        desc = "Provides different skills and abilities for players"
)
@Depend(
        components = {Database.class, RaidCraft.class}
)
public class SkillsComponent extends BukkitComponent implements Component, Listener {

    private SkillManager skillManager;
    private LocalConfiguration configuration;
    @InjectComponent
    private Database database;
    @InjectComponent
    private RaidCraft raidCraft;

    @Override
    public void enable() {

        // create the config
        this.configuration = configure(new LocalConfiguration());
        // lets register the database
        database.registerTable(SkillsTable.class, new SkillsTable());
        database.registerTable(PlayerSkillsTable.class, new PlayerSkillsTable());
        database.registerTable(PlayerSkillsLevelTable.class, new PlayerSkillsLevelTable());
        database.registerTable(PermissionSkillsTable.class, new PermissionSkillsTable());
        // register our events
        CommandBook.registerEvents(new BukkitListenerAdapter(this));
        CommandBook.registerEvents(this);
        // the skill manager takes care of all skills currently loaded
        this.skillManager = new SkillManager(this);
        // register ourself as a RPG Component
        RaidCraft.registerComponent(SkillsComponent.class, this);
    }

    public SkillManager getSkillManager() {

        return skillManager;
    }

    public LocalConfiguration getLocalConfiguration() {

        return configuration;
    }

    public static class LocalConfiguration extends ConfigurationBase {

        @Setting("threading.max-core-size")
        public int maxCoreSize = 2;
        @Setting("threading.max-pool-size")
        public int maxPoolSize = 4;
        @Setting("threading.keep-alive")
        public long keepAlive = 1;
    }

    /*///////////////////////////////////////////////////////////////
    //          All Bukkit Events are handled here
    ///////////////////////////////////////////////////////////////*/

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

        try {
            RCPlayer player = RaidCraft.getPlayer(event.getPlayer());
            Skill skill = player.getComponent(SkilledPlayer.class).getSkill(1);
            player.sendMessage("SkillId: " + skill.getId());
            player.sendMessage("Name: " + skill.getName());
            player.sendMessage("Description: " + skill.getDescription());
            player.sendMessage(skill.getUsage());
            player.sendMessage("Cost: " + skill.getCost());
            if (skill instanceof Levelable) {
                Levelable levelable = (Levelable) skill;
                player.sendMessage("Level: " + levelable.getLevel());
                player.sendMessage("Exp: " + levelable.getExp() + "/" + levelable.getMaxExp());
                player.sendMessage("MaxLevel: " + levelable.getMaxLevel());
            }
        } catch (UnknownSkillException e) {
            CommandBook.logger().info(e.getMessage());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {

        RaidCraft.getPlayer(event.getPlayer()).getComponent(SkilledPlayer.class).save();
    }
}
