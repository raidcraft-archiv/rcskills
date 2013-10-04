package de.raidcraft.skills.api.effect.common;

import de.raidcraft.api.ambient.AmbientEffect;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.PeriodicExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.AbilityEffectStage;
import de.raidcraft.util.TimeUtil;
import de.raidcraft.util.bossbar.BarAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Casttime",
        description = "Keeps track of the casttime for a char template",
        priority = -1.0
)
public class CastTime extends PeriodicExpirableEffect<SkillAction> {

    private boolean casted = false;
    private final float fillPerTick;
    private boolean isPlayer = false;
    private List<AmbientEffect> ambientEffects;
    private float filled = 0.0F;

    public CastTime(SkillAction source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        setPriority(-1.0);
        duration = TimeUtil.secondsToTicks(source.getCastTime());
        interval = 1;
        fillPerTick = 1.0F / duration;
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        String msg = ChatColor.ITALIC + "" + ChatColor.RED + target.getName() + ChatColor.GRAY + " zaubert "
                + ChatColor.AQUA + getSource().getAbility().getFriendlyName()
                + ChatColor.GRAY + " in " + TimeUtil.ticksToSeconds(getDuration()) + "s";

        isPlayer = getTarget().getEntity() instanceof Player;
        if (isPlayer) {
            BarAPI.setMessage((Player) getTarget().getEntity(), msg, filled = fillPerTick);
        }
        ambientEffects = getSource().getAbility().getAmbientEffects(AbilityEffectStage.CASTING);
    }

    @Override
    protected void tick(CharacterTemplate target) throws CombatException {

        if (isPlayer) {
            // when the spell is cast above 90% it is consired success
            if (filled > 0.9) {
                casted = true;
            }
            filled += fillPerTick;
            if (filled > 1.0F) {
                filled = 1.0F;
                return;
            }
            BarAPI.setHealth((Player) getTarget().getEntity(), filled);
        }
        for (AmbientEffect effect : ambientEffects) {
            effect.run(getTarget().getEntity().getLocation());
        }
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

        // nothing we need to do here
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        if (isPlayer) {
            BarAPI.removeBar((Player) getTarget().getEntity());
        }
        if (!casted) {
            warn(getSource().getSource(), "Zauber " + getSource().getAbility().getFriendlyName() + " wurde unterbrochen.");
        } else {
            getSource().run();
        }
    }
}
