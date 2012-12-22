package de.raidcraft.skills.creature;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.AbstractCharacterTemplate;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.attack.Attack;
import net.minecraft.server.v1_4_6.EntityLiving;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Wolf;

import java.lang.reflect.Field;

/**
 * @author Silthus
 */
public class Creature extends AbstractCharacterTemplate {

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
    public final int getHealth() {

        return getEntity().getHealth();
    }

    @Override
    public final void setHealth(int health) {

        if (health < 0) {
            health = 0;
        } else if (health > getMaxHealth()) {
            health = getMaxHealth();
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
}
