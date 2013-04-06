package de.raidcraft.skills.api.combat;

import de.raidcraft.skills.api.combat.action.MagicalAttack;
import de.raidcraft.util.EffectUtil;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * @author Silthus
 */
public enum MagicalAttackType {

    SMOKE(Effect.SMOKE),
    FIRE(Effect.MOBSPAWNER_FLAMES);

    private final Effect effect;

    private MagicalAttackType(Effect effect) {

        this.effect = effect;
    }

    public Effect getEffect() {

        return effect;
    }

    public void run(MagicalAttack attack) {

        LivingEntity attacker = attack.getSource().getEntity();
        List<Block> lineOfSight = attacker.getLineOfSight(null, 100);
        EffectUtil.playSound(attacker.getLocation(), Sound.GHAST_FIREBALL, 5F, 1F);
        for (Block block : lineOfSight) {
            EffectUtil.playEffect(block.getLocation(), effect, 1);
            EffectUtil.playEffect(block.getLocation(), effect, 1);
            EffectUtil.playEffect(block.getLocation(), effect, 1);
            EffectUtil.playEffect(block.getLocation(), effect, 1);
            EffectUtil.playEffect(block.getLocation(), effect, 1);
        }
    }
}
