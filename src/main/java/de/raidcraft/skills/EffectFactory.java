package de.raidcraft.skills;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.exceptions.UnknownEffectException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.config.EffectConfig;
import de.raidcraft.skills.config.SkillConfig;
import de.raidcraft.skills.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class EffectFactory<E extends Effect> {

    private final SkillsPlugin plugin;
    private final Class<E> eClass;
    private final EffectInformation information;
    private final String effectName;
    private final Map<Skill, EffectConfig> effectConfigs = new HashMap<>();
    private final EffectConfig defaultConfig;
    private Constructor<?> constructor = null;

    protected EffectFactory(SkillsPlugin plugin, Class<E> eClass) throws UnknownEffectException {

        this.plugin = plugin;
        this.eClass = eClass;
        this.information = eClass.getAnnotation(EffectInformation.class);
        this.effectName = StringUtils.formatName(information.name());
        this.defaultConfig = plugin.configure(new EffectConfig(this), false);

        // lets load the constructor for faster effect creations
        for (Constructor<?> constructor : eClass.getDeclaredConstructors()) {
            if (constructor.getParameterTypes().length == 3) {
                if (constructor.getParameterTypes()[1].isAssignableFrom(CharacterTemplate.class)
                        && constructor.getParameterTypes()[2].isAssignableFrom(EffectData.class)) {
                    this.constructor = constructor;
                    break;
                }
            }
        }
        if (constructor == null) {
            throw new UnknownEffectException("Found no matching constructor for the effect: " + effectName);
        }
    }

    protected void createDefaults() {

        EffectData data = defaultConfig;
        data.getFriendlyName();
        data.getEffectPriority();
    }

    @SuppressWarnings("unchecked")
    private <S> E create(S source, CharacterTemplate target, EffectConfig config) throws UnknownEffectException {

        // its reflection time yay!
        try {
            E effect = (E) constructor.newInstance(source, target, config);
            if (!effect.isEnabled()) {
                throw new UnknownEffectException("The effect " + effectName + " is disabled!");
            }
            effect.load(config.getDataMap());
            return effect;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
        throw new UnknownEffectException("Error when loading effect for class: " + eClass.getCanonicalName());
    }

    public <S> E create(S source, CharacterTemplate target) throws UnknownEffectException {

        return create(source, target, defaultConfig);
    }

    public <S> E create(S source, CharacterTemplate target, Skill skill) throws UnknownEffectException {

        if (skill == null) {
            return create(source, target, defaultConfig);
        }

        EffectConfig config;
        if (!effectConfigs.containsKey(skill)) {
            // we need to create a new effect config instance for each skill to merge overrides
            config = plugin.configure(new EffectConfig(this), false);

            // lets now merge the effect with the merged skill profession config
            SkillConfig skillConfig = plugin.getSkillManager().getFactory(skill).getConfig(skill.getProfession());
            config.merge(skillConfig, "effects." + effectName);

            effectConfigs.put(skill, config);
        } else {
            config = effectConfigs.get(skill);
        }

        return create(source, target, config);
    }

    public Class<E> getEffectClass() {

        return eClass;
    }

    public SkillsPlugin getPlugin() {

        return plugin;
    }

    public EffectInformation getInformation() {

        return information;
    }

    public String getEffectName() {

        return effectName;
    }
}
