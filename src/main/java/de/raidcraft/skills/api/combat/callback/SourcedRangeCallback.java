package de.raidcraft.skills.api.combat.callback;

import de.raidcraft.skills.api.character.CharacterTemplate;
import org.bukkit.entity.Projectile;

/**
 * @author Silthus
 */
public class SourcedRangeCallback {

    private final CharacterTemplate source;
    private final Projectile projectile;
    private final RangedCallback callback;
    private int taskId = -1;

    public SourcedRangeCallback(CharacterTemplate source, Projectile projectile, RangedCallback callback) {

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

    public RangedCallback getCallback() {

        return callback;
    }
}
