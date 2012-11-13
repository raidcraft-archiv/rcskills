package de.raidcraft.skills.api.skill;

import de.raidcraft.api.player.RCPlayer;

/**
 * @author Silthus
 */
public interface Skill {

    public int getId();

    public String getName();

    public String getDescription();

    public String[] getUsage();

    public boolean hasUsePermission(RCPlayer player);
}
