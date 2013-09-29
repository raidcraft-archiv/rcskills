package de.raidcraft.skills.api.effect.common;

import de.raidcraft.api.ambient.AmbientEffect;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.PeriodicExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.AbilityEffectStage;
import de.raidcraft.util.FakeWither;
import de.raidcraft.util.TimeUtil;
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
    private FakeWither castBar;

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
                + ChatColor.AQUA + getSource().getSkill().getFriendlyName()
                + ChatColor.GRAY + " in " + TimeUtil.ticksToSeconds(getDuration()) + "s";

        isPlayer = getTarget().getEntity() instanceof Player;
        if (isPlayer) {
            castBar = new FakeWither(target.getEntity().getLocation().add(0, -1, 0));
            castBar.setCustomName(msg);
            castBar.setVisible(false);
            castBar.setHealth(fillPerTick);
            castBar.create();
            castBar.move(0, -1, 0);
        }
        ambientEffects = getSource().getSkill().getAmbientEffects(AbilityEffectStage.CASTING);
    }

    @Override
    protected void tick(CharacterTemplate target) throws CombatException {

        if (isPlayer && castBar != null) {
            // when the spell is cast above 90% it is consired success
            if (castBar.getHealth() > 0.9) {
                casted = true;
            }
            float newStatus = castBar.getHealth() + fillPerTick;
            if (newStatus > 1.0F) {
                return;
            }
            castBar.setHealth(newStatus);
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

        if (isPlayer && castBar != null) {
            castBar.destroy();
        }
        if (!casted) {
            warn(getSource().getSource(), "Zauber " + getSource().getSkill().getFriendlyName() + " wurde unterbrochen.");
        } else {
            getSource().run();
        }
    }
}
