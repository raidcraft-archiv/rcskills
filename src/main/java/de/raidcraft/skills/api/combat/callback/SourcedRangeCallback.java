package de.raidcraft.skills.api.combat.callback;

import de.raidcraft.skills.api.character.CharacterTemplate;
import org.bukkit.entity.Projectile;

/**
 * @author Silthus
 */
public class SourcedRangeCallback<T> {

    private final CharacterTemplate source;
    private final Projectile projectile;
    private final ProjectileCallback<T> callback;
    private int taskId = -1;

    public SourcedRangeCallback(CharacterTemplate source, Projectile projectile, ProjectileCallback<T> callback) {

        this.source = source;
        this.projectile = projectile;
        this.callback = callback;
    }

    public int getTaskId() {

        return taskId;
    }

    public void setTaskId(int taskId) {

        this.taskId = taskId;
    }

    public CharacterTemplate getSource() {

        return source;
    }

    public Projectile getProjectile() {

        return projectile;
    }

    public ProjectileCallback<T> getCallback() {

        return callback;
    }
}
