package de.raidcraft.skills.api.requirement;

import de.raidcraft.api.requirement.Requirement;
import de.raidcraft.skills.api.hero.Hero;

import java.util.List;

/**
 * @author Silthus
 */
public interface Unlockable {

    public Hero getHero();

    public List<Requirement> getRequirements();

    public boolean isUnlockable();

    public String getUnlockReason();
}
