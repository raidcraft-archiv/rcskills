package de.raidcraft.skills.api.character;

import de.raidcraft.skills.api.combat.attack.Attack;
import de.raidcraft.skills.api.combat.effect.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public abstract class AbstractCharacterTemplate implements CharacterTemplate {

    private final String name;
    private final LivingEntity entity;
    private final Map<String, Effect> effects = new HashMap<>();
    private boolean inCombat = false;

    public AbstractCharacterTemplate(LivingEntity entity) {

        this.entity = entity;
        this.name = (entity instanceof Player) ? ((Player) entity).getName() : entity.getType().getName();
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public LivingEntity getEntity() {

        return entity;
    }

    @Override
    public void damage(int damage) {

        int health = getHealth() - damage;
        if (health < 0) health = 0;
        setHealth(health);
        getEntity().playEffect(EntityEffect.HURT);
    }

    @Override
    public void damage(Attack attack) {

        damage(attack.getDamage());
    }

    @Override
    public void kill(CharacterTemplate killer) {

        kill();
    }

    @Override
    public void kill() {
        //TODO: implement
    }

    @Override
    public void addEffect(Effect effect) {

        // TODO: add priority checks
        effects.put(effect.getName().toLowerCase(), effect);
    }

    @Override
    public void removeEffect(Effect effect) {

        effects.remove(effect.getName().toLowerCase());
    }

    @Override
    public void clearEffects() {

        effects.clear();
    }

    @Override
    public boolean hasEffect(Effect effect) {

        return effects.containsKey(effect.getName().toLowerCase());
    }

    @Override
    public boolean hasEffectType(Effect.Type type) {

        for (Effect effect : effects.values()) {
            if (effect.isOfType(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInCombat() {

        return inCombat;
    }

    @Override
    public void setInCombat(boolean inCombat) {

        this.inCombat = inCombat;
    }
}
