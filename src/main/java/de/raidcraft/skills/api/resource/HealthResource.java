package de.raidcraft.skills.api.resource;

import de.raidcraft.api.RaidCraftException;
import de.raidcraft.skills.api.persistance.ResourceData;
import de.raidcraft.skills.api.profession.Profession;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class HealthResource extends AbstractResource {

    public HealthResource(ResourceData data, Profession profession, ConfigurationSection config) {

        super(data, profession, config);
    }

    @Override
    public double getCurrent() {

        return getHero().getHealth();
    }

    @Override
    public void setCurrent(double current) {

        if (!getHero().isOnline() || getHero().getEntity().isDead()) {
            return;
        }
        try {
            getHero().setHealth(fireResourceChangeEvent(current));
        } catch (RaidCraftException ignored) {
        }
    }

    @Override
    public double getMax() {

        return getHero().getMaxHealth();
    }

    @Override
    public double getMin() {

        return 0;
    }

    @Override
    public double getDefault() {

        return getHero().getDefaultHealth();
    }
}
