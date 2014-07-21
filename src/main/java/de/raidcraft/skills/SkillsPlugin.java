package de.raidcraft.skills;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.api.action.action.ActionException;
import de.raidcraft.api.action.action.ActionFactory;
import de.raidcraft.api.action.requirement.RequirementFactory;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.api.requirement.RequirementManager;
import de.raidcraft.rcconversations.actions.ActionManager;
import de.raidcraft.skills.actionapi.trigger.SkillTrigger;
import de.raidcraft.skills.actions.AddHeroExpAction;
import de.raidcraft.skills.api.combat.action.HealAction;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.binds.BindListener;
import de.raidcraft.skills.commands.AdminCommands;
import de.raidcraft.skills.commands.BindAutoCommand;
import de.raidcraft.skills.commands.BindCommand;
import de.raidcraft.skills.commands.CastCommand;
import de.raidcraft.skills.commands.PartyCommands;
import de.raidcraft.skills.commands.PlayerCommands;
import de.raidcraft.skills.commands.ProfessionCommands;
import de.raidcraft.skills.commands.PvPCommands;
import de.raidcraft.skills.commands.SkillCommands;
import de.raidcraft.skills.commands.SkillsCommand;
import de.raidcraft.skills.config.ExperienceConfig;
import de.raidcraft.skills.config.LevelConfig;
import de.raidcraft.skills.config.PathConfig;
import de.raidcraft.skills.conversations.CanChooseProfessionAction;
import de.raidcraft.skills.conversations.ChooseProfessionAction;
import de.raidcraft.skills.conversations.LinkExpPoolAction;
import de.raidcraft.skills.conversations.ListProfessionSkills;
import de.raidcraft.skills.conversations.MaxOutHeroAction;
import de.raidcraft.skills.items.SkillsRequirementProvider;
import de.raidcraft.skills.requirement.ItemRequirement;
import de.raidcraft.skills.requirement.ProfessionLevelRequirement;
import de.raidcraft.skills.requirement.SkillLevelRequirement;
import de.raidcraft.skills.requirement.SkillRequirement;
import de.raidcraft.skills.skills.PermissionSkill;
import de.raidcraft.skills.tabdeco.TabDecoArmorSettings;
import de.raidcraft.skills.tabdeco.TabDecoAttributeSettings;
import de.raidcraft.skills.tabdeco.TabDecoEconomySetting;
import de.raidcraft.skills.tabdeco.TabDecoExpPoolSettings;
import de.raidcraft.skills.tabdeco.TabDecoHeroLevelSettings;
import de.raidcraft.skills.tabdeco.TabDecoMaxHealthSettings;
import de.raidcraft.skills.tabdeco.TabDecoProfessionPathSettings;
import de.raidcraft.skills.tabdeco.TabDecoPvPSettings;
import de.raidcraft.skills.tabdeco.TabDecoResourceSettings;
import de.raidcraft.skills.tables.TBinding;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroAttribute;
import de.raidcraft.skills.tables.THeroExpPool;
import de.raidcraft.skills.tables.THeroOption;
import de.raidcraft.skills.tables.THeroProfession;
import de.raidcraft.skills.tables.THeroResource;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.skills.tables.TProfession;
import de.raidcraft.skills.tables.TProfessionTranslation;
import de.raidcraft.skills.tables.TSkill;
import de.raidcraft.skills.tables.TSkillData;
import de.raidcraft.skills.tables.TSkillTranslation;
import de.raidcraft.tabdeco.api.TabDecoRegistry;
import de.raidcraft.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public class SkillsPlugin extends BasePlugin implements Component {

    private static boolean loadedTabDecoSettings = false;

    private SkillManager skillManager;
    private AbilityManager abilityManager;
    private EffectManager effectManager;
    private AliasManager aliasManager;
    private ProfessionManager professionManager;
    private CharacterManager characterManager;
    private CombatManager combatManager;
    private DamageManager damageManager;
    private ArmorManager armorManager;
    private WeaponManager weaponManager;
    private ExperienceManager experienceManager;
    private BukkitEnvironmentManager bukkitEnvironmentManager;
    private LocalConfiguration configuration;
    private PathConfig pathConfig;
    private LevelConfig levelConfig;
    private ExperienceConfig experienceConfig;
    private SkillPermissionsProvider permissionsProvider;
    private BukkitEventDispatcher bukkitEventDispatcher;

    @Override
    public void enable() {

        setupDatabase();
        // register ourself as a RPG Component
        RaidCraft.registerComponent(SkillsPlugin.class, this);
        // create the config
        this.configuration = configure(new LocalConfiguration(this));
        this.pathConfig = configure(new PathConfig(this), false);
        this.levelConfig = configure(new LevelConfig(this), false);
        this.experienceConfig = configure(new ExperienceConfig(this), false);

        // and commands gogogo
        registerCommands(SkillsCommand.class);
        registerCommands(CastCommand.class);
        registerCommands(BaseCommands.class);
        registerCommands(BindCommand.class);
        registerCommands(BindAutoCommand.class);

        getServer().getPluginManager().registerEvents(new BindListener(this), this);

        // register the tab stuff
        registerTabDecoSettings();

        registerActionAPI();

        // register conv actions when all plugins loaded
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {

                // the skill engine needs to be loaded after all other plugins are loaded
                // to avoid dependency hickups
                loadEngine();
                // lets register our permissions provider last
                permissionsProvider = new SkillPermissionsProvider(SkillsPlugin.this);

                try {
                    if (Bukkit.getPluginManager().getPlugin("RCConversations") != null) {
                        // lets register our conversation actions
                        registerConversationActions();
                    }
                    if (Bukkit.getPluginManager().getPlugin("RCItems") != null) {
                        try {
                            RaidCraft.registerItemAttachmentProvider(getSkillManager());
                            RaidCraft.registerItemAttachmentProvider(new SkillsRequirementProvider());
                        } catch (RaidCraftException e) {
                            getLogger().warning(e.getMessage());
                        }
                    }
                } catch (Throwable e) {
                    getLogger().warning(e.getMessage());
                    e.printStackTrace();
                }
            }
        }, 1L);
    }

    @Override
    public void disable() {

        // clear the cache of all heroes, saving them to the database
        for (Hero hero : new ArrayList<>(getCharacterManager().getCachedHeroes())) {
            hero.save();
        }
    }

    @Override
    public void reload() {

        // clear the cache of all heroes, saving them to the database
        for (Hero hero : new ArrayList<>(getCharacterManager().getCachedHeroes())) {
            hero.save();
        }
        // cancel all tasks
        Bukkit.getScheduler().cancelTasks(this);
        // and reload all of our managers and configs
        this.configuration.reload();
        this.pathConfig.reload();
        this.levelConfig.reload();
        this.experienceConfig.reload();
        // before reloading the managers we need to unregister all listeners
        TriggerManager.unregisterAll();
        // also unregister all of our bukkit events
        HandlerList.unregisterAll(this);
        // and reload the complete engine leaving all the stuff to the garbage collector
        loadEngine();
        // reload the skill permissions provider
        permissionsProvider.reload();
    }

    private void registerActionAPI() {

        /* ACTIONS */
        try {
            ActionFactory actionFactory = ActionFactory.getInstance();
            actionFactory.registerAction(this, "hero.addxp", new AddHeroExpAction());
        } catch (ActionException e) {
            getLogger().warning(e.getMessage());
        }

        /* REQUIREMENTS */
        RequirementFactory requirementFactory = RequirementFactory.getInstance();
        requirementFactory.registerRequirement(this, "hero.level", (Player player) -> {

            Hero hero = getCharacterManager().getHero(player);
            return hero.getPlayerLevel() >= getConfig().getInt("level");
        });

        /* TRIGGER */
        de.raidcraft.api.action.trigger.TriggerManager triggerManager = de.raidcraft.api.action.trigger.TriggerManager.getInstance();
        triggerManager.registerTrigger(this, new SkillTrigger());
    }

    public CharacterManager getCharacterManager() {

        return characterManager;
    }

    private void setupDatabase() {

        try {
            for (Class<?> clazz : getDatabaseClasses()) {
                getDatabase().find(clazz).findRowCount();
            }
        } catch (PersistenceException ex) {
            this.getLogger().info(String.format("Installing database for %s due to first time usage.", getDescription().getName()));
            installDDL();
        }
    }

    private void registerTabDecoSettings() {

        registerEvents(new Listener() {

            @EventHandler
            public void onPluginEnable(PluginEnableEvent event) {

                try {
                    //Check if TabDeco has been loaded
                    Plugin tabDeco = getServer().getPluginManager().getPlugin("TabDeco");
                    if (tabDeco != null) {
                        //Check if the settings already have been loaded
                        //This event loads multiple times for each plugin
                        if (!loadedTabDecoSettings) {

                            TabDecoRegistry.registerNewSetting("attribute\\(([a-zA-Z0-9]+)\\)",
                                    new TabDecoAttributeSettings(SkillsPlugin.this), SkillsPlugin.this);

                            TabDecoRegistry.registerNewSetting("profession([a-zA-Z0-9]+)\\(([a-zA-Z0-9]+)\\)",
                                    new TabDecoProfessionPathSettings(SkillsPlugin.this), SkillsPlugin.this);

                            TabDecoRegistry.registerNewSetting("resource([a-zA-Z0-9]+)\\(([a-zA-Z0-9]+)\\)",
                                    new TabDecoResourceSettings(SkillsPlugin.this), SkillsPlugin.this);

                            TabDecoRegistry.registerNewSetting("armorValue",
                                    new TabDecoArmorSettings(SkillsPlugin.this), SkillsPlugin.this);

                            TabDecoRegistry.registerNewSetting("maxHealth",
                                    new TabDecoMaxHealthSettings(SkillsPlugin.this), SkillsPlugin.this);

                            TabDecoRegistry.registerNewSetting("expPool",
                                    new TabDecoExpPoolSettings(SkillsPlugin.this), SkillsPlugin.this);

                            TabDecoRegistry.registerNewSetting("expPoolLink",
                                    new TabDecoExpPoolSettings(SkillsPlugin.this), SkillsPlugin.this);

                            TabDecoRegistry.registerNewSetting("heroLevel",
                                    new TabDecoHeroLevelSettings(SkillsPlugin.this), SkillsPlugin.this);

                            TabDecoRegistry.registerNewSetting("pvpStatus",
                                    new TabDecoPvPSettings(SkillsPlugin.this), SkillsPlugin.this);

                            TabDecoRegistry.registerNewSetting("rcMoneyGold",
                                    new TabDecoEconomySetting(SkillsPlugin.this), SkillsPlugin.this);
                            TabDecoRegistry.registerNewSetting("rcMoneySilver",
                                    new TabDecoEconomySetting(SkillsPlugin.this), SkillsPlugin.this);
                            TabDecoRegistry.registerNewSetting("rcMoneyCopper",
                                    new TabDecoEconomySetting(SkillsPlugin.this), SkillsPlugin.this);


                            //Let the user of the plugin know that everything worked
                            getLogger().info("Registered settings for TabDeco");

                            //Set mySettingsLoaded to true to prevent multiple registrations of the settings
                            loadedTabDecoSettings = true;
                        }
                    }
                } catch (Exception ex) {
                    //Let the user know that something didn't work as it should and
                    //print out the error
                    getLogger().severe("Couldn't load settings for TabDeco");
                }
            }
        });
    }

    private void loadEngine() {

        registerRequirements();
        // load some config stuff
        levelConfig.loadFormulas();
        // the skill manager takes care of all skills currently loaded
        this.skillManager = new SkillManager(this);
        this.abilityManager = new AbilityManager(this);
        this.effectManager = new EffectManager(this);
        this.skillManager.loadFactories();
        this.abilityManager.loadFactories();
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
        this.armorManager = new ArmorManager(this);
        this.weaponManager = new WeaponManager(this);
        this.experienceManager = new ExperienceManager(this);
        this.bukkitEnvironmentManager = new BukkitEnvironmentManager(this);
        this.bukkitEventDispatcher = new BukkitEventDispatcher(this);
    }

    private void registerConversationActions() {

        ActionManager.registerAction(new ChooseProfessionAction());
        ActionManager.registerAction(new ListProfessionSkills());
        ActionManager.registerAction(new MaxOutHeroAction());
        ActionManager.registerAction(new CanChooseProfessionAction());
        ActionManager.registerAction(new LinkExpPoolAction());
    }

    public SkillManager getSkillManager() {

        return skillManager;
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
        classes.add(THeroAttribute.class);
        classes.add(TBinding.class);
        classes.add(TSkill.class);
        classes.add(TSkillTranslation.class);
        classes.add(TProfession.class);
        classes.add(TProfessionTranslation.class);
        return classes;
    }

    private void registerRequirements() {

        RequirementManager.registerRequirementType(ItemRequirement.class);
        RequirementManager.registerRequirementType(ProfessionLevelRequirement.class);
        RequirementManager.registerRequirementType(SkillLevelRequirement.class);
        RequirementManager.registerRequirementType(SkillRequirement.class);
    }

    private void registerSkills() {

        try {
            getSkillManager().registerClass(PermissionSkill.class);
        } catch (UnknownSkillException e) {
            getLogger().warning(e.getMessage());
        }
    }

    public AbilityManager getAbilityManager() {

        return abilityManager;
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

    public CombatManager getCombatManager() {

        return combatManager;
    }

    public DamageManager getDamageManager() {

        return damageManager;
    }

    public ArmorManager getArmorManager() {

        return armorManager;
    }

    public WeaponManager getWeaponManager() {

        return weaponManager;
    }

    public ExperienceManager getExperienceManager() {

        return experienceManager;
    }

    public BukkitEnvironmentManager getBukkitEnvironmentManager() {

        return bukkitEnvironmentManager;
    }

    public PathConfig getPathConfig() {

        return pathConfig;
    }

    public LevelConfig getLevelConfig() {

        return levelConfig;
    }

    public ExperienceConfig getExperienceConfig() {

        return experienceConfig;
    }

    public boolean isSavingWorld(String world) {

        return !getCommonConfig().getIgnoredWorlds().contains(world);
    }

    public LocalConfiguration getCommonConfig() {

        return configuration;
    }

    public static class LocalConfiguration extends ConfigurationBase<SkillsPlugin> {

        @Setting("log-interval")
        public long log_interval = 60;
        @Setting("disable-error-skills")
        public boolean disable_error_skills = false;
        @Setting("disable-error-abilities")
        public boolean disable_error_abilities = false;
        @Setting("callback-purge-ticks")
        public long callback_purge_time = 1200;
        @Setting("defaults.environment-damage-in-percent")
        public boolean environment_damage_in_percent = false;
        @Setting("defaults.effect-priority")
        public double default_effect_priority = 1.0;
        @Setting("cache-offline-players")
        public boolean cache_offline_players = false;
        @Setting("defaults.party-exp-range")
        public int party_exp_range = 100;
        @Setting("defaults.global-cooldown")
        public double global_cooldown = 1.5;
        @Setting("defaults.permission-group")
        public String default_permission_group = "default";
        @Setting("defaults.skeleton-knockback")
        public double skeletons_knockback_chance = 1.0;
        @Setting("defaults.pvp-combat-timeout")
        public double combat_pvp_timeout = 300;
        @Setting("profession.change-cost")
        public int profession_change_cost = 100;
        @Setting("profession.change-level-modifier")
        public double profession_change_level_modifier = 1.0;
        @Setting("hero.max-level")
        public int hero_max_level = 100;
        @Setting("hero.level-treshhold")
        public int hero_level_treshhold = 10;
        @Setting("health-scale")
        public double health_scale = 100.0;
        @Setting("paths.skill-configs")
        public String skill_config_path = "skill-configs/";
        @Setting("paths.alias-configs")
        public String alias_config_path = "alias-configs/";
        @Setting("paths.skill-jars")
        public String skill_jar_path = "skills-and-effects/";
        @Setting("paths.effect-configs")
        public String effect_config_path = "effect-configs/";
        @Setting("paths.effect-jars")
        public String effect_jar_path = "skills-and-effects/";
        @Setting("paths.abilities-jars")
        public String ability_jar_path = "abilities/";
        @Setting("paths.profession-configs")
        public String profession_config_path = "professions/";
        @Setting("defaults.party-timeout")
        public double invite_timeout = 30.0;
        @Setting("defaults.swing-time")
        public double default_swing_time = 1.0;
        @Setting("hero-cache-timeout")
        public int hero_cache_timeout = 300;
        @Setting("defaults.userinterface-refresh-interval")
        public int userinterface_refresh_interval = 100;
        @Setting("defaults.character-invalidation-interval")
        public int character_invalidation_interval = 100;
        @Setting("defaults.pvp-toggle-delay")
        public double pvp_toggle_delay = 300;
        @Setting("defaults.exp-bat-despawn-delay")
        public double exp_bat_despawn_delay = 10.0;

        public LocalConfiguration(SkillsPlugin plugin) {

            super(plugin, "config.yml");
        }

        public Set<String> getIgnoredWorlds() {

            return new HashSet<>(getStringList("ignored-worlds"));
        }

        public Set<String> getExcludedProfessions() {

            return new HashSet<>(getStringList("excluded-max-out-professions"));
        }

        public Set<String> getExcludedSkills() {

            return new HashSet<>(getStringList("excluded-max-out-skills"));
        }
    }

    public class BaseCommands {

        @Command(
                aliases = {"profession", "prof", "path"},
                desc = "Base Command for Profession and Classes"
        )
        @NestedCommand(value = ProfessionCommands.class)
        public void profession(CommandContext args, CommandSender sender) throws CommandException {

        }

        @Command(
                aliases = {"rcs"},
                desc = "Base Command for Players"
        )
        @NestedCommand(value = PlayerCommands.class)
        public void player(CommandContext args, CommandSender sender) {

        }

        @Command(
                aliases = {"party", "p"},
                desc = "Base Command for Players"
        )
        @NestedCommand(value = PartyCommands.class)
        public void party(CommandContext args, CommandSender sender) {

        }

        @Command(
                aliases = {"rcsa"},
                desc = "Base Command for Admins"
        )
        @NestedCommand(AdminCommands.class)
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
                aliases = "pvp",
                desc = "Toggles PvP for the player"
        )
        @CommandPermissions("rcskills.player.pvp")
        @NestedCommand(value = PvPCommands.class, executeBody = true)
        public void pvp(CommandContext args, CommandSender sender) throws CommandException {

            Hero hero = getCharacterManager().getHero((Player) sender);
            long combatCooldown = TimeUtil.secondsToMillis(getCommonConfig().combat_pvp_timeout) + hero.getLastCombatAction();
            if (hero.isPvPEnabled() && System.currentTimeMillis() < combatCooldown) {
                double seconds = TimeUtil.millisToSeconds(combatCooldown - System.currentTimeMillis());
                String time;
                if (seconds > 60.0) {
                    time = TimeUtil.secondsToMinutes(seconds) + "min";
                } else {
                    time = seconds + "s";
                }
                throw new CommandException(getTranslationProvider().tr(
                        sender, "pvp.cooldown", "You need to wait " + time + " until you can toggle your PvP status.", time));
            }
            hero.setPvPEnabled(!hero.isPvPEnabled());
            sender.sendMessage((hero.isPvPEnabled() ? ChatColor.RED : ChatColor.AQUA) +
                    getTranslationProvider().tr(sender, "pvp.toggled", "PvP has been %s",
                            (hero.isPvPEnabled() ? getTranslationProvider().var(sender, "pvp.enabled", "enabled.")
                            : getTranslationProvider().var(sender, "pvp.disabled", "disabled."))));
        }

        @Command(
                aliases = "heal",
                desc = "Heals the hero to full life.",
                flags = "a"
        )
        @CommandPermissions("rcskills.admin.heal")
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
                for (Resource resource : hero.getResources()) {
                    resource.setCurrent(resource.getMax());
                }
            }
            try {
                new HealAction<>("Server", hero, hero.getMaxHealth()).run();
                hero.getPlayer().setFoodLevel(20);
                hero.getPlayer().setSaturation(1.0F);
            } catch (CombatException e) {
                throw new CommandException(e);
            }
        }
    }
}