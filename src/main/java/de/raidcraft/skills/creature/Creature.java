package de.raidcraft.skills.creature;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.AbstractCharacterTemplate;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.attack.Attack;
import net.minecraft.server.v1_4_5.EntityLiving;
import org.bukkit.EntityEffect;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_4_5.entity.CraftLivingEntity;
import org.bukkit.entity.*;

import java.lang.reflect.Field;
import java.util.Random;

/**
 * @author Silthus
 */
public class Creature extends AbstractCharacterTemplate {

    private static final Random RANDOM = new Random();
    private Field nmsHealth = null;

    public Creature(LivingEntity entity) {

        super(entity);
        try {
            // make the health field in NMS accessible
            this.nmsHealth = EntityLiving.class.getDeclaredField("health");
            this.nmsHealth.setAccessible(true);
        } catch (NoSuchFieldException ignored) { }
    }

    @Override
    public void kill() {

        super.kill();
        // play the death sound
        /*getEntity().getWorld().playSound(
                getEntity().getLocation(),
                getDeathSound(getEntity().getType()),
                1.0F,
                getSoundStrength(getEntity())
        );
        */
        // play the death effect
        getEntity().playEffect(EntityEffect.DEATH);
    }

    @Override
    public void damage(Attack attack) {

        super.damage(attack);
        if (attack.getSource() instanceof CharacterTemplate && attack.getTarget() instanceof CharacterTemplate) {
            LivingEntity attacker = ((CharacterTemplate) attack.getSource()).getEntity();
            LivingEntity target = ((CharacterTemplate) attack.getTarget()).getEntity();
            // make the creature angry if it is attacked
            if (target instanceof Wolf) {
                Wolf wolf = (Wolf) target;
                wolf.setAngry(true);
                wolf.setTarget(attacker);
            } else if (target instanceof PigZombie) {
                PigZombie pigZombie = (PigZombie) target;
                pigZombie.setAngry(true);
                pigZombie.setTarget(attacker);
            } else if (target instanceof org.bukkit.entity.Creature) {
                ((org.bukkit.entity.Creature) target).setTarget(attacker);
            }
        }
    }

    @Override
    public int getHealth() {

        return getEntity().getHealth();
    }

    @Override
    public void setHealth(int health) {

        if (health < getMaxHealth()) {
            health = 0;
        }
        try {
            nmsHealth.setInt(((CraftLivingEntity) getEntity()).getHandle(), health);
        } catch (IllegalAccessException e) {
            getEntity().setHealth(health);
            e.printStackTrace();
        }
    }

    @Override
    public int getMaxHealth() {

        return RaidCraft.getComponent(SkillsPlugin.class).getDamageManager().getCreatureHealth(getEntity().getType());
    }

    private float getSoundStrength(LivingEntity target) {

        if (!(target instanceof Ageable)) {
            return 1.0F;
        }
        if (((Ageable) target).isAdult()) {
            return (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2F + 1.0F;
        } else {
            return (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2F + 1.5F;
        }
    }

    private Sound getDeathSound(EntityType type) {

        switch (type) {

            case COW:
                return Sound.COW_IDLE;
            case BLAZE:
                return Sound.BLAZE_DEATH;
            case CHICKEN:
                return Sound.CHICKEN_HURT;
            case CREEPER:
                return Sound.CREEPER_DEATH;
            case SKELETON:
                return Sound.SKELETON_DEATH;
            case IRON_GOLEM:
                return Sound.IRONGOLEM_DEATH;
            case GHAST:
                return Sound.GHAST_DEATH;
            case PIG:
                return Sound.PIG_DEATH;
            case OCELOT:
                return Sound.CAT_HIT;
            case SHEEP:
                return Sound.SHEEP_IDLE;
            case SPIDER:
            case CAVE_SPIDER:
                return Sound.SPIDER_DEATH;
            case WOLF:
                return Sound.WOLF_DEATH;
            case ZOMBIE:
                return Sound.ZOMBIE_DEATH;
            case PIG_ZOMBIE:
                return Sound.ZOMBIE_PIG_DEATH;
            default:
                return Sound.HURT_FLESH;
        }
    }
}
