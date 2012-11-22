package de.raidcraft.skills.professions;

import de.raidcraft.skills.api.level.AbstractLevel;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.api.profession.Profession;

/**
 * @author Silthus
 */
public class ProfessionLevel extends AbstractLevel<Profession> {

    protected ProfessionLevel(Profession levelObject, LevelData data) {

        super(levelObject, data);
    }
}
