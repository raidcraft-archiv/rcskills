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

    @Override
    public ItemAttachment getItemAttachment(Player player, String attachmentName) throws ItemAttachmentException {

        // this is kinda ugly but it works ^^
        if (attachmentName.equalsIgnoreCase("skill")) {
            return new SkillRequirementAttachment();
        } else if (attachmentName.equalsIgnoreCase("level")) {
            return new LevelRequirementAttachment();
        }
        throw new ItemAttachmentException("Unknown Item Attachment with the displayName: " + attachmentName);
    }
}
