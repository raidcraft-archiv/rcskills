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
    public void setCurrent(int current) {

        if (getHero().getEntity().isDead()) {
            return;
        }
        try {
            getHero().setHealth(fireResourceChangeEvent(current));
        } catch (RaidCraftException ignored) {
        }
    }

    @Override
    public int getCurrent() {

        return getHero().getHealth();
    }

    @Override
    public int getMax() {

        return getHero().getMaxHealth();
    }

    @Override
    public int getMin() {

        return 0;
    }

    @Override
    public int getDefault() {

        return getHero().getDefaultHealth();
    }
}
