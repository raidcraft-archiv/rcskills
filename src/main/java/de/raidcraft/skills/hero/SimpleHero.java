package de.raidcraft.skills.hero;

import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.AbstractHero;
import de.raidcraft.skills.api.persistance.HeroData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class SimpleHero extends AbstractHero {

    public SimpleHero(Player player, HeroData data) {

        super(player, data);
        try {
            checkArmor();
            checkWeapons();
        } catch (CombatException e) {
            sendMessage(ChatColor.RED + e.getMessage());
        }
    }
}
