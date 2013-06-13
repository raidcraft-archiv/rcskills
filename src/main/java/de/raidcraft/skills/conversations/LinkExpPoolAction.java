package de.raidcraft.skills.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.conversations.EndReason;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.hero.Option;
import de.raidcraft.skills.api.path.Path;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.util.HeroUtil;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
@ActionInformation(name = "LINK_EXP_POOL")
public class LinkExpPoolAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);
        Hero hero = plugin.getCharacterManager().getHero(conversation.getPlayer());
        Path<Profession> path = hero.getPath(args.getString("path"));
        if (path == null) {
            throw new ActionArgumentException("Unknwon configured path " + args.getString("path"));
        }
        if (!conversation.getPlayer().hasPermission("rcskills.conversation.linkexp")) {
            hero.sendMessage(ChatColor.RED + "Du darfst diese Funktion hier nicht nutzen!");
            conversation.endConversation(EndReason.INFORM);
            return;
        }
        Profession profession = HeroUtil.getActivePathProfession(hero, path);
        if (profession == null) {
            hero.sendMessage(ChatColor.RED + "Du hast keine Spezialisierung die du linken kannst.");
            conversation.endConversation(EndReason.INFORM);
            return;
        }
        Option.EXP_POOL_LINK.set(hero, profession.getName());
    }
}
