package de.raidcraft.skills.api.requirement;

import de.raidcraft.skills.api.hero.Hero;

import java.util.List;

/**
 * @author Silthus
 */
public interface Unlockable {

    public Hero getHero();

    public List<Requirement> getRequirements();

    public void addRequirement(Requirement requirement);

    /**
     * Checks if all requirements are met.
     *
     * @return true if all requirements are met
     */
    public boolean isUnlockable();

    public String getUnlockReason();
}
