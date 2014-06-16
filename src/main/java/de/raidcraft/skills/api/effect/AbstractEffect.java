package de.raidcraft.skills.api.effect;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.ambient.AmbientEffect;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.common.DiminishingReturns;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.EffectEffectStage;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.util.ConfigUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public abstract class AbstractEffect<S> implements Effect<S> {

    protected final Map<EffectEffectStage, List<AmbientEffect>> visualEffects;
    private final EffectInformation info;
    private final String name;
    private final String friendlyName;
    private final S source;
    private final CharacterTemplate target;
    private String activateMessage;
    private String deactivateMessage;
    private String renewMessage;
    private boolean enabled;
    private int damage = 0;
    private int stacks;
    private int maxStacks;
    private double priority;

    public AbstractEffect(S source, CharacterTemplate target, EffectData data) {

        this.info = data.getInformation();
        this.name = convertName(info.name());
        this.friendlyName = data.getFriendlyName();
        this.activateMessage = data.getActivateMessage();
        this.deactivateMessage = data.getDeactivateMessage();
        this.renewMessage = data.getRenewMessage();
        this.priority = (data.getEffectPriority() == 0.0 ? info.priority() : data.getEffectPriority());
        this.source = source;
        this.target = target;
        this.enabled = data.isEnabled();
        this.visualEffects = data.getAmbientEffects();

        if (this instanceof Stackable) {
            stacks = 0;
            maxStacks = data.getMaxStacks();
        }

        load(data);
    }

    private String convertName(String name) {

        return name.toLowerCase().replace(" ", "-").trim();
    }

    private void load(EffectData data) {

        if (getSource() instanceof Skill) {
            damage = (int) ConfigUtil.getTotalValue((Skill) getSource(), data.getEffectDamage());
        } else {
            damage = data.getEffectDamage().getInt("base", 0);
        }
    }

    public int getStacks() {

        return stacks;
    }

    public void setStacks(int stacks) {

        if (stacks > getMaxStacks()) stacks = getMaxStacks();
        this.stacks = stacks;
    }

    public int getMaxStacks() {

        return maxStacks;
    }

    public DiminishingReturnType getDiminishingReturnType() {

        return info.diminishingReturn();
    }

    private Field getField(Class<?> clazz, String name) throws NoSuchFieldException {

        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass == null) {
                throw e;
            }
            return getField(superclass, name);
        }
    }

    protected abstract void apply(CharacterTemplate target) throws CombatException;

    protected void debug(String message) {

        if (message == null || message.equals("")) {
            return;
        }
        if (getSource() instanceof Hero) {
            ((Hero) getSource()).debug("You->" + getTarget().getName() + ": " + message + " - " + getName());
        }
        if (getTarget() instanceof Hero) {
            ((Hero) getTarget()).debug(
                    (getSource() instanceof CharacterTemplate ? ((CharacterTemplate) getSource()).getName() : "UNKNOWN") +
                            "->You: " + message + " - " + getName()
            );
        }
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getFriendlyName() {

        return friendlyName;
    }

    @Override
    public String getDescription() {

        return info.description();
    }

    @Override
    public EffectType[] getTypes() {

        return info.types();
    }

    @Override
    public EffectElement[] getElements() {

        return info.elements();
    }

    @Override
    public boolean isEnabled() {

        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {

        this.enabled = enabled;
    }

    @Override
    public int getDamage() {

        return damage;
    }

    @Override
    public boolean isOfType(EffectType type) {

        for (EffectType t : info.types()) {
            if (type == t) {
                return true;
            }
        }
        return false;
    }

    @Override
    public double getPriority() {

        return priority;
    }

    @Override
    public void setPriority(double priority) {

        this.priority = priority;
    }

    @Override
    public S getSource() {

        return source;
    }

    @Override
    public CharacterTemplate getTarget() {

        return target;
    }

    @Override
    public void executeAmbientEffects(EffectEffectStage stage, Location location) {

        for (AmbientEffect effect : getAmbientEffects(stage)) {
            effect.run(location);
        }
    }

    @Override
    public List<AmbientEffect> getAmbientEffects(EffectEffectStage stage) {

        List<AmbientEffect> effects = visualEffects.get(stage);
        if (effects == null) {
            return new ArrayList<>();
        }
        return effects;
    }

    @Override
    public void load(ConfigurationSection data) {
        // override if needed
    }

    @Override
    public void apply() throws CombatException {

        if (getDiminishingReturnType() != DiminishingReturnType.NULL) {
            DiminishingReturns effect = target.addEffect(this, DiminishingReturns.class);
            if (effect.getReduction(getDiminishingReturnType()) > 0) {
                // lets check via reflection if we are a duration effect and apply diminishing returns
                try {
                    Field duration = getField(getClass(), "duration");
                    duration.setAccessible(true);
                    long oldDuration = duration.getLong(this);
                    long newDuration = oldDuration - (long) (oldDuration * effect.getReduction(getDiminishingReturnType()));
                    if (newDuration <= 0) {
                        throw new CombatException("Ziel ist immun gegen diesen Effekt!");
                    }
                    duration.setLong(this, newDuration);
                } catch (NoSuchFieldException | IllegalAccessException ignored) {
                    // do nothing
                }
            }
            effect.increase(getDiminishingReturnType());
        }
        // lets add ourself to the trigger listener
        if (this instanceof Triggered) {
            TriggerManager.registerListeners((Triggered) this);
        }
        apply(getTarget());
        info(activateMessage);
        // lets play some visual effects
        List<AmbientEffect> effects = visualEffects.get(EffectEffectStage.APPLY);
        if (effects != null) {
            for (AmbientEffect effect : effects) {
                effect.run(getTarget().getEntity().getLocation());
            }
        }
    }

    @Override
    public void remove() throws CombatException {

        // lets remove ourself as listener
        if (this instanceof Triggered) {
            TriggerManager.unregisterListeners((Triggered) this);
        }
        remove(getTarget());
        getTarget().removeEffect(this);
        info(deactivateMessage);
        // lets play some visual effects
        List<AmbientEffect> effects = visualEffects.get(EffectEffectStage.REMOVE);
        if (effects != null) {
            for (AmbientEffect effect : effects) {
                effect.run(getTarget().getEntity().getLocation());
            }
        }
    }

    @Override
    public void renew() throws CombatException {

        renew(getTarget());
        info(renewMessage);
        // lets play some visual effects
        List<AmbientEffect> effects = visualEffects.get(EffectEffectStage.RENEW);
        if (effects != null) {
            for (AmbientEffect effect : effects) {
                effect.run(getTarget().getEntity().getLocation());
            }
        }
    }

    protected abstract void renew(CharacterTemplate target) throws CombatException;

    protected abstract void remove(CharacterTemplate target) throws CombatException;

    protected void info(String message) {

        if (message == null || message.equals("")) {
            return;
        }
        if (getTarget() instanceof Hero) {
            info((Hero) getTarget(), message);
        }
    }

    protected void info(Hero hero, String message) {

        if (message == null || message.equals("")) {
            return;
        }
        hero.sendMessage("" + ChatColor.GRAY + message);
    }

    protected void warn(Throwable e) {

        warn(e.getMessage());
        RaidCraft.LOGGER.warning(e.getMessage());
        e.printStackTrace();
    }

    protected void warn(String message) {

        if (message == null || message.equals("")) {
            return;
        }
        if (getTarget() instanceof Hero) {
            warn((Hero) getTarget(), message);
        }
    }

    protected void warn(Hero hero, String message) {

        if (message == null || message.equals("")) {
            return;
        }
        hero.sendMessage(ChatColor.RED + message);
    }

    protected void msg(String message) {

        if (message == null || message.equals("")) {
            return;
        }
        if (getTarget() instanceof Hero) {
            msg((Hero) getTarget(), message);
        }
    }

    protected void msg(Hero hero, String message) {

        if (message == null || message.equals("")) {
            return;
        }
        hero.sendMessage(message);
    }

    protected void combatLog(String message) {

        if (message == null || message.equals("")) {
            return;
        }
        if (getTarget() instanceof Hero) {
            ((Hero) getTarget()).combatLog(this, message);
        }
    }

    @Override
    public int hashCode() {

        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Effect
                && ((Effect) obj).getName().equalsIgnoreCase(getName());
    }

    @Override
    public String toString() {

        return getFriendlyName();
    }
}
