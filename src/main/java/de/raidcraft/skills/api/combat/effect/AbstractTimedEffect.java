package de.raidcraft.skills.api.combat.effect;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.EffectData;

/**
 * @author Silthus
 */
public abstract class AbstractTimedEffect<S, T> extends AbstractEffect<S, T> implements TimedEffect<S, T> {

    private long duration = 0;

    public AbstractTimedEffect(S source, T target, EffectData data) {

        super(source, target, data);
        load(data);
    }

    private void load(EffectData data) {

        this.duration = data.getEffectDuration();
        if (getSource() instanceof Hero) {
            Hero hero = (Hero) getSource();
            this.duration += (data.getEffectDurationLevelModifier() * hero.getLevel().getLevel())
                    + (data.getEffectDurationProfLevelModifier() * hero.getSelectedProfession().getLevel().getLevel());
        }
    }

    @Override
    public long getDuration() {

        return duration;
    }
}
