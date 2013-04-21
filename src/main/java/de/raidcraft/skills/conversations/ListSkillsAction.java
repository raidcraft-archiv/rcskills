package de.raidcraft.skills.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;

/**
 * @author Silthus
 */
@ActionInformation(name = "LIST_SKILLS")
public class ListSkillsAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);

        try {
            Hero hero = plugin.getCharacterManager().getHero(conversation.getPlayer());
            plugin.getProfessionManager().getProfession(hero, args.getString("profession"));

            
        } catch (UnknownSkillException | UnknownProfessionException e) {
            throw new ActionArgumentException(e.getMessage());
        }
    }
}
