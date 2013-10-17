package de.raidcraft.skills.api.persistance;

import de.raidcraft.api.database.Bean;

/**
 * @author Silthus
 */
public interface ResourceData extends Bean {

    public int getId();

    public String getName();

    public double getValue();

    public void setValue(double value);
}
