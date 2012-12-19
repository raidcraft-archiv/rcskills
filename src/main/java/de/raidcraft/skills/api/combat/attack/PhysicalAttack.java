package de.raidcraft.skills.api.combat.attack;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.callback.Callback;
import de.raidcraft.skills.api.combat.callback.RangedCallback;
import de.raidcraft.skills.api.exceptions.CombatException;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * This attack is issued by a skill or the damage event itself.
 *
 * @author Silthus
 */
public class PhysicalAttack extends AbstractAttack<CharacterTemplate, CharacterTemplate> {

    private Callback callback;

    public PhysicalAttack(CharacterTemplate source, CharacterTemplate target, int damage) {

        super(source, target, damage);
    }

    public PhysicalAttack(CharacterTemplate attacker, CharacterTemplate target, Callback callback) {

        this(attacker, target, 0);
        this.callback = callback;
    }

    public PhysicalAttack(CharacterTemplate attacker, CharacterTemplate target, int damage, Callback callback) {

        this(attacker, target, damage);
        this.callback = callback;
    }

    public PhysicalAttack(EntityDamageByEntityEvent event) {

        this(RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter((LivingEntity) event.getDamager()),
                RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter((LivingEntity) event.getEntity()),
                event.getDamage());
    }

    @Override
    public void run() throws CombatException, InvalidTargetException {

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                getSource().getEntity(),
                getTarget().getEntity(),
                EntityDamageEvent.DamageCause.CUSTOM,
                0);
        if (!event.isCancelled()) {
            // TODO: add fancy resitence checks and so on
            getTarget().damage(this);
            // if no exceptions was thrown to this point issue the callback
            if (callback != null && !(callback instanceof RangedCallback)) {
                callback.run(getTarget());
            }
        }
    }
}
