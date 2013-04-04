package de.raidcraft.skills.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public class ChooseProfessionAction extends AbstractAction {

    public ChooseProfessionAction(String name) {

        super(name);
    }

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws Throwable {

        SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);

        Hero hero = plugin.getCharacterManager().getHero(conversation.getPlayer());
        Profession profession = plugin.getProfessionManager().getProfession(hero, args.getString("profession"));
        if (!profession.isMeetingAllRequirements()) {
            hero.sendMessage(ChatColor.RED + profession.getResolveReason());
            conversation.triggerCurrentStage();
            return;
        }
        hero.changeProfession(profession);
    }
}
