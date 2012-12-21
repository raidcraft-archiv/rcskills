package de.raidcraft.skills;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.effect.Effect;
import de.raidcraft.skills.api.combat.effect.EffectInformation;
import de.raidcraft.skills.api.combat.effect.ScheduledEffect;
import de.raidcraft.skills.api.exceptions.InvalidEffectException;
import de.raidcraft.skills.api.exceptions.UnknownEffectException;
import de.raidcraft.skills.api.loader.GenericJarFileManager;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class EffectManager extends GenericJarFileManager<Effect> {

    private final Map<String, EffectFactory> effectFactories = new HashMap<>();
    private final Map<Class<? extends Effect>, EffectFactory> effectFactoryClasses = new HashMap<>();
    private final Map<String, Class<? extends ScheduledEffect>> scheduledEffectNames = new HashMap<>();

    protected EffectManager(SkillsPlugin plugin) {

        super(Effect.class, plugin);
    }

    @Override
    protected void loadFactories() {

        for (Class<? extends Effect> clazz : loadClasses()) {
            try {
                registerClass(clazz);
            } catch (InvalidEffectException e) {
                plugin.getLogger().warning(e.getMessage());
            }
        }
    }

    public void registerClass(Class<? extends Effect> effectClass) throws InvalidEffectException {

        if (effectClass.isAnnotationPresent(EffectInformation.class)) {
            EffectFactory factory = plugin.configure(new EffectFactory(plugin, effectClass, configDir));
            effectFactories.put(effectClass.getAnnotation(EffectInformation.class).name().toLowerCase(), factory);
            effectFactoryClasses.put(effectClass, factory);
            if (ScheduledEffect.class.isAssignableFrom(effectClass)) {
                scheduledEffectNames.put(factory.getName(), (Class<? extends ScheduledEffect>) effectClass);
            }
        } else {
            throw new InvalidEffectException("Found effect without EffectInformation: " + effectClass.getCanonicalName());
        }
    }

    public <S> Effect<S> getEffect(S source, CharacterTemplate target, String effect) {

        return getEffect(source, target, effect, null);
    }

    public <S> Effect<S> getEffect(S source, CharacterTemplate target, String effect, ConfigurationSection override) {

        try {
            effect = effect.toLowerCase();
            if (effectFactories.containsKey(effect)) {
                return effectFactories.get(effect).create(source, target, override);
            }
        } catch (UnknownEffectException e) {
            e.printStackTrace();
            plugin.getLogger().warning(e.getMessage());
        }
        return null;
    }

    public <S> Effect<S> getEffect(S source, CharacterTemplate target, Class<? extends Effect<S>> eClass) {

        return getEffect(source, target, eClass, null);
    }

    public <S> Effect<S> getEffect(S source, CharacterTemplate target, Class<? extends Effect<S>> eClass, ConfigurationSection override) {

        try {
            if (effectFactoryClasses.containsKey(eClass)) {
                return effectFactoryClasses.get(eClass).create(source, target, override);
            } else {
                registerClass(eClass);
                return getEffect(source, target, eClass, override);
            }
        } catch (InvalidEffectException | UnknownEffectException e) {
            e.printStackTrace();
            plugin.getLogger().warning(e.getMessage());
        }
        return null;
    }

    public Class<? extends ScheduledEffect> getEffectForName(String name) throws UnknownEffectException {

        if (scheduledEffectNames.containsKey(name)) {
            return scheduledEffectNames.get(name);
        }
        throw new UnknownEffectException("Es gibt keinen Effekt mit dem Namen: " + name);
    }
}
