package de.raidcraft.skills.api.effect.common;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.combat.callback.Callback;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.PlayerInteractTrigger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.Action;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Queued Interact",
        description = "Löst den gegebenen Skill bei einem Interact aus.",
        types = {EffectType.SYSTEM},
        global = true
)
public class QueuedInteract extends ExpirableEffect<Skill> implements Triggered {

    private Callback<PlayerInteractTrigger> callback;
    private Action action;
    private boolean triggered = false;
    private String activateMessage;
    private String deactivateMessage;

    public QueuedInteract(Skill source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        if (duration == 0) duration = 20 * 5;
    }

    @Override
    public void load(ConfigurationSection data) {

        this.activateMessage = data.getString("activate-message");
        this.deactivateMessage = data.getString("deactivate-message");
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        info(activateMessage);
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        if (!triggered) {
            info(deactivateMessage);
        }
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

    }

    public void sendInfo(String msg) {

        info(msg);
    }

    public void addCallback(Callback<PlayerInteractTrigger> callback) {

        addCallback(callback, null);
    }

    public void addCallback(Callback<PlayerInteractTrigger> callback, Action action) {

        this.callback = callback;
        this.action = action;
    }

    @TriggerHandler
    public void onInteract(PlayerInteractTrigger trigger) throws CombatException {

        if (action != null && trigger.getEvent().getAction() != action) {
            // dont handle events we dont want
            return;
        }
        // lets substract the usage cost if the skill is marked as a queued attack
        if (getSource().getSkillProperties().getInformation().queuedAttack()) {
            getSource().substractUsageCost(new SkillAction(getSource()));
        }
        if (callback != null) {
            callback.run(trigger);
        }
        triggered = true;
        remove();
    }
}
