package de.raidcraft.skills.api.combat.effect.common;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.effect.AbstractPeriodicEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public class CombatEffect extends AbstractPeriodicEffect<CharacterTemplate, CharacterTemplate> {

    public CombatEffect(CharacterTemplate source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        // the combat effect should always have a default priority of 1.0
        setPriority(1.0);
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        // TODO: do more stuff like moving items into the inventory
        target.setInCombat(true);
        if (target instanceof Hero) {
            ((Hero) target).sendMessage("" + ChatColor.GRAY + ChatColor.ITALIC + "Du hast den Kampf betreten.");
        }
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        // TODO: do more stuff like moving items into the inventory
        target.setInCombat(false);
        if (target instanceof Hero) {
            ((Hero) target).sendMessage("" + ChatColor.GRAY + ChatColor.ITALIC + "Du hast den Kampf verlassen.");
        }
    }

    @Override
    protected void renew(CharacterTemplate target) {

        // silently set in combat again
        target.setInCombat(true);
    }
}
