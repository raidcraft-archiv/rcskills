package de.raidcraft.skills.api.combat.effect;

import de.raidcraft.api.config.DataMap;
import de.raidcraft.skills.api.EffectElement;
import de.raidcraft.skills.api.EffectType;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.EffectData;

/**
 * @author Silthus
 */
public abstract class AbstractEffect<S> implements Effect<S> {

    private final EffectInformation info;
    private final String name;
    private final S source;
    private final CharacterTemplate target;
    private double priority;

    public AbstractEffect(S source, CharacterTemplate target, EffectData data) {

        this.info = data.getInformation();
        this.name = convertName(info.name());
        this.priority = (data.getEffectPriority() == 0.0 ? info.priority() : data.getEffectPriority());
        this.source = source;
        this.target = target;
        load(data.getDataMap());
    }

    @Override
    public void load(DataMap data) {
        // override if needed
    }

    @Override
    public String getName() {

        return name;
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

        // TODO: check restistance and that fancy stuff
        apply(getTarget());
        if (getSource() instanceof Hero) {
            ((Hero) getSource()).debug("You->" + getTarget().getName() + ": applied effect - " + getName());
        }
        if (getTarget() instanceof Hero && getSource() instanceof CharacterTemplate) {
            ((Hero) getTarget()).debug(((CharacterTemplate) getSource()).getName() + "->You: applied effect - " + getName());
        }
    }

    protected abstract void apply(CharacterTemplate target) throws CombatException;

    private String convertName(String name) {

        return name.toLowerCase().replace(" ", "-").trim();
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Effect
                && ((Effect) obj).getName().equalsIgnoreCase(getName());
    }
}
