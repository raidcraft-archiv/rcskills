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
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
@ActionInformation(name = "CHOOSE_PROFESSION")
public class ChooseProfessionAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);

        Hero hero = plugin.getCharacterManager().getHero(conversation.getPlayer());
        Profession profession;
        try {
            profession = plugin.getProfessionManager().getProfession(hero, args.getString("profession"));
            if (!profession.isMeetingAllRequirements()) {
                hero.sendMessage(ChatColor.RED + profession.getResolveReason());
                conversation.triggerCurrentStage();
                return;
            }
            if (args.getBoolean("confirmed")) {
                hero.changeProfession(profession);
            } else {
                conversation.triggerStage(createConfirmStage(
                        "Bist du dir sicher dass du die " + profession.getPath().getFriendlyName() + " Spezialisierung "
                                + profession.getFriendlyName() + " wählen willst?", args));
            }
        } catch (UnknownSkillException | UnknownProfessionException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
    }
}
