package de.raidcraft.skills.api.character;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.combat.attack.Attack;
import de.raidcraft.skills.api.combat.effect.Effect;
import de.raidcraft.skills.api.combat.effect.PeriodicEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.Bukkit;
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
    private final Map<Class<? extends Effect<?, CharacterTemplate>>, Effect<?, CharacterTemplate>> effects = new HashMap<>();
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

        int newHealth = getHealth() - damage;
        if (newHealth < 0) newHealth = 0;
        setHealth(newHealth);
        getEntity().playEffect(EntityEffect.HURT);
        if (this instanceof Hero) {
            ((Hero)this).debug("You took: " + damage + "dmg - [" + newHealth + "]");
        }
        if (getHealth() <= 0) {
            kill();
        }
    }

    @Override
    public void damage(Attack attack) {

        damage(attack.getDamage());
        if (attack.getSource() instanceof Hero) {
            ((Hero) attack.getSource()).debug(
                    "You->" + getName() + ": " + attack.getDamage() + "dmg - " + getName() + "[" + getHealth() + "]");
        }
    }

    @Override
    public void kill(CharacterTemplate killer) {

        kill();
    }

    @Override
    public void kill() {

        getEntity().setHealth(0);
        getEntity().playEffect(EntityEffect.DEATH);
        // TODO: add playSound effect when it works...
    }

    @Override
    public <S> Effect<S, CharacterTemplate> addEffect(S source, Class<? extends Effect<S, CharacterTemplate>> eClass)
            throws CombatException {

        Effect<S, CharacterTemplate> effect =
                RaidCraft.getComponent(SkillsPlugin.class).getEffectManager().getEffect(source, this, eClass);
        // lets check the priority of the existing effect
        if (effects.containsKey(eClass)) {
            if (effects.get(eClass).getPriority() > effect.getPriority()) {
                throw new CombatException("Es ist bereits ein stärkerer Effekt aktiv!");
            }
        }
        // TODO: do some fancy resistence checks
        effects.put(eClass, effect);
        // applying the effect will start the task if periodic or trigger it once
        effect.apply();
        return effect;
    }

    @Override
    public <S> void removeEffect(Class<? extends Effect<S, CharacterTemplate>> eClass) {

        Effect<?, CharacterTemplate> effect = effects.remove(eClass);
        if (effect instanceof PeriodicEffect) {
            Bukkit.getScheduler().cancelTask(((PeriodicEffect) effect).getTaskId());
        }
        // some debug output
        if (effect != null) {
            if (effect.getSource() instanceof Hero) {
                ((Hero) effect.getSource()).debug(
                        "You->" + getName() + ": effect(" + effect.getPriority() + ") manually removed - " + effect.getName());
            }
            if (effect.getTarget() instanceof Hero) {
                ((Hero) effect.getTarget()).debug(
                        (effect.getSource() instanceof CharacterTemplate ? ((CharacterTemplate) effect.getSource()).getName() : "UNKNOWN")
                                + "->You: effect(" + effect.getPriority() + ") manually removed - " + effect.getName()
                );
            }
        }
    }

    @Override
    public void clearEffects() {

        effects.clear();
    }


    @Override
    public <S> boolean hasEffect(Class<? extends Effect<S, CharacterTemplate>> eClass) {

        return effects.containsKey(eClass);
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
