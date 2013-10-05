package de.raidcraft.skills.effects.disabling;

import de.raidcraft.skills.api.ability.Ability;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.PeriodicExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.TriggerPriority;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.BlockBreakTrigger;
import de.raidcraft.skills.trigger.DamageTrigger;
import de.raidcraft.skills.trigger.PlayerCastSkillTrigger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Web",
        description = "Webs the target to the ground making it unable to move.",
        types = {EffectType.DISABLEING, EffectType.DEBUFF, EffectType.HARMFUL, EffectType.MOVEMENT}
)
public class Web extends PeriodicExpirableEffect<Ability> implements Triggered {

    private boolean abortSkillCast = true;
    private boolean abortDestruction = true;
    private boolean abortKnockback = true;
    private Block web;

    public Web(Ability source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        interval = 5;
    }

    @Override
    public void load(ConfigurationSection data) {

        abortSkillCast = data.getBoolean("abort-skillcast", true);
        abortDestruction = data.getBoolean("abort-destruction", true);
        abortKnockback = data.getBoolean("abort-knockback", true);
    }

    @TriggerHandler(ignoreCancelled = true, priority = TriggerPriority.LOWEST, filterTargets = false)
    public void onBlockDestroy(BlockBreakTrigger trigger) throws CombatException {

        if (!abortDestruction) {
            return;
        }
        if (trigger.getEvent().getBlock().equals(web)) {
            trigger.getEvent().setCancelled(true);
            throw new CombatException("Du versuchst dich vergeblich aus dem Netz zu befreien.");
        }
    }

    @TriggerHandler(ignoreCancelled = true)
    public void onDamage(DamageTrigger trigger) {

        if (!abortKnockback) {
            return;
        }
        trigger.getAttack().setKnockback(false);
    }

    @TriggerHandler(ignoreCancelled = true)
    public void onCastTrigger(PlayerCastSkillTrigger trigger) throws CombatException {

        if (!abortSkillCast) {
            return;
        }
        if (trigger.getSkill().isOfType(EffectType.MOVEMENT) && trigger.getSkill().isOfType(EffectType.HELPFUL)) {
            trigger.setCancelled(true);
            throw new CombatException("Du versucht dich vergeblich mit Zaubern aus dem Netz zu befreien.");
        }
    }

    @Override
    protected void tick(CharacterTemplate target) throws CombatException {

        Location location = web.getLocation();
        location.setPitch(target.getEntity().getLocation().getPitch());
        location.setYaw(target.getEntity().getLocation().getYaw());
        target.getEntity().teleport(location);
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        renew(target);
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        web.setType(Material.AIR);
        target.getEntity().removePotionEffect(PotionEffectType.JUMP);
        target.getEntity().removePotionEffect(PotionEffectType.SLOW);
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

        web = target.getEntity().getLocation().getBlock();
        while (web.getType() == Material.AIR) {
            web = web.getRelative(BlockFace.DOWN);
        }
        web = web.getRelative(BlockFace.UP);
        web.setType(Material.WEB);
    }
}
