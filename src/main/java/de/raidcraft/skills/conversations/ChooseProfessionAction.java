package de.raidcraft.skills.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.economy.BalanceSource;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.conversations.EndReason;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.util.ProfessionUtil;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
@ActionInformation(name = "CHOOSE_PROFESSION")
public class ChooseProfessionAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);
        boolean forced = args.getBoolean("forced", false);
        boolean quite = args.getBoolean("quite", false);

        Hero hero = plugin.getCharacterManager().getHero(conversation.getPlayer());
        Profession profession;
        try {
            profession = plugin.getProfessionManager().getProfession(hero, args.getString("profession"));
            double cost = 0.0;
            if (profession.getAttachedLevel().getLevel() > 1) {
                cost = ProfessionUtil.getProfessionChangeCost(profession);
            }

            if (!forced) {
                if (hero.hasProfession(profession) && profession.isActive()) {
                    hero.sendMessage("", ChatColor.AQUA + "Du besitzt die " + profession.getPath().getFriendlyName() + " Spezialisierung '"
                            + profession.getFriendlyName() + "' bereits!");
                    conversation.endConversation(EndReason.INFORM);
                    return;
                }
            }
            if (!profession.isMeetingAllRequirements(hero)) {
                hero.sendMessage(ChatColor.RED + profession.getResolveReason(hero));
                conversation.endConversation(EndReason.INFORM);
                return;
            }
            if (forced) {
                hero.changeProfession(profession);
                if (!quite) {
                    hero.sendMessage("", ChatColor.GREEN + "Du besitzt nun die " + profession.getPath().getFriendlyName()
                            + " Spezialisierung " + profession.getFriendlyName() + "!");
                }
            } else {
                if (args.getBoolean("confirmed")) {

                    hero.sendMessage("");
                    hero.changeProfession(profession);
                    if (cost > 0.0) {
                        RaidCraft.getEconomy().modify(hero.getPlayer().getUniqueId(), -cost, BalanceSource.SKILL, "Wechsel zu " + profession.getFriendlyName());
                    }
                    hero.sendMessage("", ChatColor.AQUA + "Viel Spaß mit deiner neuen " + profession.getPath().getFriendlyName() + " Spezialisierung!", "");
                } else {
                    if (cost <= 0.0 || RaidCraft.getEconomy().hasEnough(hero.getPlayer().getUniqueId(), cost)) {
                        conversation.triggerStage(createConfirmStage(
                                "Bist du dir sicher dass du die " + profession.getPath().getFriendlyName() + " Spezialisierung "
                                        + profession.getFriendlyName() + " wählen willst?" +
                                        (cost > 0.0 ? "\n Dies kostet dich " + RaidCraft.getEconomy().getFormattedAmount(ProfessionUtil.getProfessionChangeCost(profession)) : ""), args
                        ));
                    } else {
                        hero.sendMessage(ChatColor.RED + "Du benötigst hierfür mindestens " + RaidCraft.getEconomy().getFormattedAmount(cost));
                        conversation.endConversation(EndReason.INFORM);
                    }
                }
            }
        } catch (UnknownSkillException | UnknownProfessionException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
    }
}
