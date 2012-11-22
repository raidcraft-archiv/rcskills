package de.raidcraft.skills.api.persistance;

import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.util.DataMap;

/**
* @author Silthus
*/
public interface SkillData extends SkillProperties {

    public DataMap getData();

    public Profession getProfession();
}
