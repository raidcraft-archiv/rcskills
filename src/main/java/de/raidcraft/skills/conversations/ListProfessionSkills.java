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
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
@ActionInformation(name = "LIST_PROFESSION_SKILLS")
public class ListProfessionSkills extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);
        Hero hero = plugin.getCharacterManager().getHero(conversation.getPlayer());
        Profession profession;

        try {
            profession = plugin.getProfessionManager().getProfession(hero, args.getString("profession"));

            hero.sendMessage("", ChatColor.AQUA + "Mit der " + profession.getPath().getFriendlyName() + " Spezialisierung '"
                    + profession.getFriendlyName() + "' besitzt man folgende Fähigkeiten: ");

            String output = "";
            int i = 0;
            ChatColor color;
            for(Skill skill : profession.getSkills()) {
                i++;
                if(i%2 == 0) {
                    color = ChatColor.YELLOW;
                }
                else {
                    color = ChatColor.WHITE;
                }
                output += color + skill.getFriendlyName() + ", ";
            }

            hero.sendMessage(output);
            hero.sendMessage("", ChatColor.AQUA + "Eine bessere Übersicht findest du in unserem Wiki: " + ChatColor.LIGHT_PURPLE + "wiki.raid-craft.de",
                    ChatColor.AQUA + "Oder mit dem Befehl: " + ChatColor.YELLOW + "/skills " + profession.getFriendlyName());

        } catch (UnknownSkillException | UnknownProfessionException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
    }
}
