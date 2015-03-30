package de.raidcraft.skills.random;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.random.GenericRDSValue;
import de.raidcraft.api.random.Obtainable;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSObjectFactory;
import de.raidcraft.api.random.Spawnable;
import de.raidcraft.skills.CharacterManager;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
public class ExpLootObject extends GenericRDSValue<Integer> implements Obtainable, Spawnable {

    @RDSObjectFactory.Name("rcskills.exp")
    public static class ExpLootFactory implements RDSObjectFactory {

        @Override
        public RDSObject createInstance(ConfigurationSection config) {

            return new ExpLootObject(config.getInt("exp", 0));
        }

    }

    public ExpLootObject(Integer value) {

        super(value);
    }

    @Override
    public void addTo(Player player) {

        if (getValue().isPresent() && getValue().get() > 0) {
            Hero hero = RaidCraft.getComponent(CharacterManager.class).getHero(player);
            hero.getExpPool().addExp(getValue().get(), true);
        }
    }

    @Override
    public void spawn(Location location) {

        if (getValue().isPresent()) {
            ExperienceOrb exp = location.getWorld().spawn(location, ExperienceOrb.class);
            exp.setExperience(getValue().get());
        }
    }
}
