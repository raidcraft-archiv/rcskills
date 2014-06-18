package de.raidcraft.skills.api.combat.callback;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.CombatManager;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.RangedAttack;
import org.bukkit.entity.Projectile;

/**
 * @author Silthus
 */
public class SourcedRangeCallback<T extends ProjectileCallback> {

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

    public RangedAttack<T> getAttack() {

        return attack;
    }

    public double getDamage() {

        return attack.getDamage();
    }

    @SuppressWarnings("unchecked")
    public void queueCallback() {

        CombatManager combatManager = RaidCraft.getComponent(SkillsPlugin.class).getCombatManager();
        if (getCallback() instanceof RangedCallback) {
            combatManager.queueEntityCallback((SourcedRangeCallback<RangedCallback>) this);
        } else if (getCallback() instanceof LocationCallback) {
            combatManager.queueLocationCallback((SourcedRangeCallback<LocationCallback>) this);
        } else {
            combatManager.queueRangedAttack(this);
        }
    }

    public T getCallback() {

        return attack.getCallback();
    }
}
