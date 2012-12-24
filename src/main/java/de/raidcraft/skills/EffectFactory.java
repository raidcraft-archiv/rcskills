package de.raidcraft.skills;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.exceptions.UnknownEffectException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.util.StringUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Silthus
 */
public final class EffectFactory<E extends Effect> extends ConfigurationBase<SkillsPlugin> implements EffectData {

    private final SkillsPlugin plugin;
    private final Class<E> eClass;
    private final EffectInformation information;
    private final String effectName;

    protected EffectFactory(SkillsPlugin plugin, Class<E> eClass, File configDir) {

        super(plugin, new File(configDir, eClass.getAnnotation(EffectInformation.class).name().toLowerCase() + ".yml"));
        this.plugin = plugin;
        this.eClass = eClass;
        this.information = eClass.getAnnotation(EffectInformation.class);
        this.effectName = StringUtil.formatName(information.name());
    }

    @SuppressWarnings("unchecked")
    public E create(Object source, CharacterTemplate target) throws UnknownEffectException {

        // its reflection time yay!
        try {
            for (Constructor<?> constructor : eClass.getDeclaredConstructors()) {
                if (constructor.getParameterTypes().length == 3) {
                    if (constructor.getParameterTypes()[0].isAssignableFrom(source.getClass())
                            && constructor.getParameterTypes()[1].isAssignableFrom(target.getClass())
                            && constructor.getParameterTypes()[2].isAssignableFrom(this.getClass())) {
                        return (E) constructor.newInstance(source, target, this);
                    }
                }
            }
        } catch ( InvocationTargetException | InstantiationException | IllegalAccessException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
        throw new UnknownEffectException("Error when loading effect for class: " + eClass.getCanonicalName());
    }

    public E create(Object source, CharacterTemplate target, Skill skill) throws UnknownEffectException {

        if (skill == null) {
            return create(source, target);
        }
        // lets get all the possible override configs and merge them
        // at this point we need to merge the skill config with the
        // profession config and then merge this config with the result
        SkillFactory skillConfig = plugin.getSkillManager().getFactory(skill);
        ProfessionFactory profConfig = plugin.getProfessionManager().getFactory(skill.getProfession());
        skillConfig.merge(profConfig);
        // and now merge the result with this effet config
        merge(skillConfig);
        return create(source, target);
    }

    public Class<E> getEffectClass() {

        return eClass;
    }

    @Override
    public String getName() {

        return information.name().toLowerCase().replace(" ", "-").trim();
    }

    @Override
    public ConfigurationSection getDataMap() {

        return getOverrideSection("custom");
    }

    @Override
    public EffectInformation getInformation() {

        return this.information;
    }

    @Override
    public double getEffectPriority() {

        return getOverride("priority", plugin.getCommonConfig().default_effect_priority);
    }

    @Override
    public int getEffectDuration() {

        return getOverride("duration.base", 0);
    }

    @Override
    public int getEffectDelay() {

        return getOverride("delay.base", 0);
    }

    @Override
    public int getEffectInterval() {

        return getOverride("interval.base", 0);
    }

    @Override
    public double getEffectDurationLevelModifier() {

        return getOverride("duration.level-modifier", 0.0);
    }

    @Override
    public double getEffectDurationProfLevelModifier() {

        return getOverride("duration.prof-level-modifier", 0.0);
    }

    @Override
    public double getEffectDelayLevelModifier() {

        return getOverride("delay.level-modifier", 0.0);
    }

    @Override
    public double getEffectDelayProfLevelModifier() {

        return getOverride("delay.prof-level-modifier", 0.0);
    }

    @Override
    public double getEffectIntervalLevelModifier() {

        return getOverride("interval.level-modifier", 0.0);
    }

    @Override
    public double getEffectIntervalProfLevelModifier() {

        return getOverride("interval.prof-level-modifier", 0.0);
    }
}
