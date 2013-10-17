package de.raidcraft.skills.creature;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.AbstractCharacterTemplate;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.util.EntityUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public class Creature extends AbstractCharacterTemplate {

    public Creature(LivingEntity entity) {

        super(entity);
        attachLevel(new CreatureAttachedLevel<CharacterTemplate>(this, 1));
    }

    @Override
    public void setHealth(double health) {

        super.setHealth(health);
        updateHealthBar();
    }

    @Override
    public void setMaxHealth(double maxHealth) {

        super.setMaxHealth(maxHealth);
        updateHealthBar();
    }

    private void updateHealthBar() {

        getEntity().setCustomName(EntityUtil.drawHealthBar(getHealth(), getMaxHealth(), ChatColor.GREEN));
    }

    @Override
    public void setInCombat(boolean inCombat) {

        super.setInCombat(inCombat);
        if (!usingHealthBar) {
            return;
        }
        getEntity().setCustomNameVisible(inCombat);
        updateHealthBar();
    }

    @Override
    public double getDefaultHealth() {

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
