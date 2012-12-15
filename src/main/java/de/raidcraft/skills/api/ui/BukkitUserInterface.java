package de.raidcraft.skills.api.ui;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.util.HeroUtil;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class BukkitUserInterface implements UserInterface {

    private final Hero hero;
    private final Player player;
    private long lastUpdate = 0L;
    private boolean enabled = true;

    public BukkitUserInterface(Hero hero) {

        this.hero = hero;
        this.player = hero.getBukkitPlayer();
    }

    @Override
    public Hero getHero() {

        return hero;
    }

    @Override
    public boolean isEnabled() {

        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {

        this.enabled = enabled;
    }

    @Override
    public void run() {

        if (player == null || !isEnabled()) {
            return;
        }

        // set the players health bar to a percentage of his actual health
        int health = (int) Math.ceil((hero.getHealth() / hero.getMaxHealth()) * player.getMaxHealth());
        player.setHealth(health);

        // set the stamina bar to a percentage of the actual stamina
        int stamina = (int) Math.ceil((hero.getStamina() / hero.getMaxStamina()) * 20);
        player.setFoodLevel(stamina);
        // see the minecraft wiki for the mechanics: http://www.minecraftwiki.net/wiki/Hunger#Mechanics
        player.setSaturation(20.0F);
        player.setExhaustion(0.0F);

        // set the manabar if it changed
        long time = System.currentTimeMillis();
        if (time < lastUpdate + RaidCraft.getComponent(SkillsPlugin.class).getCommonConfig().manabar_update_interval) {
            if (hero.getMana() < hero.getMaxMana()) {
                hero.sendMessage(HeroUtil.createManaBar(hero.getMana(), hero.getMaxMana()));
                this.lastUpdate = time;
            }
        }
    }
}
