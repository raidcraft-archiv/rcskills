package de.raidcraft.skills.requirement;

import de.raidcraft.api.requirement.AbstractRequirement;
import de.raidcraft.api.requirement.RequirementResolver;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Levelable;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public abstract class LevelRequirement extends AbstractRequirement<Hero> {

    private int requiredLevel;

    public LevelRequirement(RequirementResolver<Hero> resolver, ConfigurationSection config) {

        super(resolver, config);
    }

    protected abstract Levelable getLevelable();

    @Override
    protected void load(ConfigurationSection data) {

        requiredLevel = data.getInt("level", getLevelable() == null ? 1 : getLevelable().getMaxLevel());
    }

    protected int getRequiredLevel() {

        return requiredLevel;
    }

    @Override
    public boolean isMet(Hero object) {

        return getLevelable() != null && getLevelable().getAttachedLevel() != null
                && requiredLevel <= getLevelable().getAttachedLevel().getLevel();
    }
}
