package de.raidcraft.skills.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.commands.QueuedCommand;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.api.conversations.stage.Stage;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.util.HeroUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author Silthus
 */
public class MaxOutHeroAction implements Action<Player> {


    @Override
    @Information(
            value = "hero.max-out",
            desc = "Brings all skills and professions of the hero to max level.",
            conf = {
                    "forced",
                    "confirmed"
            },
            aliases = {"MAXOUT_HERO"}
    )
    public void accept(Player player, ConfigurationSection config) {

        SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);
        boolean forced = config.getBoolean("forced", false);
        Hero hero = plugin.getCharacterManager().getHero(player);
        if (hero == null) return;

        if (!player.hasPermission("rcskills.conversation.maxout")) {
            if (!forced) {
                hero.sendMessage(ChatColor.RED + "Du besitzt nicht die nötigen Rechte um alle deine Skills & Klassen auf Max Level zu bringen.");
            }
            Conversations.endActiveConversation(player, ConversationEndReason.ENDED);
            return;
        }
        if (forced) {
            HeroUtil.maxOutAll(hero);
        } else {
            if (config.getBoolean("confirmed")) {
                maxOut(hero);
            } else {
                Optional<Conversation<Player>> activeConversation = Conversations.getActiveConversation(player);
                if (activeConversation.isPresent()) {
                    Stage stage = Stage.confirm(activeConversation.get(),
                            "Bist du dir sicher dass du alle deine Spezialisierungen auf das max. Level setzen möchtest?",
                            Action.ofMethod(this, "maxOut", hero));
                    activeConversation.get().changeToStage(stage);
                } else {
                    try {
                        new QueuedCommand(player, this, "maxOut", hero);
                    } catch (NoSuchMethodException e) {
                        Conversations.endActiveConversation(player, ConversationEndReason.ERROR);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void maxOut(Hero hero) {

        hero.sendMessage("");
        HeroUtil.maxOutAll(hero);
        hero.sendMessage(ChatColor.GREEN + "Alle deine Skills, Berufe und Klassen wurden auf max gesetzt.");
    }
}
