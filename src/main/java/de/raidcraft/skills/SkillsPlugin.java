package de.raidcraft.skills;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.skills.api.combat.CombatManager;
import de.raidcraft.skills.commands.AdminCommands;
import de.raidcraft.skills.commands.CastCommand;
import de.raidcraft.skills.commands.ProfessionCommands;
import de.raidcraft.skills.commands.SkillsCommand;
import de.raidcraft.skills.skills.magic.Fireball;
import de.raidcraft.skills.skills.misc.PermissionSkill;
import de.raidcraft.skills.skills.physical.Strike;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroProfession;
import de.raidcraft.skills.tables.THeroSkill;
import org.bukkit.command.CommandSender;
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
    private CombatManager combatManager;
    private LocalConfiguration configuration;

    @Override
    public void enable() {

        // create the config
        this.configuration = configure(new LocalConfiguration(this));
        // register our events
        registerEvents(this);
        loadEngine();
        // and commands gogogo
        registerCommands(SkillsCommand.class);
        registerCommands(CastCommand.class);
        registerCommands(BaseCommands.class);
        // register ourself as a RPG Component
        RaidCraft.registerComponent(SkillsPlugin.class, this);
    }

    private void loadEngine() {

        // the skill manager takes care of all skills currently loaded
        this.skillManager = new SkillManager(this);
        // register our inhouse skills
        registerSkills();
        // these managers can only be loaded after the skill manager
        this.professionManager = new ProfessionManager(this);
        this.heroManager = new HeroManager(this);
        this.combatManager = new CombatManager(this);
    }

    @Override
    public void disable() {


    }

    @Override
    public void reload() {

        this.configuration.reload();
        // will override all set variables
        // the garbage collector will take care of the rest
        loadEngine();
    }

    private void registerSkills() {

        SkillManager m = getSkillManager();
        m.registerSkill(Fireball.class);
        m.registerSkill(PermissionSkill.class);
        m.registerSkill(Strike.class);
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

    public CombatManager getCombatManager() {

        return combatManager;
    }

    public LocalConfiguration getCommonConfig() {

        return configuration;
    }

    public static class LocalConfiguration extends ConfigurationBase {

        @Setting("max-player-level")
        public int max_player_level;
        @Setting("callback-purge-ticks")
        public long callback_purge_time = 1200;
        @Setting("defaults.effect-priority")
        public double default_effect_priority = 1.0;
        @Setting("profession.change-cost")
        public int profession_change_cost = 100;
        @Setting("profession.change-level-modifier")
        public double profession_change_level_modifier = 1.0;

        public LocalConfiguration(BasePlugin plugin) {

            super(plugin, "config.yml");
        }
    }

    public class BaseCommands {

        @Command(
                aliases = {"profession", "prof"},
                desc = "Base Command for Profession and Classes"
        )
        @NestedCommand(ProfessionCommands.class)
        public void profession(CommandContext args, CommandSender sender) {

        }

        @Command(
                aliases = {"rcsa"},
                desc = "Base Command for Admins"
        )
        @NestedCommand(AdminCommands.class)
        @CommandPermissions("rcskills.admin")
        public void admin(CommandContext args, CommandSender sender) {

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

        getHeroManager().getHero(event.getPlayer()).save();
    }
}
