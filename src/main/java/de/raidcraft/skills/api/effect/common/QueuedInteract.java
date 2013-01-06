package de.raidcraft.skills.api.effect.common;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.callback.Callback;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.PlayerInteractTrigger;
import org.bukkit.event.block.Action;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "QueuedInteract",
        description = "Löst den gegebenen Skill bei einem Interact aus."
)
public class QueuedInteract extends ExpirableEffect<Skill> implements Triggered {

    private Callback<PlayerInteractTrigger> callback;
    private Action action;
    private boolean triggered = false;

    public QueuedInteract(Skill source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        if (duration == 0) duration = 20 * 5;
    }

    public void sendInfo(String msg) {

        info(msg);
    }

    public void addCallback(Callback<PlayerInteractTrigger> callback, Action action) {

        this.callback = callback;
        this.action = action;
    }

    public void addCallback(Callback<PlayerInteractTrigger> callback) {

        addCallback(callback, null);
    }

    @TriggerHandler
    public void onInteract(PlayerInteractTrigger trigger) throws CombatException {

        if (action != null && trigger.getEvent().getAction() != action) {
            // dont handle events we dont want
            return;
        }
        if (callback != null) {
            callback.run(trigger);
        }
        triggered = true;
        remove();
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        if (!triggered) {
            info("Du senkst deine Arme.");
        }
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

    }
}
