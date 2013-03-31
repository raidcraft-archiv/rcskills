package de.raidcraft.skills.requirement;

import de.raidcraft.api.requirement.RequirementResolver;
import de.raidcraft.skills.api.hero.Hero;

/**
 * @author Silthus
 */
public interface SkillRequirementResolver extends RequirementResolver {

    public Hero getHero();
}
