package de.raidcraft.skills;

import de.raidcraft.skills.api.ability.Ability;
import de.raidcraft.skills.api.ability.AbilityInformation;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.config.AbilityConfig;
import de.raidcraft.skills.config.AliasesConfig;
import de.raidcraft.skills.util.AbstractFactory;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Constructor;

/**
 * @author Silthus
 */
public final class AbilityFactory extends AbstractFactory<AbilityInformation> {

    private final SkillsPlugin plugin;
    private final Class<? extends Ability> sClass;
    private final AliasesConfig aliasConfig;
    private Constructor<? extends Ability> constructor;

    protected AbilityFactory(SkillsPlugin plugin, Class<? extends Ability> sClass, String skillName) throws UnknownSkillException {

        this(plugin, sClass, skillName, null);
    }

    @SuppressWarnings("unchecked")
    protected AbilityFactory(SkillsPlugin plugin, Class<? extends Ability> sClass, String skillName, AliasesConfig aliasConfig) throws UnknownSkillException {

        super(plugin, skillName);
        this.plugin = plugin;
        this.sClass = sClass;
        this.aliasConfig = aliasConfig;
        // lets cache the constructor for faster skill generation
        for (Constructor<?> constructor : sClass.getConstructors()) {
            if (constructor.getParameterTypes().length != 2) {
                continue;
            }
            if (CharacterTemplate.class.isAssignableFrom(constructor.getParameterTypes()[0])
                    && SkillProperties.class.isAssignableFrom(constructor.getParameterTypes()[1])) {
                constructor.setAccessible(true);
                this.constructor = (Constructor<? extends Ability>) constructor;
            } else {
                throw new UnknownSkillException("Found no matching constructor for the skill: " + skillName);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected <T extends CharacterTemplate> Ability<T> create(T character, ConfigurationSection... overrides) throws UnknownSkillException {

        AbilityConfig config = plugin.configure(new AbilityConfig(this), false);
        // we need to set all the overrides to null because they are used multiple times
        if (useAlias()) {
            config.merge(aliasConfig);
        }

        // also lets merge all aditional override configs
        for (ConfigurationSection section : overrides) {
            config.getOverrideConfig().merge(section);
        }

        if (!config.isEnabled()) {
            throw new UnknownSkillException("The ability " + getName() + " is not enabled!");
        }

        // its reflection time yay!
        try {
            Ability<T> ability = (Ability<T>) constructor.newInstance(character, config);
            // this is called after the skill is created in order
            // to give local variables of the skill a chance to init
            ability.load(config.getData());
            return ability;
        } catch (Throwable e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
            // lets disable the skill so the console wont be spammed
            if (plugin.getCommonConfig().disable_error_abilities) config.setEnabled(false);
        }
        throw new UnknownSkillException("Error when loading ability for class: " + sClass.getCanonicalName());
    }

    public AbilityInformation getInformation() {

        return sClass.getAnnotation(AbilityInformation.class);
    }

    public String getAlias() {

        return aliasConfig.getName();
    }

    public boolean useAlias() {

        return aliasConfig != null;
    }

    protected AbilityConfig getNewConfig() {

        return plugin.configure(new AbilityConfig(this), false);
    }

    protected Class<? extends Ability> getAbilityClass() {

        return sClass;
    }
}
