package de.raidcraft.skills.api.persistance;

import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.util.DataMap;

/**
* @author Silthus
*/
public interface SkillData extends SkillProperties {

    public int getId();

    public DataMap getData();

    public SkillInformation getSkillInformation();

    public String getFriendlyName();

    public String getDescription();

    public String[] getUsage();
}
