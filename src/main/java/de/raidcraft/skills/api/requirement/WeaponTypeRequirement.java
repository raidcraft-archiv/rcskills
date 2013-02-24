package de.raidcraft.skills.api.requirement;

import de.raidcraft.api.requirement.AbstractRequirement;
import de.raidcraft.api.requirement.RequirementInformation;
import de.raidcraft.skills.items.WeaponType;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@RequirementInformation("weapon")
public class WeaponTypeRequirement extends AbstractRequirement<Unlockable> {

    private WeaponType type;

    protected WeaponTypeRequirement(Unlockable resolver, ConfigurationSection config) {

        super(resolver, config);
    }

    @Override
    protected void load(ConfigurationSection data) {

        type = WeaponType.fromString(data.getString("type"));
    }

    @Override
    public boolean isMet() {

        return WeaponType.fromMaterial(getResolver().getHero().getItemTypeInHand()) == type;
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
