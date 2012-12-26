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
import de.raidcraft.skills.commands.*;
import de.raidcraft.skills.config.AliasesConfig;
import de.raidcraft.skills.skills.magic.Fireball;
import de.raidcraft.skills.skills.misc.PermissionSkill;
import de.raidcraft.skills.skills.physical.Strike;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroProfession;
import de.raidcraft.skills.tables.THeroSkill;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class SkillsPlugin extends BasePlugin implements Component, Listener {

    private SkillManager skillManager;
    private EffectManager effectManager;
    private ProfessionManager professionManager;
    private CharacterManager characterManager;
    private CombatManager combatManager;
    private DamageManager damageManager;
    private StaminaManager staminaManager;
    private AliasesConfig aliasesConfig;
    private LocalConfiguration configuration;

    @Override
    public void enable() {

        // create the config
        this.configuration = configure(new LocalConfiguration(this));
        loadEngine();
        // and commands gogogo
        registerCommands(SkillsCommand.class);
        registerCommands(CastCommand.class);
        registerCommands(BaseCommands.class);
        // register ourself as a RPG Component
        RaidCraft.registerComponent(SkillsPlugin.class, this);
    }

    private void loadEngine() {

        this.aliasesConfig = configure(new AliasesConfig(this));
        // the skill manager takes care of all skills currently loaded
        this.skillManager = new SkillManager(this);
        this.effectManager = new EffectManager(this);
        // register our inhouse skills
        registerSkills();
        // these managers can only be loaded after the skill manager
        this.professionManager = new ProfessionManager(this);
        this.characterManager = new CharacterManager(this);
        this.combatManager = new CombatManager(this);
        this.damageManager = new DamageManager(this);
        this.staminaManager = new StaminaManager(this);
    }

    private void registerSkills() {

        getSkillManager().registerClass(Fireball.class);
        getSkillManager().registerClass(PermissionSkill.class);
        getSkillManager().registerClass(Strike.class);
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
        this.aliasesConfig.reload();
        this.skillManager.reload();
        registerSkills();
        this.effectManager.reload();
        this.professionManager.reload();
        this.characterManager.reload();
        this.combatManager.reload();
        this.damageManager.reload();
        this.staminaManager.reload();
        this.characterManager.startTasks();
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

    public EffectManager getEffectManager() {

        return effectManager;
    }

    public ProfessionManager getProfessionManager() {

        return professionManager;
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

    public StaminaManager getStaminaManager() {

        return staminaManager;
    }

    public AliasesConfig getAliasesConfig() {

        return aliasesConfig;
    }

    public LocalConfiguration getCommonConfig() {

        return configuration;
    }

    public static class LocalConfiguration extends ConfigurationBase<SkillsPlugin> {

        @Setting("callback-purge-ticks")
        public long callback_purge_time = 1200;
        @Setting("defaults.effect-priority")
        public double default_effect_priority = 1.0;
        @Setting("profession.change-cost")
        public int profession_change_cost = 100;
        @Setting("profession.change-level-modifier")
        public double profession_change_level_modifier = 1.0;
        @Setting("interface.updateinterval")
        public long interface_update_interval = 20;
        @Setting("interface.mana-bar-interval")
        public long interface_mana_bar_update = 5 * 1000;
        @Setting("hero.max-level")
        public int hero_max_level = 100;
        @Setting("hero.mana-regain-interval")
        public long hero_mana_regain_interval = 50;
        @Setting("hero.mana-regain-amount")
        public int hero_mana_regain_amount = 5;
        @Setting("hero.stamina-regain-interval")
        public long hero_stamina_regain_interval = 25;
        @Setting("hero.stamina-regain-amount")
        public int hero_stamina_regain_amount = 1;

        public LocalConfiguration(SkillsPlugin plugin) {

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

        @Command(
                aliases = "skill",
                desc = "Base Command for the Skill"
        )
        @NestedCommand(SkillCommands.class)
        public void skill(CommandContext args, CommandSender sender) {

        }
    }
}
