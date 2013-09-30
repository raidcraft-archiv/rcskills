package de.raidcraft.skills.api.character;

import de.raidcraft.skills.api.ability.Ability;
import de.raidcraft.skills.api.ability.Useable;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public abstract class AbstractSkilledCharacter<T extends SkilledCharacter> extends AbstractCharacterTemplate implements SkilledCharacter<T> {

    private final Map<String, Ability<T>> abilities = new CaseInsensitiveMap<>();

    public AbstractSkilledCharacter(LivingEntity entity) {

        super(entity);
    }

    @Override
    public List<Ability<T>> getAbilties() {

        return new ArrayList<>(abilities.values());
    }

    @Override
    public List<Ability<T>> getUseableAbilities() {

        ArrayList<Ability<T>> useables = new ArrayList<>();
        for (Ability<T> ability : abilities.values()) {
            if (ability instanceof Useable && !ability.isOnCooldown()) {
                useables.add(ability);
            }
        }
        return useables;
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
