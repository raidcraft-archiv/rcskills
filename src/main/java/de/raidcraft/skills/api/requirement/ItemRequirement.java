package de.raidcraft.skills.api.requirement;

import de.raidcraft.api.requirement.AbstractRequirement;
import de.raidcraft.api.requirement.RequirementInformation;
import de.raidcraft.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@RequirementInformation("item")
public class ItemRequirement extends AbstractRequirement<Unlockable> {

    private Material item;

    protected ItemRequirement(Unlockable resolver, ConfigurationSection config) {

        super(resolver, config);
    }

    @Override
    protected void load(ConfigurationSection data) {

        item = ItemUtils.getItem(data.getString("item"));
    }

    @Override
    public boolean isMet() {

        return getResolver().getHero().getItemTypeInHand() == item;
    }

    @Override
    public String getShortReason() {

        return ItemUtils.getFriendlyName(item) + " in der Hand.";
    }

    @Override
    public String getLongReason() {

        return ItemUtils.getFriendlyName(item) + " muss sich in der Hand befinden.";
    }
}
