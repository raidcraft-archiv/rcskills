package de.raidcraft.skills.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.util.SkillUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class ListProfessionSkills implements Action<Player> {

    @Override
    @Information(
            value = "profession.list-skills",
            desc = "Lists all skills of the given profession.",
            conf = {
                    "profession"
            },
            aliases = {"LIST_PROFESSION_SKILLS"}
    )
    public void accept(Player player, ConfigurationSection config) {

        SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);
        Hero hero = plugin.getCharacterManager().getHero(player);
        if (hero == null) return;
        Profession profession;
        try {
            profession = plugin.getProfessionManager().getProfession(hero, config.getString("profession"));

            hero.sendMessage("", ChatColor.AQUA + "Mit der " + profession.getPath().getFriendlyName() + " Spezialisierung '"
                    + profession.getFriendlyName() + "' besitzt man folgende Fähigkeiten: ");

            int i = 0;
            ChatColor color;
            ComponentBuilder msg = new ComponentBuilder("");
            for (Skill skill : profession.getSkills()) {
                i++;
                if (i % 2 == 0) {
                    color = ChatColor.YELLOW;
                } else {
                    color = ChatColor.WHITE;
                }
                msg.append(skill.getFriendlyName()).color(color.asBungee())
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, SkillUtil.getSkillTooltip(skill, true)));
            }
            player.spigot().sendMessage(msg.create());

            player.spigot().sendMessage(new ComponentBuilder("Du kannst dir deine Skills auch mit ")
                    .color(ChatColor.YELLOW.asBungee()).append("/skills ").color(ChatColor.AQUA.asBungee())
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/skills"))
                    .append("anzeigen lassen.").color(ChatColor.YELLOW.asBungee())
                    .create());
        } catch (UnknownSkillException | UnknownProfessionException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
            Conversations.endActiveConversation(player, ConversationEndReason.ERROR);
        }
    }
}
