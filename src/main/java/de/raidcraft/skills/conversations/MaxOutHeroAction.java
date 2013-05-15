package de.raidcraft.skills.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.util.HeroUtil;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
@ActionInformation(name = "MAXOUT_HERO")
public class MaxOutHeroAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);
        Hero hero = plugin.getCharacterManager().getHero(conversation.getPlayer());

        if (!conversation.getPlayer().hasPermission("rcskills.conv.maxout")) {
            hero.sendMessage(ChatColor.RED + "Du darfst diese Funktion hier nicht nutzen!");
            conversation.endConversation();
            return;
        }
        HeroUtil.maxOutAll(hero);
    }
}
