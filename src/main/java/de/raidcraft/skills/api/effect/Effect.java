package de.raidcraft.skills.api.effect;

import de.raidcraft.api.ambient.AmbientEffect;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.skill.EffectEffectStage;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * An effect is something that can be applied to entities around in the world.
 * An effect can also be scheduled to apply periodically or after a set time.
 *
 * @author Silthus
 */
@IgnoredEffect
public interface Effect<S> {

    String getName();

    String getFriendlyName();

    String getDescription();

    EffectType[] getTypes();

    EffectElement[] getElements();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    int getDamage();

    boolean isOfType(EffectType type);

    boolean isOfAnyType(EffectType... types);

    double getPriority();

    void setPriority(double priority);

    S getSource();

    CharacterTemplate getTarget();

    void executeAmbientEffects(EffectEffectStage stage, Location location);

    List<AmbientEffect> getAmbientEffects(EffectEffectStage stage);

    void load(ConfigurationSection data);

    void apply() throws CombatException;

    void remove() throws CombatException;

    void renew() throws CombatException;
}
