package de.raidcraft.skills.api.character;

import de.raidcraft.skills.api.skill.Ability;
import org.bukkit.entity.LivingEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public abstract class AbstractSkilledCharacter<T extends Ability<? extends SkilledCharacter>> extends AbstractCharacterTemplate implements SkilledCharacter<T> {

    private final Map<String, T> abilities = new HashMap<>();

    public AbstractSkilledCharacter(LivingEntity entity) {

        super(entity);
    }

    protected abstract void loadAbilities();

    @Override
    public Collection<T> getAbilties() {

        return abilities.values();
    }

    @Override
    public void addAbility(T ability) {

        abilities.put(ability.getName(), ability);
    }

    @Override
    public T removeAbility(String name) {

        return abilities.remove(name);
    }

    @Override
    public T getAbility(String name) {

        return abilities.get(name);
    }
}
