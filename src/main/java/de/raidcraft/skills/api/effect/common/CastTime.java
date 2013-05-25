package de.raidcraft.skills.api.effect.common;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.ambient.AmbientEffect;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.PeriodicExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.AbilityEffectStage;
import de.raidcraft.skills.util.TimeUtil;
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
    private Player player;
    private List<AmbientEffect> ambientEffects;

    public CastTime(SkillAction source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        setPriority(-1.0);
        duration = source.getCastTime();
        interval = 1;
        fillPerTick = 1.0F / delay;
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        getSource().getSource().sendMessage("" + ChatColor.GRAY + ChatColor.ITALIC +
                "Wirke Zauber " + ChatColor.AQUA + getSource().getSkill().getFriendlyName()
                + ChatColor.GRAY + " in " + TimeUtil.ticksToSeconds(getDuration()) + "s");

        isPlayer = getTarget().getEntity() instanceof Player;
        if (isPlayer) {
            player = (Player) getTarget().getEntity();
            RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().pausePlayerExpUpdate(player);
            nullExp();
        }
        ambientEffects = getSource().getSkill().getAmbientEffects(AbilityEffectStage.CASTING);
    }

    @Override
    protected void tick(CharacterTemplate target) throws CombatException {

        if (isPlayer) {
            if (player.getExp() >= 1.0F) {
                casted = true;
                return;
            }
            player.setExp(player.getExp() + fillPerTick);
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
            nullExp();
            RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().unpausePlayerExpUpdate(player);
        }
        if (!casted) {
            warn(getSource().getSource(), "Zauber " + getSource().getSkill().getFriendlyName() + " wurde unterbrochen.");
        }
    }

    private void nullExp() {

        if (isPlayer) {
            player.setExp(0.0F);
            player.setLevel(0);
            player.setTotalExperience(0);
        }
    }
}
