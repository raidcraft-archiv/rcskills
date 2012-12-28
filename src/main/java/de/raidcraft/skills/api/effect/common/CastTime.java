package de.raidcraft.skills.api.effect.common;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.CastCommandAction;
import de.raidcraft.skills.api.effect.DelayedEffect;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.util.TimeUtil;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Casttime",
        description = "Keeps track of the casttime for a char template",
        priority = -1.0
)
public class CastTime extends DelayedEffect<CastCommandAction> {

    private boolean casted = false;

    public CastTime(CastCommandAction source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        setPriority(-1.0);
        delay = source.getSkill().getTotalCastTime();
        debug("started timer");
    }

    @Override
    public void apply() throws CombatException {

        super.apply();
        getSource().getSource().sendMessage("" + ChatColor.GRAY + ChatColor.ITALIC +
                "Wirke Zauber " + ChatColor.AQUA + getSource().getSkill().getFriendlyName()
                + ChatColor.GRAY + " in " + TimeUtil.ticksToSeconds(getDelay()) + "s");
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        casted = true;
        remove();
        getSource().run();
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

        // nothing we need to do here
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        if (!casted) {
            warn(getSource().getSource(), "Zauber " + getSource().getSkill().getFriendlyName() + " wurde unterbrochen.");
        }
    }
}
