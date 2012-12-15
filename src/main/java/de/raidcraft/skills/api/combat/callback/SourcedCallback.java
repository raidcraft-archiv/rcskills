package de.raidcraft.skills.api.combat.callback;

import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public class SourcedCallback {

    private final LivingEntity source;
    private final Callback callback;

    protected SourcedCallback(LivingEntity source, Callback callback) {

        this.source = source;
        this.callback = callback;
    }

    public LivingEntity getSource() {

        return source;
    }

    public Callback getCallback() {

        return callback;
    }
}
