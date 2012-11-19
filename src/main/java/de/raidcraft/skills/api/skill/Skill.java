package de.raidcraft.skills.api.skill;

import de.raidcraft.api.inheritance.Parent;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.persistance.SkillData;

/**
 * @author Silthus
 */
public interface Skill extends Parent {

    public void load(SkillData data);

    public int getId();

    public String getName();

    public String getFriendlyName();

    public String getDescription();

    public String[] getUsage();

    public boolean hasUsePermission(RCPlayer player);
}
