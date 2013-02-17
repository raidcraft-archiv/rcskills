package de.raidcraft.skills.api.combat.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.common.Combat;
import de.raidcraft.skills.api.exceptions.CombatException;
import org.bukkit.Bukkit;

/**
 * @author Silthus
 */
public abstract class AbstractAction<T> implements Action<T> {

    private final T source;

    protected AbstractAction(final T source) {

        this.source = source;
        // lets trigger the combat effect
        if (source instanceof CharacterTemplate) {
            // add the combat effect with a 2 tick delay in order for the combat allowed stuff to trigger
            Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(SkillsPlugin.class), new Runnable() {
                @Override
                public void run() {

                    try {
                        ((CharacterTemplate)source).addEffect(source, Combat.class);
                    } catch (CombatException e) {
                        RaidCraft.LOGGER.warning(e.getMessage());
                    }
                }
            }, 2L);
        }
    }

    @Override
    public T getSource() {

        return source;
    }
}
