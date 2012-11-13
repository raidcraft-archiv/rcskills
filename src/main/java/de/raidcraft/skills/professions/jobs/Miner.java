package de.raidcraft.skills.professions.jobs;

import de.raidcraft.api.inheritance.Parent;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.profession.AbstractProfession;

/**
 * @author Silthus
 */
public class Miner extends AbstractProfession implements Parent {

    public Miner(int id) throws UnknownProfessionException {

        super(id);
    }
}
