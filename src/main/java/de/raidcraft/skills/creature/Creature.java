package de.raidcraft.skills.creature;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.AbstractCharacterTemplate;
import net.minecraft.server.EntityLiving;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

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
}
