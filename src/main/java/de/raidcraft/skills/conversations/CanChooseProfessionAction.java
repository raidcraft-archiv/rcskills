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
import de.raidcraft.skills.api.profession.Profession;

/**
 * @author Silthus
 */
@ActionInformation(name = "CAN_CHOOSE_PROFESSION")
public class CanChooseProfessionAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);

        Hero hero = plugin.getCharacterManager().getHero(conversation.getPlayer());
        Profession profession;
        String success = args.getString("onsuccess", null);
        String failure = args.getString("onfailure", null);

        try {
            profession = plugin.getProfessionManager().getProfession(hero, args.getString("profession"));
            if (profession.isMeetingAllRequirements()) {
                if (success != null) {
                    conversation.setCurrentStage(success);
                    conversation.triggerCurrentStage();
                }
            }
            else {
                if (failure != null) {
                    conversation.setCurrentStage(failure);
                    conversation.triggerCurrentStage();
                }
            }
        } catch (UnknownSkillException | UnknownProfessionException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
    }
}
