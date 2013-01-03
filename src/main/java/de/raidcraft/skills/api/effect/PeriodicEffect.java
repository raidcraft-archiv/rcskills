package de.raidcraft.skills.api.effect;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.LevelableSkill;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.Bukkit;

/**
 * @author Silthus
 */
public abstract class PeriodicEffect<S> extends ScheduledEffect<S> {

    protected long delay;
    protected long interval;

    public PeriodicEffect(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        load(data);
    }

    private void load(EffectData data) {

        delay = data.getEffectDelay();
        interval = data.getEffectDuration();
        if (getSource() instanceof Hero) {
            Hero hero = (Hero) getSource();
            delay += (data.getEffectDelayLevelModifier() * hero.getLevel().getLevel());

            interval += (data.getEffectIntervalLevelModifier() * hero.getLevel().getLevel());
        }
        if (getSource() instanceof Skill) {
            delay += data.getEffectDelayLevelModifier() * ((Skill) getSource()).getHero().getLevel().getLevel();
            delay += data.getEffectDelayProfLevelModifier() * ((Skill) getSource()).getProfession().getLevel().getLevel();

            interval += data.getEffectIntervalLevelModifier() * ((Skill) getSource()).getHero().getLevel().getLevel();
            interval += data.getEffectIntervalProfLevelModifier() * ((Skill) getSource()).getProfession().getLevel().getLevel();
        }
        if (getSource() instanceof LevelableSkill) {
            delay += data.getEffectDelaySkillLevelModifier() * ((LevelableSkill) getSource()).getLevel().getLevel();

            interval += data.getEffectIntervalSkillLevelModifier() * ((LevelableSkill) getSource()).getLevel().getLevel();
        }
    }

    public long getDelay() {

        return delay;
    }

    public long getInterval() {

        return interval;
    }

    @Override
    public void startTask() {

        if (!isStarted()) {
            setTask(Bukkit.getScheduler().runTaskTimer(
                    RaidCraft.getComponent(SkillsPlugin.class),
                    this,
                    getDelay(),
                    getInterval()
            ));
        }
    }

    @Override
    public void apply() throws CombatException {

        // dont start if interval is not configured
        if (getInterval() < 1) {
            remove();
        }
        if (!isStarted()) {
            // lets start the task
            startTask();
            super.apply();
        }
    }

    @Override
    public void remove() throws CombatException {

        if (isStarted()) {
            stopTask();
            super.remove();
        }
    }

    @Override
    public void renew() throws CombatException {

        if (isStarted()) {
            // this is a periodic effect and runs forever until cancelled so simply call renew
            super.renew();
        }
    }

    protected abstract void tick(CharacterTemplate target) throws CombatException;

    @Override
    public void run() {

        try {
            // a periodic effects apply method is called everytime the effect ticks
            tick(getTarget());
            debug("effect ticked");
        } catch (CombatException e) {
            warn(e.getMessage());
        }
    }
}
