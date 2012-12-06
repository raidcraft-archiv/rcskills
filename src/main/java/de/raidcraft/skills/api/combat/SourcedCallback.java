package de.raidcraft.skills.api.combat;

import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public class SourcedCallback {

    private final LivingEntity source;
    private final LivingEntity target;
    private final Callback callback;

    protected SourcedCallback(LivingEntity source, LivingEntity target, Callback callback) {

        this.source = source;
        this.target = target;
        this.callback = callback;
    }

    public LivingEntity getSource() {

        return source;
    }

    public LivingEntity getTarget() {

        return target;
    }

    public Callback getCallback() {

        return callback;
    }

    public void run() {

        callback.run(target);
    }
}
