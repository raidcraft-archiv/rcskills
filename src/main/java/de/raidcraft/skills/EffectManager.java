package de.raidcraft.skills;

import de.raidcraft.skills.api.combat.effect.Effect;
import de.raidcraft.skills.api.combat.effect.EffectInformation;
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

    protected EffectManager(SkillsPlugin plugin) {

        super(Effect.class, plugin);
    }

    @Override
    protected void loadFactories() {

        for (Class<? extends Effect> clazz : loadClasses()) {
            EffectFactory factory = plugin.configure(new EffectFactory(plugin, clazz, configDir));
            effectFactories.put(clazz.getAnnotation(EffectInformation.class).name().toLowerCase(), factory);
            effectFactoryClasses.put(clazz, factory);
        }
    }

    public <S, T> void registerClass(Class<? extends Effect<S, T>> effectClass) {

        if (effectClass.isAnnotationPresent(EffectInformation.class)) {
            EffectFactory factory = plugin.configure(new EffectFactory(plugin, effectClass, configDir));
            effectFactories.put(effectClass.getAnnotation(EffectInformation.class).name().toLowerCase(), factory);
            effectFactoryClasses.put(effectClass, factory);
        } else {
            plugin.getLogger().warning("Found effect without EffectInformation: " + effectClass.getCanonicalName());
        }
    }

    public <S, T> Effect<S, T> getEffect(S source, T target, String effect) {

        return getEffect(source, target, effect, null);
    }

    public <S, T> Effect<S, T> getEffect(S source, T target, String effect, ConfigurationSection override) {

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

    public <S, T> Effect<S, T> getEffect(S source, T target, Class<? extends Effect<S, T>> eClass) {

        return getEffect(source, target, eClass, null);
    }

    public <S, T> Effect<S, T> getEffect(S source, T target, Class<? extends Effect<S, T>> eClass, ConfigurationSection override) {

        try {
            if (effectFactoryClasses.containsKey(eClass)) {
                return effectFactoryClasses.get(eClass).create(source, target, override);
            }
        } catch (UnknownEffectException e) {
            e.printStackTrace();
            plugin.getLogger().warning(e.getMessage());
        }
        return null;
    }
}