package de.raidcraft.skills;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.skill.Ability;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.config.AliasesConfig;
import de.raidcraft.skills.config.SkillConfig;
import de.raidcraft.skills.util.AbstractFactory;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Constructor;

/**
 * @author Silthus
 */
public final class AbilityFactory extends AbstractFactory {

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

    protected Ability create(CharacterTemplate character, ConfigurationSection... overrides) throws UnknownSkillException {

        SkillConfig config = plugin.configure(new SkillConfig(this), false);
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
            Ability skill = constructor.newInstance(character, config);
            // this is called after the skill is created in order
            // to give local variables of the skill a chance to init
            skill.load(config.getData());
            return skill;
        } catch (Throwable e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
            // lets disable the skill so the console wont be spammed
            if (plugin.getCommonConfig().disable_error_abilities) config.setEnabled(false);
        }
        throw new UnknownSkillException("Error when loading ability for class: " + sClass.getCanonicalName());
    }

    public SkillInformation getInformation() {

        return sClass.getAnnotation(SkillInformation.class);
    }

    public String getAlias() {

        return aliasConfig.getName();
    }

    public boolean useAlias() {

        return aliasConfig != null;
    }

    protected SkillConfig getNewConfig() {

        return plugin.configure(new SkillConfig(this), false);
    }

    protected Class<? extends Ability> getAbilityClass() {

        return sClass;
    }
}
