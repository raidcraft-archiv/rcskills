package de.raidcraft.skills.hero;

import de.raidcraft.skills.api.hero.AbstractHero;
import de.raidcraft.skills.api.persistance.HeroData;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class TemporaryHero extends AbstractHero {

    public TemporaryHero(Player player, HeroData data) {

        super(player, data);
    }

    @Override
    public void save() {

        // do not save
        saveProfessions();
        saveSkills();
    }
}
