package de.raidcraft.skills.api.persistance;

import de.raidcraft.api.database.Bean;

/**
 * @author Silthus
 */
public interface ResourceData extends Bean {

    int getId();

    String getName();

    double getValue();

    void setValue(double value);
}
