package de.raidcraft.skills.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.commands.QueuedCommand;
import de.raidcraft.api.config.builder.ConfigGenerator;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.stage.Stage;
import de.raidcraft.api.economy.BalanceSource;
import de.raidcraft.conversations.actions.EndConversationAction;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.util.ProfessionUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author Silthus
 */
public class ChooseProfessionAction implements Action<Player> {

    @Override
    @ConfigGenerator.Information(
            value = "profession.choose",
            desc = "Chooses the given profession for the player.",
            conf = {
                    "profession",
                    "force",
                    "quiet"
            },
            aliases = {"CHOOSE_PROFESSION"}
    )
    public void accept(Player player, ConfigurationSection config) {

        SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);
        boolean forced = config.getBoolean("force", false);
        boolean quiet = config.getBoolean("quiet", false);

        Hero hero = plugin.getCharacterManager().getHero(player);
        if (hero == null) return;
        Profession profession;
        try {
            profession = plugin.getProfessionManager().getProfession(hero, config.getString("profession"));
            double cost = 0.0;
            if (profession.getAttachedLevel().getLevel() > 1) {
                cost = ProfessionUtil.getProfessionChangeCost(profession);
            }

            if (!forced) {
                if (hero.hasProfession(profession) && profession.isActive()) {
                    hero.sendMessage("", ChatColor.AQUA + "Du besitzt die " + profession.getPath().getFriendlyName() + " Spezialisierung '"
                            + profession.getFriendlyName() + "' bereits!");
                    return;
                }
            }
            if (!profession.isMeetingAllRequirements(hero.getPlayer())) {
                hero.sendMessage(ChatColor.RED + profession.getResolveReason(hero.getPlayer()));
                return;
            }
            if (forced) {
                changeProfession(hero, profession, quiet);
            } else {
                if (config.getBoolean("confirmed")) {
                    hero.sendMessage("");
                    hero.changeProfession(profession);
                    if (cost > 0.0) {
                        RaidCraft.getEconomy().modify(hero.getPlayer().getUniqueId(), -cost, BalanceSource.SKILL, "Wechsel zu " + profession.getFriendlyName());
                    }
                    hero.sendMessage("", ChatColor.AQUA + "Viel Spaß mit deiner neuen " + profession.getPath().getFriendlyName() + " Spezialisierung!", "");
                } else {
                    if (cost <= 0.0 || RaidCraft.getEconomy().hasEnough(hero.getPlayer().getUniqueId(), cost)) {
                        Optional<Conversation<Player>> activeConversation = Conversations.getActiveConversation(player);
                        if (!activeConversation.isPresent()) {
                            // issue a confirm command
                            try {
                                new QueuedCommand(player, this, "changeProfession", hero, profession, quiet);
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Stage stage = Stage.of(activeConversation.get(),
                                    "Bist du dir sicher dass du die " + profession.getPath().getFriendlyName() + " Spezialisierung "
                                            + profession.getFriendlyName() + " wählen willst?" +
                                            (cost > 0.0 ? "| Dies kostet dich " + RaidCraft.getEconomy().getFormattedAmount(ProfessionUtil.getProfessionChangeCost(profession)) : ""))
                                    .addAnswer(Answer.of("Ja bin ich.")
                                            .addAction(Action.ofMethod(this, "changeProfession", hero, profession, quiet)))
                                    .addAnswer(Answer.of("Nein, ich habe es mir anders überlegt.")
                                            .addAction(Action.of(EndConversationAction.class)));
                            activeConversation.get().changeToStage(stage);
                        }
                    } else {
                        hero.sendMessage(ChatColor.RED + "Du benötigst hierfür mindestens " + RaidCraft.getEconomy().getFormattedAmount(cost));
                    }
                }
            }
        } catch (UnknownSkillException | UnknownProfessionException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }

    private void changeProfession(Hero hero, Profession profession, boolean quiet) {

        hero.changeProfession(profession);
        if (!quiet) {
            hero.sendMessage("", ChatColor.GREEN + "Du besitzt nun die " + profession.getPath().getFriendlyName()
                    + " Spezialisierung " + profession.getFriendlyName() + "!");
        }
    }
}
