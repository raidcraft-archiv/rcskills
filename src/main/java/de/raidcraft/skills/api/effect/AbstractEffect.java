package de.raidcraft.skills.api.effect;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.LevelableSkill;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.api.trigger.Triggered;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public abstract class AbstractEffect<S> implements Effect<S> {

    private final EffectInformation info;
    private final String name;
    private final String friendlyName;
    private final S source;
    private final CharacterTemplate target;
    private boolean enabled;
    private int damage = 0;
    private int stacks;
    private int maxStacks;
    private double priority;

    public AbstractEffect(S source, CharacterTemplate target, EffectData data) {

        this.info = data.getInformation();
        this.name = convertName(info.name());
        this.friendlyName = data.getFriendlyName();
        this.priority = (data.getEffectPriority() == 0.0 ? info.priority() : data.getEffectPriority());
        this.source = source;
        this.target = target;
        this.enabled = data.isEnabled();

        if (this instanceof Stackable) {
            stacks = 0;
            maxStacks = data.getMaxStacks();
        }

        load(data);
    }

    private void load(EffectData data) {

        damage = data.getEffectDamage();
        if (getSource() instanceof Hero) {
            damage += data.getEffectDamageLevelModifier() * ((Hero) getSource()).getLevel().getLevel();
        }
        if (getSource() instanceof Skill) {
            damage += data.getEffectDamageLevelModifier() * ((Skill) getSource()).getHero().getLevel().getLevel();
            damage += data.getEffectDamageProfLevelModifier() * ((Skill) getSource()).getProfession().getLevel().getLevel();
        }
        if (getSource() instanceof LevelableSkill) {
            damage += data.getEffectDamageSkillLevelModifier() * ((LevelableSkill) getSource()).getLevel().getLevel();
        }
    }

    @Override
    public void load(ConfigurationSection data) {
        // override if needed
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
    public void apply() throws CombatException {

        // lets add ourself to the trigger listener
        if (this instanceof Triggered) {
            TriggerManager.registerListeners((Triggered) this);
        }
        apply(getTarget());
        debug("applied effect");
    }

    @Override
    public void remove() throws CombatException {

        // lets remove ourself as listener
        if (this instanceof Triggered) {
            TriggerManager.unregisterListeners((Triggered) this);
        }
        remove(getTarget());
        getTarget().removeEffect(this);
        debug("removed effect");
    }

    @Override
    public void renew() throws CombatException {

        renew(getTarget());
        debug("renewed effect");
    }

    protected abstract void apply(CharacterTemplate target) throws CombatException;

    protected abstract void remove(CharacterTemplate target) throws CombatException;

    protected abstract void renew(CharacterTemplate target) throws CombatException;

    protected void debug(String message) {

        if (getSource() instanceof Hero) {
            ((Hero) getSource()).debug("You->" + getTarget().getName() + ": " + message + " - " + getName());
        }
        if (getTarget() instanceof Hero) {
            ((Hero) getTarget()).debug(
                    (getSource() instanceof CharacterTemplate ? ((CharacterTemplate) getSource()).getName() : "UNKNOWN") +
                            "->You: " + message + " - " + getName());
        }
    }

    protected void warn(String message) {

        if (getTarget() instanceof Hero) {
            warn((Hero) getTarget(), message);
        }
    }

    protected void warn(Hero hero, String message) {

        hero.sendMessage(ChatColor.RED + message);
    }

    protected void info(String message) {

        if (getTarget() instanceof Hero) {
            info((Hero) getTarget(), message);
        }
    }

    protected void info(Hero hero, String message) {

        hero.sendMessage("" + ChatColor.GRAY + ChatColor.ITALIC + message);
    }

    protected void msg(String message) {

        if (getTarget() instanceof Hero) {
            msg((Hero) getTarget(), message);
        }
    }

    protected void msg(Hero hero, String message) {

        hero.sendMessage(message);
    }

    private String convertName(String name) {

        return name.toLowerCase().replace(" ", "-").trim();
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
