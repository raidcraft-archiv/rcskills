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

    public String getName();

    public String getFriendlyName();

    public String getDescription();

    public EffectType[] getTypes();

    public EffectElement[] getElements();

    public boolean isEnabled();

    public void setEnabled(boolean enabled);

    public int getDamage();

    public boolean isOfType(EffectType type);

    public boolean isOfAnyType(EffectType... types);

    public double getPriority();

    public void setPriority(double priority);

    public S getSource();

    public CharacterTemplate getTarget();

    public void executeAmbientEffects(EffectEffectStage stage, Location location);

    public List<AmbientEffect> getAmbientEffects(EffectEffectStage stage);

    public void load(ConfigurationSection data);

    public void apply() throws CombatException;

    public void remove() throws CombatException;

    public void renew() throws CombatException;
}
