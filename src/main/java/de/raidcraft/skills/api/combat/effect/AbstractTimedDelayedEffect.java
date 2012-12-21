package de.raidcraft.skills.api.combat.effect;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Silthus
 */
public abstract class AbstractTimedDelayedEffect<S> extends AbstractDelayedEffect<S> {

    private BukkitTask removalTask = null;

    public AbstractTimedDelayedEffect(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    public void remove() throws CombatException {

        if (removalTask == null) {
            super.remove();
        } else {
            removalTask.cancel();
            super.remove();
        }
    }

    @Override
    public void run() {

        try {
            super.apply();
            setTask(null);
            this.removalTask = Bukkit.getScheduler().runTaskLater(
                    RaidCraft.getComponent(SkillsPlugin.class),
                    new Runnable() {
                        @Override
                        public void run() {

                            try {
                                remove();
                            } catch (CombatException e) {
                                if (getSource() instanceof Hero) {
                                    ((Hero) getSource()).sendMessage(ChatColor.RED + e.getMessage());
                                }
                            }
                        }
                    }, getDelay() + getDuration()
            );
        } catch (CombatException e) {
            if (getSource() instanceof Hero) {
                ((Hero) getSource()).sendMessage(ChatColor.RED + e.getMessage());
            }
        }
    }
}
