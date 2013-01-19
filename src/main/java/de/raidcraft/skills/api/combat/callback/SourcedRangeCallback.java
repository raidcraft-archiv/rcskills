package de.raidcraft.skills.api.combat.callback;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.RangedAttack;
import org.bukkit.entity.Projectile;

/**
 * @author Silthus
 */
public class SourcedRangeCallback<T> {

    private final RangedAttack<T> attack;
    private int taskId = -1;

    public SourcedRangeCallback(RangedAttack<T> attack) {

        this.attack = attack;
    }

    public int getTaskId() {

        return taskId;
    }

    public void setTaskId(int taskId) {

        this.taskId = taskId;
    }

    public CharacterTemplate getSource() {

        return attack.getSource();
    }

    public Projectile getProjectile() {

        return attack.getProjectile();
    }

    public ProjectileCallback<T> getCallback() {

        return attack.getCallback();
    }

    public RangedAttack<T> getAttack() {

        return attack;
    }

    public int getDamage() {

        return attack.getDamage();
    }
}
