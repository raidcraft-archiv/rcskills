package de.raidcraft.skills.api.combat.callback;

import de.raidcraft.skills.api.character.CharacterTemplate;

/**
 * @author Silthus
 */
public class SourcedCallback {

    private final CharacterTemplate source;
    private final Callback callback;
    private int taskId = -1;

    public SourcedCallback(CharacterTemplate source, Callback callback) {

        this.source = source;
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

    public Callback getCallback() {

        return callback;
    }
}
