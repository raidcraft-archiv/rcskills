package de.raidcraft.skills.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.hero.Option;
import de.raidcraft.skills.api.path.Path;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.util.HeroUtil;
import de.raidcraft.util.ConfigUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class LinkExpPoolAction implements Action<Player> {

    @Override
    @Information(
            value = "exppool.link",
            desc = "Links the EXP Pool to the active profession of the given path.",
            conf = {
                    "path"
            },
            aliases = {"LINK_EXP_POOL"}
    )
    public void accept(Player player, ConfigurationSection config) {

        SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);
        Hero hero = plugin.getCharacterManager().getHero(player);
        if (hero == null) return;
        Path<Profession> path = hero.getPath(config.getString("path"));

        if (path == null) {
            RaidCraft.LOGGER.warning("Unknwon configured path " + config.getString("path") + " in action " + ConfigUtil.getFileName(config));
            Conversations.endActiveConversation(player, ConversationEndReason.ERROR);
            return;
        }
        if (!player.hasPermission("rcskills.conversation.linkexp")) {
            hero.sendMessage(ChatColor.RED + "Du hast nicht die nötigen Rechte um deinen EXP Pool zu linken.");
            Conversations.endActiveConversation(player, ConversationEndReason.ENDED);
            return;
        }
        Profession profession = HeroUtil.getActivePathProfession(hero, path);
        if (profession == null) {
            hero.sendMessage(ChatColor.RED + "Du hast keine Spezialisierung die du linken kannst.");
            Conversations.endActiveConversation(player, ConversationEndReason.ENDED);
            return;
        }
        Option.EXP_POOL_LINK.set(hero, profession.getName());
    }
}
