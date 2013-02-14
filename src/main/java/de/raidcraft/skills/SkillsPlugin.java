package de.raidcraft.skills;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.commands.AdminCommands;
import de.raidcraft.skills.commands.CastCommand;
import de.raidcraft.skills.commands.PlayerComands;
import de.raidcraft.skills.commands.ProfessionCommands;
import de.raidcraft.skills.commands.SkillCommands;
import de.raidcraft.skills.commands.SkillsCommand;
import de.raidcraft.skills.config.LevelConfig;
import de.raidcraft.skills.config.PathConfig;
import de.raidcraft.skills.skills.PermissionSkill;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroExpPool;
import de.raidcraft.skills.tables.THeroOption;
import de.raidcraft.skills.tables.THeroProfession;
import de.raidcraft.skills.tables.THeroResource;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.skills.tables.TSkillData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class SkillsPlugin extends BasePlugin implements Component, Listener {

    private SkillManager skillManager;
    private EffectManager effectManager;
    private AliasManager aliasManager;
    private ProfessionManager professionManager;
    private CharacterManager characterManager;
    private CombatManager combatManager;
    private DamageManager damageManager;
    private BukkitEnvironmentManager bukkitEnvironmentManager;
    private LocalConfiguration configuration;
    private PathConfig pathConfig;
    private LevelConfig levelConfig;
    private SkillPermissionsProvider permissionsProvider;

    @Override
    public void enable() {

        // create the config
        this.configuration = configure(new LocalConfiguration(this));
        this.pathConfig = configure(new PathConfig(this), false);
        this.levelConfig = configure(new LevelConfig(this), false);
        levelConfig.loadFormulas();
        loadEngine();
        // and commands gogogo
        registerCommands(SkillsCommand.class);
        registerCommands(CastCommand.class);
        registerCommands(BaseCommands.class);
        new BukkitEventDispatcher(this);
        // register ourself as a RPG Component
        RaidCraft.registerComponent(SkillsPlugin.class, this);
    }

    private void loadEngine() {

        // the skill manager takes care of all skills currently loaded
        this.skillManager = new SkillManager(this);
        this.effectManager = new EffectManager(this);
        this.skillManager.loadFactories();
        this.effectManager.loadFactories();
        // register our inhouse skills
        registerSkills();
        // init the alias manager directly after the skills
        this.aliasManager = new AliasManager(this);
        // these managers can only be loaded after the skill manager
        this.professionManager = new ProfessionManager(this);
        this.characterManager = new CharacterManager(this);
        this.combatManager = new CombatManager(this);
        this.damageManager = new DamageManager(this);
        this.bukkitEnvironmentManager = new BukkitEnvironmentManager(this);
        // lets register our permissions provider last
        this.permissionsProvider = new SkillPermissionsProvider(this);
    }

    private void registerSkills() {

        try {
            getSkillManager().registerClass(PermissionSkill.class);
        } catch (UnknownSkillException e) {
            getLogger().warning(e.getMessage());
        }
    }

    @Override
    public void disable() {


    }

    @Override
    public void reload() {

        // cancel all tasks
        Bukkit.getScheduler().cancelTasks(this);
        // and reload all of our managers
        this.configuration.reload();
        // before reloading the managers we need to unregister all listeners
        TriggerManager.unregisterAll();

        this.skillManager.reload();
        registerSkills();
        this.effectManager.reload();
        this.aliasManager.reload();

        this.professionManager.reload();
        this.characterManager.reload();
        this.combatManager.reload();
        this.damageManager.reload();
        this.characterManager.startTasks();

        this.permissionsProvider.reload();
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> classes = new ArrayList<>();
        classes.add(THero.class);
        classes.add(THeroOption.class);
        classes.add(THeroExpPool.class);
        classes.add(THeroProfession.class);
        classes.add(THeroSkill.class);
        classes.add(TSkillData.class);
        classes.add(THeroResource.class);
        return classes;
    }

    public SkillManager getSkillManager() {

        return skillManager;
    }

    public EffectManager getEffectManager() {

        return effectManager;
    }

    public ProfessionManager getProfessionManager() {

        return professionManager;
    }

    public AliasManager getAliasManager() {

        return aliasManager;
    }

    public CharacterManager getCharacterManager() {

        return characterManager;
    }

    public CombatManager getCombatManager() {

        return combatManager;
    }

    public DamageManager getDamageManager() {

        return damageManager;
    }

    public BukkitEnvironmentManager getBukkitEnvironmentManager() {

        return bukkitEnvironmentManager;
    }

    public LocalConfiguration getCommonConfig() {

        return configuration;
    }

    public PathConfig getPathConfig() {

        return pathConfig;
    }

    public LevelConfig getLevelConfig() {

        return levelConfig;
    }

    public static class LocalConfiguration extends ConfigurationBase<SkillsPlugin> {

        @Setting("disable-error-skills")
        public boolean disable_error_skills = false;
        @Setting("callback-purge-ticks")
        public long callback_purge_time = 1200;
        @Setting("defaults.effect-priority")
        public double default_effect_priority = 1.0;
        @Setting("defaults.global-cooldown")
        public double global_cooldown = 1.5;
        @Setting("defaults.swing-delay")
        public double swing_delay = 1.0;
        @Setting("defaults.permission-group")
        public String default_permission_group = "default";
        @Setting("profession.change-cost")
        public int profession_change_cost = 100;
        @Setting("profession.change-level-modifier")
        public double profession_change_level_modifier = 1.0;
        @Setting("interface.updateinterval")
        public long interface_update_interval = 20;
        @Setting("interface.resource-bar-interval")
        public long interface_resource_bar_update = 5 * 1000;
        @Setting("hero.max-level")
        public int hero_max_level = 100;
        @Setting("hero.stamina-regain-interval")
        public long hero_stamina_regain_interval = 25;
        @Setting("hero.stamina-regain-amount")
        public int hero_stamina_regain_default_amount = 1;
        @Setting("paths.skill-configs")
        public String skill_config_path = "skill-configs/";
        @Setting("paths.alias-configs")
        public String alias_config_path = "alias-configs/";
        @Setting("paths.skill-jars")
        public String skill_jar_path = "skills/";
        @Setting("paths.effect-configs")
        public String effect_config_path = "effect-configs/";
        @Setting("paths.effect-jars")
        public String effect_jar_path = "effects/";
        @Setting("paths.profession-configs")
        public String profession_config_path = "professions/";

        public LocalConfiguration(SkillsPlugin plugin) {

            super(plugin, "config.yml");
        }
    }

    public class BaseCommands {

        @Command(
                aliases = {"profession", "prof"},
                desc = "Base Command for Profession and Classes"
        )
        @NestedCommand(value = ProfessionCommands.class)
        public void profession(CommandContext args, CommandSender sender) throws CommandException {

                getCommand("profession info").execute(sender, "profession info", args.getSlice(0));
        }

        @Command(
                aliases = {"rcs"},
                desc = "Base Command for Players"
        )
        @NestedCommand(value = PlayerComands.class)
        public void player(CommandContext args, CommandSender sender) {

            getCommand("rcs info").execute(sender, "rcs info", args.getSlice(0));
        }

        @Command(
                aliases = {"rcsa"},
                desc = "Base Command for Admins"
        )
        @NestedCommand(AdminCommands.class)
        @CommandPermissions("rcskills.admin")
        public void admin(CommandContext args, CommandSender sender) {

        }

        @Command(
                aliases = "skill",
                desc = "Base Command for the Skill"
        )
        @NestedCommand(SkillCommands.class)
        public void skill(CommandContext args, CommandSender sender) {

        }

        @Command(
                aliases = "heal",
                desc = "Heals the hero to full life.",
                flags = "a"
        )
        public void heal(CommandContext args, CommandSender sender) throws CommandException {

            Hero hero;
            if (args.argsLength() > 0) {
                try {
                    hero = getCharacterManager().getHero(args.getString(0));
                } catch (UnknownPlayerException e) {
                    throw new CommandException(e.getMessage());
                }
            } else {
                hero = getCharacterManager().getHero((Player) sender);
            }
            if (args.hasFlag('a')) {
                hero.reset();
            } else {
                hero.heal(hero.getMaxHealth());
            }
        }
    }
}
