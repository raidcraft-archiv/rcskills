package de.raidcraft.skills.api.persistance;

/**
* @author Silthus
*/
public interface SkillData extends SkillProperties {

    public abstract String getFriendlyName();

    public abstract String getDescription();

    public abstract String[] getUsage();
}
