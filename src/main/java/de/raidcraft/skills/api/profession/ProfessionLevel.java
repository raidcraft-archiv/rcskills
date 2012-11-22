package de.raidcraft.skills.api.profession;

import de.raidcraft.skills.api.level.AbstractLevel;
import de.raidcraft.skills.api.persistance.ProfessionData;

/**
 * @author Silthus
 */
public class ProfessionLevel extends AbstractLevel<Profession> {

    public ProfessionLevel(Profession profession, ProfessionData data) {

        super(profession, data.getLevelData());
    }
}
