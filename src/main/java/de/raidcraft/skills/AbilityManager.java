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
import de.raidcraft.skills.util.StringUtils;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.Map;

/**
 * @author Silthus
 */
public final class AbilityManager extends GenericJarFileManager<Ability> implements Component {

    private final SkillsPlugin plugin;
    private final Map<String, AbilityFactory> abilityFactories = new CaseInsensitiveMap<>();
    private final Map<String, Class<? extends Ability>> abilityClasses = new CaseInsensitiveMap<>();
    private int loadedAbilities;
    private int failedAbilities;

    protected AbilityManager(SkillsPlugin plugin) {

        super(Ability.class, new File(plugin.getDataFolder(), plugin.getCommonConfig().ability_jar_path));
        this.plugin = plugin;
        // create the config path
        new File(plugin.getDataFolder(), plugin.getCommonConfig().skill_config_path).mkdirs();
        RaidCraft.registerComponent(AbilityManager.class, this);
    }

    @Override
    public void loadFactories() {

        for (Class<? extends Ability> clazz : loadClasses()) {
            try {
                registerClass(clazz);
            } catch (UnknownSkillException e) {
                plugin.getLogger().warning(e.getMessage());
                failedAbilities++;
            }
        }
        plugin.getLogger().info("Loaded " + loadedAbilities + "/" + (loadedAbilities + failedAbilities) + " skills.");
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
            loadedAbilities++;
        } else {
            plugin.getLogger().warning("Found ability without AbilityInformation: " + skillClass.getCanonicalName());
        }
    }

    public <T extends CharacterTemplate> Ability<T> getAbility(T character, String abilityName, ConfigurationSection merge) throws UnknownSkillException {

        Ability<T> ability;
        abilityName = StringUtils.formatName(abilityName);
        if (merge != null && merge.isSet("ability") && !abilityFactories.containsKey(abilityName)) {
            // create ourselves an alias factory
            createAliasFactory(abilityName, merge.getString("ability"), merge);
        }
        if (!abilityFactories.containsKey(abilityName)) {
            throw new UnknownSkillException("Es gibt keine Fähigkeit mit dem Namen: " + abilityName);
        }
        // lets create a new ability for this character
        ability = abilityFactories.get(abilityName).create(character, merge);
        // lets add the skill as a trigger handler
        if (ability instanceof Triggered) {
            TriggerManager.registerListeners((Triggered) ability);
        }
        return ability;
    }

    protected void createAliasFactory(String alias, String skill, ConfigurationSection config) {

        try {
            Class<? extends Ability> sClass = abilityClasses.get(skill);
            if (sClass == null) throw new UnknownSkillException("No skill class for " + skill + " found!");
            AbilityFactory factory = new AbilityFactory(plugin, sClass, skill, config);
            abilityFactories.put(alias, factory);
        } catch (UnknownSkillException e) {
            plugin.getLogger().warning(e.getMessage());
        }
    }

    public boolean hasAbility(String ability) {

        ability = StringUtils.formatName(ability);
        return abilityFactories.containsKey(ability);
    }

    public AbilityFactory getFactory(Ability ability) {

        return abilityFactories.get(ability.getName());
    }
}
