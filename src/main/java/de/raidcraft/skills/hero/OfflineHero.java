package de.raidcraft.skills.hero;

import de.raidcraft.skills.api.hero.AbstractHero;
import de.raidcraft.skills.api.persistance.HeroData;
import org.bukkit.OfflinePlayer;

/**
 * @author mdoering
 */
public class OfflineHero extends AbstractHero {

    public OfflineHero(OfflinePlayer player, HeroData data) {

        super(player, data);
    }
}
