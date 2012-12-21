package de.raidcraft.skills.api.combat.effect;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public abstract class AbstractDelayedEffect<S> extends AbstractTimedEffect<S> {

    protected int delay = 0;

    public AbstractDelayedEffect(S source, CharacterTemplate target, EffectData data) {

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

    public int getDelay() {

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

        // only start the task and dont apply yet
        startTask();
    }

    @Override
    public void remove() throws CombatException {

        if (isStarted()) {
            stopTask();
        } else {
            // this means the effect was already applied
            super.remove();
        }
    }

    @Override
    public void run() {

        try {
            ((AbstractEffect)this).apply();
            stopTask();
            getTarget().removeEffect(this);
        } catch (CombatException e) {
            if (getSource() instanceof Hero) {
                ((Hero) getSource()).sendMessage(ChatColor.RED + e.getMessage());
            }
        }
    }
}
