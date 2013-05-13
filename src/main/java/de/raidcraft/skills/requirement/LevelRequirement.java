package de.raidcraft.skills.requirement;

import de.raidcraft.api.requirement.AbstractRequirement;
import de.raidcraft.api.requirement.RequirementResolver;
import de.raidcraft.skills.api.level.Levelable;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public abstract class LevelRequirement<T extends RequirementResolver> extends AbstractRequirement<T> {

    private int requiredLevel;

    public LevelRequirement(T resolver, ConfigurationSection config) {

        super(resolver, config);
    }

    protected abstract Levelable getLevelable();

    @Override
    protected void load(ConfigurationSection data) {

        requiredLevel = data.getInt("level", getLevelable().getMaxLevel());
    }

    protected int getRequiredLevel() {

        return requiredLevel;
    }

    @Override
    public boolean isMet() {

        return requiredLevel <= getLevelable().getAttachedLevel().getLevel();
    }
}
