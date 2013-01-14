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
public abstract class PeriodicExpirableEffect<S> extends PeriodicEffect<S> {

    protected long duration;
    protected long remainingTicks = 0;

    public PeriodicExpirableEffect(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        load(data);
    }

    private void load(EffectData data) {

        duration = data.getEffectDuration();
        if (getSource() instanceof Hero) {
            Hero hero = (Hero) getSource();
            duration += (data.getEffectDurationLevelModifier() * hero.getLevel().getLevel());
        }
        if (getSource() instanceof Skill) {
            duration += data.getEffectDurationLevelModifier() * ((Skill) getSource()).getHero().getLevel().getLevel();
            duration += data.getEffectDurationProfLevelModifier() * ((Skill) getSource()).getProfession().getLevel().getLevel();
        }
        if (getSource() instanceof LevelableSkill) {
            duration += data.getEffectDurationSkillLevelModifier() * ((LevelableSkill) getSource()).getLevel().getLevel();
        }
    }

    @Override
    public double getPriority() {

        return super.getPriority() + getRemainingTicks();
    }

    public long getDuration() {

        return duration;
    }

    public long getRemainingTicks() {

        return remainingTicks;
    }

    public int getTickCount() {

        return (int) (getDuration() / getInterval());
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
            this.remainingTicks = getDuration();
        }
    }

    @Override
    public void renew() throws CombatException {

        if (isStarted()) {
            // reset the remaining ticks
            this.remainingTicks = getDuration();
            // this is a periodic effect and runs forever until cancelled so simply call renew
            super.renew();
        }
    }

    @Override
    public void run() {

        if (this.remainingTicks > 0) {
            try {
                tick(getTarget());
                debug("effect ticked");
            } catch (CombatException e) {
                warn(e.getMessage());
            }
            // set the ticks that remain in this task
            // we want them outside the catch to make sure failed attempts count as ticked
            this.remainingTicks -= getInterval();
        }

        // lets cancel the task if the effect expired
        if (this.remainingTicks <= 0) {
            try {
                remove();
            } catch (CombatException e) {
                warn(e.getMessage());
            }
        }
    }
}
