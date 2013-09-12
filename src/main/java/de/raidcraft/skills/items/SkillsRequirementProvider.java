package de.raidcraft.skills.items;

import de.raidcraft.api.items.attachments.ItemAttachment;
import de.raidcraft.api.items.attachments.ItemAttachmentException;
import de.raidcraft.api.items.attachments.ItemAttachmentProvider;
import de.raidcraft.api.items.attachments.ProviderInformation;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
@ProviderInformation("skills.requirements")
public class SkillsRequirementProvider implements ItemAttachmentProvider {

    private final SkillRequirementAttachment skillRequirementAttachment = new SkillRequirementAttachment();

    @Override
    public ItemAttachment getItemAttachment(Player player, String attachmentName) throws ItemAttachmentException {

        // this is kinda ugly but it works ^^
        if (attachmentName.equalsIgnoreCase("skill")) {
            return skillRequirementAttachment;
        }
        throw new ItemAttachmentException("Unknown Item Attachment with the name: " + attachmentName);
    }
}
