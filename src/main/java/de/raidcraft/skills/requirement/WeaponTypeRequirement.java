package de.raidcraft.skills.requirement;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.requirement.AbstractRequirement;
import de.raidcraft.api.requirement.RequirementInformation;
import de.raidcraft.skills.items.WeaponType;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@RequirementInformation("weapons")
public class WeaponTypeRequirement extends AbstractRequirement<SkillRequirementResolver> {

    private WeaponType type;

    public WeaponTypeRequirement(SkillRequirementResolver resolver, ConfigurationSection config) {

        super(resolver, config);
    }

    @Override
    protected void load(ConfigurationSection data) {

        type = WeaponType.fromString(data.getString("type"));
        if (type == null) {
            RaidCraft.LOGGER.warning("Wrong WeaponType " + data.getString("type") + " defined in requirement for " + getResolver());
        }
    }

    @Override
    public boolean isMet() {

        return type != null && WeaponType.fromMaterial(getResolver().getHero().getItemTypeInHand()) == type;
    }

    @Override
    public String getShortReason() {

        return "Waffe vom Typ " + type.getFriendlyName() + " in der Hand.";
    }

    @Override
    public String getLongReason() {

        return "Du musst eine Waffe vom Typ " + type.getFriendlyName() + " in der Hand haben.";
    }
}
