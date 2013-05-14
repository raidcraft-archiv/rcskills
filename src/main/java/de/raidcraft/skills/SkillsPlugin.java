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
import de.raidcraft.api.requirement.RequirementManager;
import de.raidcraft.rcconversations.actions.ActionManager;
import de.raidcraft.skills.api.combat.action.HealAction;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.bindings.BindManager;
import de.raidcraft.skills.commands.AdminCommands;
import de.raidcraft.skills.commands.CastCommand;
import de.raidcraft.skills.commands.PartyCommands;
import de.raidcraft.skills.commands.PlayerComands;
import de.raidcraft.skills.commands.ProfessionCommands;
import de.raidcraft.skills.commands.SkillCommands;
import de.raidcraft.skills.commands.SkillsCommand;
import de.raidcraft.skills.config.ExperienceConfig;
import de.raidcraft.skills.config.LevelConfig;
import de.raidcraft.skills.config.PathConfig;
import de.raidcraft.skills.conversations.CanChooseProfessionAction;
import de.raidcraft.skills.conversations.ChooseProfessionAction;
import de.raidcraft.skills.conversations.ListProfessionSkills;
import de.raidcraft.skills.conversations.MaxOutHeroAction;
import de.raidcraft.skills.logging.ExpLogger;
import de.raidcraft.skills.requirement.ItemRequirement;
import de.raidcraft.skills.requirement.ProfessionLevelRequirement;
import de.raidcraft.skills.requirement.SkillLevelRequirement;
import de.raidcraft.skills.requirement.SkillRequirement;
import de.raidcraft.skills.skills.PermissionSkill;
import de.raidcraft.skills.skills.TestSkill;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroAttribute;
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

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public class SkillsPlugin extends BasePlugin implements Component, Listener {

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
    private BindManager bindManager;

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
        levelConfig.loadFormulas();
        loadEngine();
        // and commands gogogo
        registerCommands(SkillsCommand.class);
        registerCommands(CastCommand.class);
        registerCommands(BaseCommands.class);

        this.bindManager = new BindManager(this);
        new BukkitEventDispatcher(this);
        // lets start our logging tasks
        ExpLogger.startTask(this);
        // register conv actions when all plugins loaded
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {

                if (Bukkit.getPluginManager().getPlugin("RCConversations") != null) {
                    // lets register our conversation actions
                    registerConversationActions();
                }
            }
        }, 1L);
    }

    private void setupDatabase() {
        try {
            for (Class<?> clazz : getDatabaseClasses()) {
                getDatabase().find(clazz).findRowCount();
            }
        } catch (PersistenceException ex) {
            System.out.println("Installing database for " + getDescription().getName() + " due to first time usage");
            installDDL();
        }
    }

    private void registerConversationActions() {

        ActionManager.registerAction(new ChooseProfessionAction());
        ActionManager.registerAction(new ListProfessionSkills());
        ActionManager.registerAction(new MaxOutHeroAction());
        ActionManager.registerAction(new CanChooseProfessionAction());
    }

    private void loadEngine() {

        registerRequirements();
        // the skill manager takes care of all skills currently loaded
        this.skillManager = new SkillManager(this);
        this.abilityManager = new AbilityManager(this);
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
        this.armorManager = new ArmorManager(this);
        this.weaponManager = new WeaponManager(this);
        this.experienceManager = new ExperienceManager(this);
        this.bukkitEnvironmentManager = new BukkitEnvironmentManager(this);
        // lets register our permissions provider last
        this.permissionsProvider = new SkillPermissionsProvider(this);
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
            getSkillManager().registerClass(TestSkill.class);
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
        // and reload all of our managers and configs
        this.configuration.reload();
        this.pathConfig.reload();
        this.levelConfig.reload();
        this.experienceConfig.reload();
        // before reloading the managers we need to unregister all listeners
        TriggerManager.unregisterAll();

        this.skillManager.reload();
        this.abilityManager.reload();
        registerSkills();
        this.effectManager.reload();
        this.aliasManager.reload();

        this.professionManager.reload();
        this.characterManager.reload();
        this.combatManager.reload();
        this.damageManager.reload();
        this.armorManager.reload();
        this.weaponManager.reload();

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
        classes.add(ExpLogger.class);
        classes.add(THeroAttribute.class);
        return classes;
    }

    public SkillManager getSkillManager() {

        return skillManager;
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

    public CharacterManager getCharacterManager() {

        return characterManager;
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

    public LocalConfiguration getCommonConfig() {

        return configuration;
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

    public BindManager getBindManager() {

        return bindManager;
    }

    public boolean isSavingWorld(String world) {

        return !getCommonConfig().getIgnoredWorlds().contains(world);
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
        @Setting("defaults.global-cooldown")
        public double global_cooldown = 1.5;
        @Setting("defaults.fist-damage")
        public int fist_attack_damage = 1;
        @Setting("defaults.permission-group")
        public String default_permission_group = "default";
        @Setting("defaults.skeleton-knockback")
        public double skeletons_knockback_chance = 1.0;
        @Setting("profession.change-cost")
        public int profession_change_cost = 100;
        @Setting("profession.change-level-modifier")
        public double profession_change_level_modifier = 1.0;
        @Setting("interface.updateinterval")
        public long interface_update_interval = 20;
        @Setting("hero.max-level")
        public int hero_max_level = 100;
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
        @Setting("paths.abilities-jars")
        public String ability_jar_path = "abilities/";
        @Setting("paths.profession-configs")
        public String profession_config_path = "professions/";
        @Setting("defaults.party-timeout")
        public double invite_timeout = 30.0;
        @Setting("defaults.swing-time")
        public double default_swing_time = 1.0;

        public LocalConfiguration(SkillsPlugin plugin) {

            super(plugin, "config.yml");
        }

        public Set<String> getIgnoredWorlds() {

            return new HashSet<>(getStringList("ignored-worlds"));
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
        @NestedCommand(value = PlayerComands.class)
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
            } catch (CombatException e) {
                throw new CommandException(e);
            }
        }
    }
}
