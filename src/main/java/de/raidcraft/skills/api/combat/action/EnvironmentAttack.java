package de.raidcraft.skills.api.combat.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.AttackType;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.trigger.DamageTrigger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * @author Silthus
 */
public class EnvironmentAttack extends AbstractAttack<EntityDamageByEntityEvent.DamageCause, CharacterTemplate> {

    private final EntityDamageByEntityEvent event;

    public EnvironmentAttack(EntityDamageByEntityEvent event, int damage) {

        super(event.getCause(),
                RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter((LivingEntity) event.getEntity()),
                damage,
                AttackType.fromEvent(event.getCause()));
        this.event = event;
    }

    @Override
    public void run() throws CombatException {

        // TODO: set damage based on source and config

        // lets run the triggers first to give the skills a chance to cancel the attack or do what not
        if (getTarget() instanceof Hero) {
            TriggerManager.callTrigger(new DamageTrigger((Hero) getTarget(), this));
        }
        if (isCancelled()) {
            setCancelled(true);
            throw new CombatException(CombatException.Type.CANCELLED);
        }
        // TODO: check resistance and the fancy stuff
        getTarget().damage(this);
    }
}
