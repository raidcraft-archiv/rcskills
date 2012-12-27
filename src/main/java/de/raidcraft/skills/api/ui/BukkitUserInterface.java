package de.raidcraft.skills.api.ui;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.util.HeroUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class BukkitUserInterface implements UserInterface {

    private final Hero hero;
    private final Player player;
    private long lastUpdate = 0L;
    private boolean enabled = true;
    private boolean maxedMana = false;

    public BukkitUserInterface(Hero hero) {

        this.hero = hero;
        this.player = hero.getPlayer();
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
    public void refresh() {

        if (player == null || !isEnabled() || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        // set the players health bar to a percentage of his actual health
        int health = (int) Math.ceil(((double)hero.getHealth() / hero.getMaxHealth()) * player.getMaxHealth());
        if (health > 20) health = 20;
        if (health < 0) health = 0;
        player.setHealth(health);

        // set the stamina bar to a percentage of the actual stamina
        int stamina = (int) Math.ceil(((double)hero.getStamina() / hero.getMaxStamina()) * 20);
        player.setFoodLevel(stamina);
        // see the minecraft wiki for the mechanics: http://www.minecraftwiki.net/wiki/Hunger#Mechanics
        player.setSaturation(20.0F);
        player.setExhaustion(0.0F);

        Profession prof = hero.getSelectedProfession();
        if (prof != null) {
            // lets set the experience bar to the level of the player
            player.setLevel(prof.getLevel().getLevel());
            // setExp() - This is a percentage value. 0 is "no progress" and 1 is "next level".
            float exp = ((float) prof.getLevel().getExp()) / ((float) prof.getLevel().getMaxExp());
            player.setExp(exp);
        } else {
            // lets set the level to 0
            player.setExp(0F);
            player.setLevel(0);
        }

        if (hero.getMana() < hero.getMaxMana()) maxedMana = false;
        // set the manabar if it changed
        long time = System.currentTimeMillis();
        if (time - RaidCraft.getComponent(SkillsPlugin.class).getCommonConfig().interface_mana_bar_update > lastUpdate) {
            if (hero.getMana() < hero.getMaxMana() || !maxedMana) {
                hero.sendMessage(ChatColor.BLUE + "Mana: " + HeroUtil.createManaBar(hero.getMana(), hero.getMaxMana()));
                this.lastUpdate = time;
                if (hero.getMana() == hero.getMaxMana()) maxedMana = true;
            }
        }
    }
}
