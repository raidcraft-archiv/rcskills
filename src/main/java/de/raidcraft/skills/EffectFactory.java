package de.raidcraft.skills;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.DataMap;
import de.raidcraft.api.config.YamlDataMap;
import de.raidcraft.skills.api.combat.effect.Effect;
import de.raidcraft.skills.api.combat.effect.EffectInformation;
import de.raidcraft.skills.api.exceptions.UnknownEffectException;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Silthus
 */
public final class EffectFactory extends ConfigurationBase implements EffectData {

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
            for (Constructor<?> constructor : eClass.getDeclaredConstructors()) {
                if (constructor.getParameterTypes().length == 3
                        && constructor.getParameterTypes()[0].isAssignableFrom(source.getClass())
                        && constructor.getParameterTypes()[1].isAssignableFrom(target.getClass())
                        && constructor.getParameterTypes()[2].isAssignableFrom(this.getClass())) {
                    constructor.setAccessible(true);
                    return (Effect<S, T>) constructor.newInstance(source, target, this);
                }
            }
        } catch ( InvocationTargetException | InstantiationException | IllegalAccessException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
        throw new UnknownEffectException("Error when loading effect for class: " + eClass.getCanonicalName());
    }

    public <S, T> Effect<S, T> create(S source, T target, ConfigurationSection override) throws UnknownEffectException {

        String effectName = information.name().toLowerCase().replace(" ", "-").trim();
        Pattern pattern = Pattern.compile("^.*?" + effectName + "\\.(.*?)$");
        // we still need to add our base values for this to work
        YamlDataMap rootMap = new YamlDataMap(this, this);
        for (Map.Entry<String, Object> entry : override.getValues(true).entrySet()) {
            Matcher matcher = pattern.matcher(entry.getKey());
            if (matcher.matches()) {
                rootMap.set(matcher.group(1), entry.getValue());
            }
        }
        setOverrideConfig(rootMap);
        return create(source, target);
    }

    @Override
    public DataMap getDataMap() {

        return new YamlDataMap(getOverrideSection("custom"), this);
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
