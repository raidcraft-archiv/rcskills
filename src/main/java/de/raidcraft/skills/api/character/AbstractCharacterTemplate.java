package de.raidcraft.skills.api.character;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.config.DataMap;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.EffectType;
import de.raidcraft.skills.api.combat.attack.Attack;
import de.raidcraft.skills.api.combat.effect.Effect;
import de.raidcraft.skills.api.combat.effect.ScheduledEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.exceptions.UnknownEffectException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.EntityEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Silthus
 */
public abstract class AbstractCharacterTemplate implements CharacterTemplate {

    private static final Random RANDOM = new Random();

    private final String name;
    private final LivingEntity entity;
    private final Map<Class<? extends ScheduledEffect>, ScheduledEffect> effects = new HashMap<>();
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
        if (killer instanceof Hero) {
            ((Hero) killer).debug("YOU killed " + getName());
        }
    }

    @Override
    public void kill() {

        getEntity().setHealth(0);
        // play the death sound
        getEntity().getWorld().playSound(
                getEntity().getLocation(),
                getDeathSound(getEntity().getType()),
                1.0F,
                getSoundStrength(getEntity())
        );
        // play the death effect
        getEntity().playEffect(EntityEffect.DEATH);
    }

    @SuppressWarnings("unchecked")
    private <S> void addEffect(Class<? extends Effect<S>> eClass, Effect<S> effect)
            throws CombatException {

        if (!(effect instanceof ScheduledEffect)) {
            effect.apply();
        } else {
            Class<? extends ScheduledEffect<S>> effectClass =
                    (Class<? extends ScheduledEffect<S>>) eClass;
            ScheduledEffect<S> scheduledEffect = (ScheduledEffect<S>) effect;
            // lets only apply scheduled effects because the other ones are only applied exactly once
            if (effects.containsKey(effectClass)) {
                ScheduledEffect<?> existingEffect = effects.get(effectClass);
                // lets check priorities
                // the -1.0 is a special case for skill casted effects
                if (scheduledEffect.getPriority() == -1.0 && scheduledEffect.getSource().equals(existingEffect.getSource())) {
                    removeEffect(effectClass);
                    addEffect(effectClass, scheduledEffect);
                } else if (existingEffect.getPriority() > effect.getPriority()) {
                    throw new CombatException("Es ist bereits ein stärkerer Effekt aktiv!");
                } else if (existingEffect.getPriority() == scheduledEffect.getPriority()) {
                    existingEffect.renew();
                } else {
                    // remove the existing effect and apply our stronger effect
                    removeEffect(effectClass);
                    addEffect(effectClass, scheduledEffect);
                }
            } else {
                effects.put(effectClass, scheduledEffect);
                scheduledEffect.apply();
            }
        }
    }

    @Override
    public final Effect<CharacterTemplate> addEffect(Skill skill, Class<? extends Effect<CharacterTemplate>> eClass) throws CombatException {

        DataMap config = skill.getEffectConfiguration();
        // okay we have a config from the skills now lets create a wicked effect with lots of override
        Effect<CharacterTemplate> effect = RaidCraft.getComponent(SkillsPlugin.class).getEffectManager()
                .getEffect(skill.getHero(), this, eClass, config);
        addEffect(eClass, effect);
        return effect;
    }

    @Override
    public final <S> Effect<S> addEffect(S source, Class<? extends Effect<S>> eClass)
            throws CombatException {

        Effect<S> effect = RaidCraft.getComponent(SkillsPlugin.class).getEffectManager().getEffect(source, this, eClass);
        addEffect(eClass, effect);
        return effect;
    }

    @Override
    public final void removeEffect(Class<? extends ScheduledEffect> eClass) throws CombatException {

        ScheduledEffect<?> effect = effects.remove(eClass);
        // some debug output
        if (effect != null) {
            if (effect.isStarted()) {
                effect.remove();
            }
        }
    }

    @Override
    public void removeEffect(Effect effect) throws CombatException {

        try {
            removeEffect(RaidCraft.getComponent(SkillsPlugin.class).getEffectManager().getEffectForName(effect.getName()));
        } catch (UnknownEffectException e) {
            throw new CombatException(e.getMessage());
        }
    }

    @Override
    public final void clearEffects() {

        effects.clear();
    }


    @Override
    public final  <S> boolean hasEffect(Class<? extends ScheduledEffect<S>> eClass) {

        return effects.containsKey(eClass);
    }

    @Override
    public final boolean hasEffectType(EffectType type) {

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

    protected float getSoundStrength(LivingEntity target) {

        if (!(target instanceof Ageable)) {
            return 1.0F;
        }
        if (((Ageable) target).isAdult()) {
            return (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2F + 1.0F;
        } else {
            return (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2F + 1.5F;
        }
    }

    protected Sound getDeathSound(EntityType type) {

        switch (type) {

            case COW:
                return Sound.COW_IDLE;
            case BLAZE:
                return Sound.BLAZE_DEATH;
            case CHICKEN:
                return Sound.CHICKEN_HURT;
            case CREEPER:
                return Sound.CREEPER_DEATH;
            case SKELETON:
                return Sound.SKELETON_DEATH;
            case IRON_GOLEM:
                return Sound.IRONGOLEM_DEATH;
            case GHAST:
                return Sound.GHAST_DEATH;
            case PIG:
                return Sound.PIG_DEATH;
            case OCELOT:
                return Sound.CAT_HIT;
            case SHEEP:
                return Sound.SHEEP_IDLE;
            case SPIDER:
            case CAVE_SPIDER:
                return Sound.SPIDER_DEATH;
            case WOLF:
                return Sound.WOLF_DEATH;
            case ZOMBIE:
                return Sound.ZOMBIE_DEATH;
            case PIG_ZOMBIE:
                return Sound.ZOMBIE_PIG_DEATH;
            default:
                return Sound.HURT_FLESH;
        }
    }
}
