package de.raidcraft.skills;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.skills.api.ability.Ability;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.IgnoredEffect;
import de.raidcraft.skills.api.exceptions.InvalidEffectException;
import de.raidcraft.skills.api.exceptions.UnknownEffectException;
import de.raidcraft.skills.api.loader.GenericJarFileManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
@SuppressWarnings("unchecked")
public final class EffectManager extends GenericJarFileManager<Effect> implements Component {

    private final SkillsPlugin plugin;
    private final Map<Class<? extends Effect>, EffectFactory<? extends Effect>> effectFactoryClasses = new HashMap<>();
    private int loadedEffects;
    private int failedEffects;

    protected EffectManager(SkillsPlugin plugin) {

        super(Effect.class, new File(plugin.getDataFolder(), plugin.getCommonConfig().effect_jar_path));
        this.plugin = plugin;
        new File(plugin.getDataFolder(), plugin.getCommonConfig().effect_config_path).mkdirs();
        RaidCraft.registerComponent(EffectManager.class, this);
    }

    @Override
    public void loadFactories() {

        for (Class<? extends Effect> clazz : loadClasses()) {
            try {
                registerClass(clazz);
            } catch (InvalidEffectException | UnknownEffectException e) {
                plugin.getLogger().warning(e.getMessage());
                failedEffects++;
            }
        }
        plugin.getLogger().info("Loaded " + loadedEffects + "/" + (loadedEffects + failedEffects) + " effects.");
    }

    public <E extends Effect> void registerClass(Class<E> effectClass) throws InvalidEffectException, UnknownEffectException {

        if (effectClass.isAnnotationPresent(IgnoredEffect.class)) {
            return;
        }
        if (effectClass.isAnnotationPresent(EffectInformation.class)) {
            EffectFactory factory = new EffectFactory<>(plugin, effectClass);
            effectFactoryClasses.put(factory.getEffectClass(), factory);
            factory.createDefaults();
            loadedEffects++;
        } else {
            throw new InvalidEffectException("Found effect without EffectInformation: " + effectClass.getCanonicalName());
        }
    }

    public <E extends Effect, S> E getEffect(S source, CharacterTemplate target, Class<E> eClass) {

        return getEffect(source, target, eClass, null);
    }

    public <E extends Effect, S> E getEffect(S source, CharacterTemplate target, Class<E> eClass, Ability skill) {

        try {
            if (effectFactoryClasses.containsKey(eClass)) {
                return (E) effectFactoryClasses.get(eClass).create(source, target, skill);
            } else {
                registerClass(eClass);
                return getEffect(source, target, eClass, skill);
            }
        } catch (InvalidEffectException | UnknownEffectException e) {
            e.printStackTrace();
            plugin.getLogger().warning(e.getMessage());
        }
        return null;
    }
}
