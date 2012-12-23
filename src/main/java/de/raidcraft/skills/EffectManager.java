package de.raidcraft.skills;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.exceptions.InvalidEffectException;
import de.raidcraft.skills.api.exceptions.UnknownEffectException;
import de.raidcraft.skills.api.loader.GenericJarFileManager;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
@SuppressWarnings("unchecked")
public final class EffectManager extends GenericJarFileManager<Effect> {

    private final Map<String, EffectFactory> effectFactories = new HashMap<>();
    private final Map<Class<? extends Effect>, EffectFactory<? extends Effect>> effectFactoryClasses = new HashMap<>();

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

    public <E extends Effect> void registerClass(Class<E> effectClass) throws InvalidEffectException {

        if (effectClass.isAnnotationPresent(EffectInformation.class)) {
            EffectFactory factory = plugin.configure(new EffectFactory<>(plugin, effectClass, configDir));
            effectFactories.put(factory.getName(), factory);
            effectFactoryClasses.put(effectClass, factory);
        } else {
            throw new InvalidEffectException("Found effect without EffectInformation: " + effectClass.getCanonicalName());
        }
    }

    public Effect getEffect(Object source, CharacterTemplate target, String effect) {

        return getEffect(source, target, effect, null);
    }

    public Effect getEffect(Object source, CharacterTemplate target, String effect, ConfigurationSection override) {

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

    public <E extends Effect> E getEffect(Object source, CharacterTemplate target, Class<E> eClass) {

        return getEffect(source, target, eClass, null);
    }

    public <E extends Effect> E getEffect(Object source, CharacterTemplate target, Class<E> eClass, ConfigurationSection override) {

        try {
            if (effectFactoryClasses.containsKey(eClass)) {
                return (E) effectFactoryClasses.get(eClass).create(source, target, override);
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

    public Class<? extends Effect> getEffectForName(String name) throws UnknownEffectException {

        if (effectFactories.containsKey(name)) {
            return effectFactories.get(name).getEffectClass();
        }
        throw new UnknownEffectException("Es gibt keinen Effekt mit dem Namen: " + name);
    }
}
