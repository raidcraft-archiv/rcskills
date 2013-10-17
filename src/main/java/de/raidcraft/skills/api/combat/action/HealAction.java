package de.raidcraft.skills.api.combat.action;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.trigger.HealTrigger;

/**
 * @author Silthus
 */
public class HealAction<S> extends AbstractTargetedAction<S, CharacterTemplate> {

    private double amount;

    public HealAction(S source, CharacterTemplate target, double amount) {

        super(source, target);
        this.amount = amount;
    }

    @Override
    public double getThreat() {

        return getAmount() * 0.5;
    }

    public double getAmount() {

        return amount;
    }

    public void setAmount(double amount) {

        this.amount = amount;
    }

    @Override
    public void run() throws CombatException {

        HealTrigger trigger = TriggerManager.callTrigger(
                new HealTrigger(this, amount)
        );

        if (trigger.isCancelled()) {
            setCancelled(true);
            throw new CombatException("Ziel kann nicht geheilt werden!");
        }

        amount = trigger.getAmount();
        if (amount < 1) {
            return;
        }

        getTarget().heal(this);
    }
}
