package de.raidcraft.skills.api.effect;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.Bukkit;

/**
 * @author Silthus
 */
public abstract class DelayedEffect<S> extends ScheduledEffect<S> {

    protected long delay = 0;

    public DelayedEffect(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        load(data);
    }

    private void load(EffectData data) {

        // load the delay
        this.delay = data.getEffectDelay();
        if (getSource() instanceof Hero) {
            Hero hero = (Hero) getSource();
            this.delay += (data.getEffectDelayLevelModifier() * hero.getLevel().getLevel())
                    + (data.getEffectDelayProfLevelModifier() * hero.getSelectedProfession().getLevel().getLevel());
        }
    }

    public long getDelay() {

        return delay;
    }

    @Override
    public void startTask() {

        setTask(Bukkit.getScheduler().runTaskLater(
                RaidCraft.getComponent(SkillsPlugin.class),
                this,
                getDelay()
        ));
    }

    @Override
    public void apply() throws CombatException {

        if (isStarted()) {
            renew();
        } else {
            // only start the task and dont apply yet
            startTask();
        }
    }

    @Override
    public void remove() throws CombatException {

        if (isStarted()) {
            stopTask();
            super.remove();
        } else {
            // this means the effect was already applied
            super.remove();
        }
    }

    @Override
    public void renew() throws CombatException {

        stopTask();
        startTask();
        super.renew();
    }

    @Override
    public void run() {

        try {
            super.apply();
            stopTask();
        } catch (CombatException e) {
            warn(e.getMessage());
        }
    }
}
