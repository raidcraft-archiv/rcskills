package de.raidcraft.skills.creature;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.AbstractCharacterTemplate;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.level.AttachedLevel;
import de.raidcraft.skills.api.level.Levelable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Wolf;

/**
 * @author Silthus
 */
public class Creature extends AbstractCharacterTemplate implements Levelable<Creature> {

    private CharacterTemplate highestThread;
    private AttachedLevel<Creature> attachedLevel;

    public Creature(LivingEntity entity) {

        super(entity);
        // lets calculate the average level based on the players around
/*        int averageLevel = 1;
        try {
            int totalLevel = 0;
            int totalHeroes = 0;
            List<CharacterTemplate> targets = getNearbyTargets(100, false);
            for (CharacterTemplate target : targets) {
                if (target instanceof Hero && ((Hero) target).getPlayer().getGameMode() != GameMode.CREATIVE) {
                    totalHeroes++;
                    totalLevel += ((Hero) target).getAttachedLevel().getLevel();
                }
            }
            if (totalHeroes != 0) {
                averageLevel = totalLevel / totalHeroes;
            }
        } catch (CombatException ignored) {
            // ignored
        }*/
        attachLevel(new CreatureAttachedLevel<>(this, 1));
    }

    public CharacterTemplate getHighestThread() {

        return highestThread;
    }

    public void setHighestThread(CharacterTemplate highestThread) {

        this.highestThread = highestThread;
    }

    public void setAttachedLevel(AttachedLevel<Creature> attachedLevel) {

        this.attachedLevel = attachedLevel;
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
    public AttachedLevel<Creature> getAttachedLevel() {

        return attachedLevel;
    }

    @Override
    public void attachLevel(AttachedLevel<Creature> attachedLevel) {

        this.attachedLevel = attachedLevel;
    }

    @Override
    public int getMaxLevel() {

        return getAttachedLevel().getMaxLevel();
    }

    @Override
    public void onExpGain(int exp) {

    }

    @Override
    public void onExpLoss(int exp) {

    }

    @Override
    public void onLevelGain() {

    }

    @Override
    public void onLevelLoss() {

    }

    @Override
    public boolean isMastered() {

        return true;
    }

    @Override
    public void saveLevelProgress(AttachedLevel<Creature> attachedLevel) {


    }
}
