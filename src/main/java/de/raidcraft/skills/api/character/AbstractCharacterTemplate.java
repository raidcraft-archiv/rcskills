package de.raidcraft.skills.api.character;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.combat.Effect;
import net.minecraft.server.EntityLiving;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public abstract class AbstractCharacterTemplate implements CharacterTemplate {

    private final String name;
    private final LivingEntity entity;
    private final Map<String, Effect> effects = new HashMap<>();
    private Field nmsHealth = null;
    private boolean inCombat = false;

    public AbstractCharacterTemplate(LivingEntity entity) {

        this.entity = entity;
        this.name = (entity instanceof Player) ? ((Player) entity).getName() : entity.getType().getName();
        try {
            // make the health field in NMS accessible
            this.nmsHealth = EntityLiving.class.getDeclaredField("health");
            this.nmsHealth.setAccessible(true);
        } catch (NoSuchFieldException ignored) { }
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public LivingEntity getEntity() {

        return entity;
    }

    @Override
    public int getHealth() {

        return entity.getHealth();
    }

    @Override
    public void setHealth(int health) {

        if (health < getMaxHealth()) {
            health = 0;
        }
        try {
            nmsHealth.setInt(((CraftLivingEntity) entity).getHandle(), health);
        } catch (IllegalAccessException e) {
            entity.setHealth(health);
            e.printStackTrace();
        }
    }

    @Override
    public int getMaxHealth() {

        return RaidCraft.getComponent(SkillsPlugin.class).getDamageManager().getCreatureHealth(entity.getType());
    }

    @Override
    public void damage(int damage) {

        setHealth(getHealth() - damage);
    }

    @Override
    public void addEffect(Effect effect) {

        // TODO: add priority checks
        effects.put(effect.getName().toLowerCase(), effect);
    }

    @Override
    public void removeEffect(Effect effect) {

        effects.remove(effect.getName().toLowerCase());
    }

    @Override
    public void clearEffects() {

        effects.clear();
    }

    @Override
    public boolean hasEffect(Effect effect) {

        return effects.containsKey(effect.getName().toLowerCase());
    }

    @Override
    public boolean hasEffectType(Effect.Type type) {

        for (Effect effect : effects.values()) {
            if (effect.hasType(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInCombat() {

        return inCombat;
    }

    @Override
    public void setInCombat(boolean inCombat) {

        this.inCombat = inCombat;
    }
}
