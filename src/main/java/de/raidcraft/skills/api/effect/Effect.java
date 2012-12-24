package de.raidcraft.skills.api.effect;

import de.raidcraft.skills.api.EffectElement;
import de.raidcraft.skills.api.EffectType;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;
import org.bukkit.configuration.ConfigurationSection;

/**
 * An effect is something that can be applied to entities around in the world.
 * An effect can also be scheduled to apply periodically or after a set time.
 *
 * @author Silthus
 */
public interface Effect<S> {

    public String getName();

    public String getDescription();

    public EffectType[] getTypes();

    public EffectElement[] getElements();

    public boolean isOfType(EffectType type);

    public double getPriority();

    public void setPriority(double priority);

    public S getSource();

    public CharacterTemplate getTarget();

    public void load(ConfigurationSection data);

    public void apply() throws CombatException;

    public void remove() throws CombatException;

    public void renew() throws CombatException;
}
