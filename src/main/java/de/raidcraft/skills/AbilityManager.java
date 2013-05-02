package de.raidcraft.skills;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.skills.api.ability.Ability;
import de.raidcraft.skills.api.ability.AbilityInformation;
import de.raidcraft.skills.api.ability.IgnoredAbility;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.loader.GenericJarFileManager;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.config.AliasesConfig;
import de.raidcraft.skills.util.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class AbilityManager extends GenericJarFileManager<Ability> implements Component {

    private final SkillsPlugin plugin;
    private final Map<String, AbilityFactory> abilityFactories = new HashMap<>();
    private final Map<String, Class<? extends Ability>> abilityClasses = new HashMap<>();

    protected AbilityManager(SkillsPlugin plugin) {

        super(Ability.class, new File(plugin.getDataFolder(), plugin.getCommonConfig().ability_jar_path));
        this.plugin = plugin;
        // create the config path
        new File(plugin.getDataFolder(), plugin.getCommonConfig().skill_config_path).mkdirs();
        RaidCraft.registerComponent(AbilityManager.class, this);
    }

    public void reload() {

        abilityFactories.clear();
        loadFactories();
    }

    @Override
    public void loadFactories() {

        for (Class<? extends Ability> clazz : loadClasses()) {
            try {
                registerClass(clazz);
            } catch (UnknownSkillException e) {
                plugin.getLogger().warning(e.getMessage());
            }
        }
    }

    /**
     * Registers a skill directly with the skill manager making it possible to use that skill.
     * Skills in external files are loaded via our own class loader on loadSkillClasses() call.
     *
     * @param skillClass of the skill
     */
    public void registerClass(Class<? extends Ability> skillClass) throws UnknownSkillException {

        if (skillClass.isAnnotationPresent(IgnoredAbility.class)) {
            return;
        }

        if (skillClass.isAnnotationPresent(AbilityInformation.class)) {
            String skillName = StringUtils.formatName(skillClass.getAnnotation(AbilityInformation.class).name());
            // check for duplicate skills
            if (abilityFactories.containsKey(skillName)) {
                plugin.getLogger().warning("Found duplicate Ability: " + skillName);
            }
            // load the skill factory
            AbilityFactory factory = new AbilityFactory(plugin, skillClass, skillName);
            abilityFactories.put(skillName, factory);
            abilityClasses.put(skillName, skillClass);
            // lets create the skill once to make a default config
            plugin.getLogger().info("Loaded Ability: " + factory.getName());
        } else {
            plugin.getLogger().warning("Found ability without AbilityInformation: " + skillClass.getCanonicalName());
        }
    }

    protected void createAliasFactory(String alias, String skill, AliasesConfig config) {

        try {
            AbilityFactory factory = new AbilityFactory(plugin, abilityClasses.get(skill), skill, config);
            abilityFactories.put(alias, factory);
            plugin.getLogger().info("Loaded Alias Ability: " + alias + " -> " + skill);
        } catch (UnknownSkillException e) {
            plugin.getLogger().warning(e.getMessage());
        }
    }

    public <T extends CharacterTemplate> Ability<T> getAbility(T character, String abilityName) throws UnknownSkillException {

        Ability<T> ability;
        abilityName = StringUtils.formatName(abilityName);
        if (!abilityFactories.containsKey(abilityName)) {
            throw new UnknownSkillException("Es gibt keine Fähigkeit mit dem Namen: " + abilityName);
        }
        // lets create a new ability for this character
        ability = abilityFactories.get(abilityName).create(character);
        // lets add the skill as a trigger handler
        if (ability instanceof Triggered) {
            TriggerManager.registerListeners((Triggered) ability);
        }
        return ability;
    }

    public boolean hasAbility(String ability) {

        ability = StringUtils.formatName(ability);
        return abilityFactories.containsKey(ability);
    }
}
