package de.raidcraft.skills.api.character;

import de.raidcraft.skills.api.ability.Ability;
import org.bukkit.entity.LivingEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public abstract class AbstractSkilledCharacter<T extends SkilledCharacter> extends AbstractCharacterTemplate implements SkilledCharacter<T> {

    private final Map<String, Ability<T>> abilities = new HashMap<>();

    public AbstractSkilledCharacter(LivingEntity entity) {

        super(entity);
    }

    @Override
    public Collection<Ability<T>> getAbilties() {

        return abilities.values();
    }

    @Override
    public void addAbility(Ability<T> ability) {

        abilities.put(ability.getName(), ability);
    }

    @Override
    public Ability<T> removeAbility(String name) {

        return abilities.remove(name);
    }

    @Override
    public Ability<T> getAbility(String name) {

        return abilities.get(name);
    }
}
