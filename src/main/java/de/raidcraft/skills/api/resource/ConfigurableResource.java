package de.raidcraft.skills.api.resource;

import de.raidcraft.skills.api.persistance.ResourceData;
import de.raidcraft.skills.api.profession.Profession;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class ConfigurableResource extends AbstractResource {

    public ConfigurableResource(ResourceData data, Profession profession, ConfigurationSection config) {

        super(data, profession, config);
    }
}
