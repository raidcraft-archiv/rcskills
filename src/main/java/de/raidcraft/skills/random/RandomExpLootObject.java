package de.raidcraft.skills.random;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.random.*;
import de.raidcraft.skills.CharacterManager;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author mdoering
 */
public class RandomExpLootObject extends GenericRDSValue<Integer> implements Obtainable {

    @RDSObjectFactory.Name("rcskills.random-exp")
    public static class RandomExpLootFactory implements RDSObjectFactory {

        @Override
        public RDSObject createInstance(ConfigurationSection config) {

            return new RandomExpLootObject(config.getInt("min", 0), config.getInt("max", 0));
        }
    }

    private int min;
    private int max;

    public RandomExpLootObject(int minValue, int maxValue) {

        super(minValue);
        this.min = minValue;
        this.max = maxValue >= minValue ? maxValue : minValue;
    }

    @Override
    public Optional<Integer> getValue() {

        return Optional.of(RDSRandom.getIntValue(min, max));
    }

    @Override
    public void addTo(Player player) {

        if (getValue().isPresent() && getValue().get() > 0) {
            Hero hero = RaidCraft.getComponent(CharacterManager.class).getHero(player);
            hero.getExpPool().addExp(getValue().get(), true);
        }
    }
}
