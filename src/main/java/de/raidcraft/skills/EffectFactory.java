package de.raidcraft.skills;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.api.combat.effect.Effect;
import de.raidcraft.skills.api.combat.effect.EffectInformation;
import de.raidcraft.skills.api.exceptions.UnknownEffectException;
import de.raidcraft.skills.api.persistance.PeriodicEffectData;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Silthus
 */
public final class EffectFactory extends ConfigurationBase implements PeriodicEffectData {

    private final SkillsPlugin plugin;
    private final Class<? extends Effect> eClass;
    private final EffectInformation information;

    protected EffectFactory(SkillsPlugin plugin, Class<? extends Effect> eClass, File configDir) {

        super(plugin, new File(configDir, eClass.getAnnotation(EffectInformation.class).name().toLowerCase() + ".yml"));
        this.plugin = plugin;
        this.eClass = eClass;
        this.information = eClass.getAnnotation(EffectInformation.class);
    }

    @SuppressWarnings("unchecked")
    public <S, T> Effect<S, T> create(S source, T target) throws UnknownEffectException {

        // its reflection time yay!
        try {
            Constructor<? extends Effect> constructor = eClass.getDeclaredConstructor(
                    source.getClass(),
                    target.getClass(),
                    PeriodicEffectData.class);
            return (Effect<S, T>) constructor.newInstance(source, target, this);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
        throw new UnknownEffectException("Error when loading effect for class: " + eClass.getCanonicalName());
    }

    public <S, T> Effect<S, T> create(S source, T target, ConfigurationSection override) throws UnknownEffectException {

        setOverrideConfig(override);
        return create(source, target);
    }

    @Override
    public EffectInformation getInformation() {

        return this.information;
    }

    @Override
    public double getEffectPriority() {

        return getOverride("effect.priority", plugin.getCommonConfig().default_effect_priority);
    }

    @Override
    public int getEffectDuration() {

        return getOverride("effect.duration", 0);
    }

    @Override
    public int getEffectDelay() {

        return getOverride("effect.delay", 0);
    }

    @Override
    public int getEffectInterval() {

        return getOverride("effect.interval", 0);
    }

    @Override
    public double getEffectDurationLevelModifier() {

        return getOverride("effect.duration-level-modifier", 0.0);
    }

    @Override
    public double getEffectDurationProfLevelModifier() {

        return getOverride("effect.duration-prof-level-modifier", 0.0);
    }

    @Override
    public double getEffectDelayLevelModifier() {

        return getOverride("effect.delay-level-modifier", 0.0);
    }

    @Override
    public double getEffectDelayProfLevelModifier() {

        return getOverride("effect.delay-prof-level-modifier", 0.0);
    }

    @Override
    public double getEffectIntervalLevelModifier() {

        return getOverride("effect.interval-level-modifier", 0.0);
    }

    @Override
    public double getEffectIntervalProfLevelModifier() {

        return getOverride("effect.interval-prof-level-modifier", 0.0);
    }
}
