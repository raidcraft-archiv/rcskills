package de.raidcraft.skills.professions.classes;

import de.raidcraft.api.inheritance.Parent;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.profession.AbstractProfession;

/**
 * @author Silthus
 */
public class Warrior extends AbstractProfession implements Parent {

    protected Warrior(int id) throws UnknownProfessionException {

        super(id);
    }
}
