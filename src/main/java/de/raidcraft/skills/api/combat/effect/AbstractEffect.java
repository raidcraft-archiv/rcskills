package de.raidcraft.skills.api.combat.effect;

import de.raidcraft.skills.api.persistance.EffectData;

/**
 * @author Silthus
 */
public abstract class AbstractEffect<S, T> implements Effect<S, T> {

    private final EffectInformation info;
    private final String name;
    private final S source;
    private final T target;
    private double priority;

    protected AbstractEffect(S source, T target, EffectData data) {

        this.info = data.getInformation();
        this.name = convertName(info.name());
        this.priority = (data.getEffectPriority() == 0.0 ? info.priority() : data.getEffectPriority());
        this.source = source;
        this.target = target;
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
    public Type[] getTypes() {

        return info.types();
    }

    @Override
    public boolean isOfType(Type type) {

        for (Type t : info.types()) {
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
    public T getTarget() {

        return target;
    }

    private String convertName(String name) {

        return name.toLowerCase().replace(" ", "-").trim();
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Effect
                && ((Effect) obj).getName().equalsIgnoreCase(getName());
    }
}
