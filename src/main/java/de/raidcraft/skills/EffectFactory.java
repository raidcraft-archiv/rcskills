package de.raidcraft.skills;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.exceptions.UnknownEffectException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.config.EffectConfig;
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
    private final Map<String, EffectConfig> effectConfigs = new HashMap<>();
    private final EffectConfig defaultConfig;

    protected EffectFactory(SkillsPlugin plugin, Class<E> eClass) {

        this.plugin = plugin;
        this.eClass = eClass;
        this.information = eClass.getAnnotation(EffectInformation.class);
        this.effectName = StringUtils.formatName(information.name());
        this.defaultConfig = plugin.configure(new EffectConfig(this));
    }

    protected void createDefaults() {

        EffectData data = defaultConfig;
        data.getEffectPriority();
        data.getEffectDelay();
        data.getEffectDelayLevelModifier();
        data.getEffectDelayProfLevelModifier();
        data.getEffectDuration();
        data.getEffectDurationLevelModifier();
        data.getEffectDurationProfLevelModifier();
        data.getEffectInterval();
        data.getEffectIntervalLevelModifier();
        data.getEffectIntervalProfLevelModifier();
    }

    @SuppressWarnings("unchecked")
    private <S> E create(S source, CharacterTemplate target, EffectConfig config) throws UnknownEffectException {

        // its reflection time yay!
        try {
            for (Constructor<?> constructor : eClass.getDeclaredConstructors()) {
                if (constructor.getParameterTypes().length == 3) {
                    if (constructor.getParameterTypes()[0].isAssignableFrom(source.getClass())
                            && constructor.getParameterTypes()[1].isAssignableFrom(target.getClass())
                            && constructor.getParameterTypes()[2].isAssignableFrom(EffectConfig.class)) {
                        E effect = (E) constructor.newInstance(source, target, config);
                        if (effect instanceof Triggered) {
                            TriggerManager.registerListeners((Triggered) effect);
                        }
                        return effect;
                    }
                }
            }
        } catch ( InvocationTargetException | InstantiationException | IllegalAccessException e) {
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
        if (!effectConfigs.containsKey(skill.getProfession().getName())) {
            config = plugin.configure(new EffectConfig(this));
            // lets get all the possible override configs and merge them
            // at this point the skill config should already be merged with the respective profession config
            // so we only need to merge the override config of the skill into our override config
            SkillFactory skillFactory = plugin.getSkillManager().getFactory(skill);
            // and now merge the result with this effet config
            config.getOverrideConfig().merge(skillFactory.getConfig(skill.getProfession()).getOverrideSection("effects." + effectName));

            effectConfigs.put(skill.getProfession().getName(), config);
        } else {
            config = effectConfigs.get(skill.getProfession().getName());
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

    public EffectConfig getConfig(Profession profession) {

        return effectConfigs.get(profession.getName());
    }

    public EffectConfig getDefaultConfig() {

        return defaultConfig;
    }
}
