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

import java.util.HashSet;
import java.util.Set;

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
    private Set<Block> blocks = new HashSet<>();

    public Web(Ability source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
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
        if (blocks.contains(trigger.getEvent().getBlock())) {
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
    protected void apply(CharacterTemplate target) throws CombatException {

        renew(target);
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        for (Block block : blocks) {
            block.setType(Material.AIR);
        }
    }

    @Override
    protected void tick(CharacterTemplate target) throws CombatException {

        renew(target);
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

        setWebBlock(target.getEntity().getLocation());
    }

    private void setWebBlock(Location location) {

        Block web = location.getBlock();
        while (web.getType() == Material.AIR) {
            web = web.getRelative(BlockFace.DOWN);
        }
        web = web.getRelative(BlockFace.UP);
        web.setType(Material.WEB);
        blocks.add(web);
    }
}
