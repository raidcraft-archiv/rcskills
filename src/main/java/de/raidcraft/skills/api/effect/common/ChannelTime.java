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
        priority = -1.0,
        global = true
)
public class ChannelTime extends PeriodicExpirableEffect<SkillAction> {

    private final float lossPerTick;
    private boolean casted = false;
    private boolean isPlayer = false;
    private Player player;
    private List<AmbientEffect> ambientEffects;

    public ChannelTime(SkillAction source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        setPriority(-1.0);
        duration = TimeUtil.secondsToMillis(source.getCastTime());
        interval = 1;
        lossPerTick = 1.0F / duration;
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
            player.setExp(1.0F - lossPerTick);
        }
        ambientEffects = getSource().getSkill().getAmbientEffects(AbilityEffectStage.CASTING);
    }

    @Override
    protected void tick(CharacterTemplate target) throws CombatException {

        if (isPlayer) {
            float newExp = player.getExp() - lossPerTick;
            // 10% marging to call it a complete channelling success
            if (newExp < 0.10F) {
                casted = true;
            }
            if (newExp < 0.0F) {
                return;
            }
            player.setExp(newExp);
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
            warn(getSource().getSource(), "Kanalisierungs Zauber " + getSource().getSkill().getFriendlyName() + " wurde unterbrochen.");
        } else {
            getSource().run();
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
