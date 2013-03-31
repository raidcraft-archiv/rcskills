package de.raidcraft.skills.requirement;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.requirement.AbstractRequirement;
import de.raidcraft.api.requirement.RequirementInformation;
import de.raidcraft.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@RequirementInformation("items")
public class ItemRequirement extends AbstractRequirement<SkillRequirementResolver> {

    private int itemId;

    public ItemRequirement(SkillRequirementResolver resolver, ConfigurationSection config) {

        super(resolver, config);
    }

    @Override
    protected void load(ConfigurationSection data) {

        Material item = ItemUtils.getItem(data.getString("item"));
        if (item == null) {
            RaidCraft.LOGGER.warning("Unknown item " + data.getString("item") + " defined in requirement for " + getResolver());
        } else {
            itemId = item.getId();
        }
    }

    @Override
    public boolean isMet() {

        return getResolver().getHero().getItemTypeInHand().getId() == itemId;
    }

    @Override
    public String getShortReason() {

        return ItemUtils.getFriendlyName(itemId) + " in der Hand.";
    }

    @Override
    public String getLongReason() {

        return ItemUtils.getFriendlyName(itemId) + " muss sich in der Hand befinden.";
    }
}
