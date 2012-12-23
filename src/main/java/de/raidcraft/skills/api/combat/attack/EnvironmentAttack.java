package de.raidcraft.skills.api.combat.attack;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * @author Silthus
 */
public class EnvironmentAttack extends AbstractAttack<EntityDamageByEntityEvent.DamageCause, CharacterTemplate> {

    public EnvironmentAttack(EntityDamageByEntityEvent event, double damageModifier) {

        super(event.getCause(),
                RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter((LivingEntity) event.getEntity()),
                (int) (event.getDamage() * damageModifier));
    }

    @Override
    public void run() throws CombatException {

        // TODO: check resistance and the fancy stuff
        getTarget().damage(this);
    }
}
