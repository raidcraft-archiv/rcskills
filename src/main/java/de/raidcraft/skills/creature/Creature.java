package de.raidcraft.skills.creature;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.AbstractCharacterTemplate;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Wolf;

/**
 * @author Silthus
 */
public class Creature extends AbstractCharacterTemplate {

    public Creature(LivingEntity entity) {

        super(entity);
    }

    @Override
    public void damage(Attack attack) {

        super.damage(attack);
        if (attack.getSource() instanceof CharacterTemplate && attack.getTarget() instanceof CharacterTemplate) {
            LivingEntity attacker = ((CharacterTemplate) attack.getSource()).getEntity();
            LivingEntity target = ((CharacterTemplate) attack.getTarget()).getEntity();
            // make the creature angry if it is attacked
            if (target instanceof Wolf) {
                Wolf wolf = (Wolf) target;
                wolf.setAngry(true);
                wolf.setTarget(attacker);
            } else if (target instanceof PigZombie) {
                PigZombie pigZombie = (PigZombie) target;
                pigZombie.setAngry(true);
                pigZombie.setTarget(attacker);
            } else if (target instanceof org.bukkit.entity.Creature) {
                ((org.bukkit.entity.Creature) target).setTarget(attacker);
            }
        }
    }

    @Override
    public int getDefaultHealth() {

        return RaidCraft.getComponent(SkillsPlugin.class).getDamageManager().getCreatureHealth(getEntity().getType());
    }

    @Override
    public boolean isFriendly(Hero source) {

        // TODO: implement friendly check for summoned creatures
        return false;
    }
}
