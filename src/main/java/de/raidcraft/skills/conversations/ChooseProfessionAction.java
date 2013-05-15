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
            if(hero.hasProfession(profession) && profession.isActive()) {
                hero.sendMessage("", ChatColor.AQUA + "Du besitzt die " + profession.getPath().getFriendlyName() + " Spezialisierung '"
                        + profession.getFriendlyName() + "' bereits!");
                conversation.endConversation();
                return;
            }
            if (!profession.isMeetingAllRequirements(hero)) {
                hero.sendMessage(ChatColor.RED + profession.getResolveReason(hero));
                conversation.triggerCurrentStage();
                conversation.endConversation();
                return;
            }
            if (args.getBoolean("confirmed")) {

                hero.sendMessage("");
                hero.changeProfession(profession);
                hero.sendMessage("", ChatColor.AQUA + "Viel Spaß mit deiner neuen " + profession.getPath().getFriendlyName() + " Spezialisierung!", "");
            } else {
                conversation.triggerStage(createConfirmStage(
                        "Bist du dir sicher dass du die " + profession.getPath().getFriendlyName() + " Spezialisierung "
                                + profession.getFriendlyName() + " wählen willst? Dies kostet dich " + plugin.getCommonConfig().profession_change_cost + "c.", args));
            }
        } catch (UnknownSkillException | UnknownProfessionException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
    }
}
