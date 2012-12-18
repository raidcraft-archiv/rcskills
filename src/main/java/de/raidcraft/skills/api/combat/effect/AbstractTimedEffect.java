package de.raidcraft.skills.api.combat.effect;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.PeriodicEffectData;

/**
 * @author Silthus
 */
public abstract class AbstractTimedEffect<S, T> extends AbstractEffect<S, T> implements TimedEffect<S, T> {

    private final PeriodicEffectData data;

    protected AbstractTimedEffect(S source, T target, PeriodicEffectData data) {

        super(source, target, data);
        this.data = data;
    }

    @Override
    public int getDuration() {

        int duration = data.getEffectDuration();
        if (getSource() instanceof Hero) {
            Hero hero = (Hero) getSource();
            duration += (data.getEffectDurationLevelModifier() * hero.getLevel().getLevel())
                    + (data.getEffectDurationProfLevelModifier() * hero.getSelectedProfession().getLevel().getLevel());
        }
        return duration;
    }
}
