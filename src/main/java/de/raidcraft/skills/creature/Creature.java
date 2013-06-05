package de.raidcraft.skills.creature;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.AbstractCharacterTemplate;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.Attack;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Wolf;

/**
 * @author Silthus
 */
public class Creature extends AbstractCharacterTemplate {

    private static final char HEALTH_BAR_MAIN_SYMBOL = '█';
    private static final char HEALTH_BAR_HALF_SYMBOL = '▌';
    private static final int HEALTH_BAR_LENGTH = 5;

    private CharacterTemplate highestThread;

    public Creature(LivingEntity entity) {

        super(entity);
        attachLevel(new CreatureAttachedLevel<CharacterTemplate>(this, 60));
    }

    public CharacterTemplate getHighestThread() {

        return highestThread;
    }

    public void setHighestThread(CharacterTemplate highestThread) {

        this.highestThread = highestThread;
    }

    @Override
    public void setHealth(int health) {

        super.setHealth(health);
        updateHealthBar();
    }

    @Override
    public void setMaxHealth(int maxHealth) {

        super.setMaxHealth(maxHealth);
        updateHealthBar();
    }

    private void updateHealthBar() {

        ChatColor barColor = ChatColor.GREEN;
        double healthInPercent = getHealth() / (double) getMaxHealth();
        if (healthInPercent < 0.20) {
            barColor = ChatColor.DARK_RED;
        } else if (healthInPercent < 0.35) {
            barColor = ChatColor.RED;
        } else if (healthInPercent < 0.50) {
            barColor = ChatColor.GOLD;
        } else if (healthInPercent < 0.75) {
            barColor = ChatColor.YELLOW;
        } else if (healthInPercent < 0.90) {
            barColor = ChatColor.DARK_GREEN;
        }
        String health = ChatColor.BLACK + "[" + barColor + getHealth() + ChatColor.BLACK
                + "/" + ChatColor.GREEN + getMaxHealth() + ChatColor.BLACK + "]";
        if (!usingHealthBar) {
            getEntity().setCustomName(health + ChatColor.RED + getName());
        } else {
            StringBuilder healthBar = new StringBuilder(health).append(barColor);
            int count = (int) (healthInPercent * HEALTH_BAR_LENGTH);
            double modulo = (healthInPercent * (HEALTH_BAR_LENGTH * 10)) % 10;
            boolean appendHalfBar = modulo < 6 && modulo > 0;
            for (int i = 0; i < count; i++) {
                if (i == count - 1 && appendHalfBar) {
                    break;
                }
                healthBar.append(HEALTH_BAR_MAIN_SYMBOL);
            }
            if (appendHalfBar) {
                healthBar.append(HEALTH_BAR_HALF_SYMBOL);
            }
            for (int i = 0; i < HEALTH_BAR_LENGTH - count; i++) {
                healthBar.append("  ");
            }

            getEntity().setCustomName(healthBar.toString());
            getEntity().setCustomNameVisible(isInCombat());
        }
    }

    @Override
    public void setInCombat(boolean inCombat) {

        super.setInCombat(inCombat);
        if (!usingHealthBar) {
            return;
        }
        getEntity().setCustomNameVisible(inCombat);
    }

    @Override
    public void damage(Attack attack) {

        super.damage(attack);
        if (attack.getSource() instanceof CharacterTemplate) {
            LivingEntity attacker = ((CharacterTemplate) attack.getSource()).getEntity();
            LivingEntity target = getEntity();
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
}
