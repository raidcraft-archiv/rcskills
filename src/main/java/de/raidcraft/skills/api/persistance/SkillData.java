package de.raidcraft.skills.api.persistance;

import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.util.DataMap;

/**
* @author Silthus
*/
public interface SkillData extends SkillProperties {

    public SkillInformation getSkillInformation();

    public DataMap getData();

    public String getProfession();
}
