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
        if(args.getBoolean("forced", false)) {
            HeroUtil.maxOutAll(hero);
        }
        else {
            if (args.getBoolean("confirmed")) {
                hero.sendMessage("");
                HeroUtil.maxOutAll(hero);
                hero.sendMessage(ChatColor.GREEN + "Alle deine Skills, Berufe und Klassen wurden auf max gesetzt.");
            } else {
                conversation.triggerStage(createConfirmStage(
                        "Bist du dir sicher dass du alle deine Spezialisierungen auf das max. Level setzen möchtest?", args));
            }
        }
    }
}
