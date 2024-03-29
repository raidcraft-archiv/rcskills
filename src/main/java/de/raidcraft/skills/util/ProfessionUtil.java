package de.raidcraft.skills.util;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.util.StringUtil;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.requirement.Reasonable;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.skill.Skill;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public final class ProfessionUtil {

    private ProfessionUtil() {

    }

    public static Profession getProfessionFromArgs(Hero hero, String input) throws CommandException {

        return getProfessionFromArgs(hero, input, RaidCraft.getComponent(SkillsPlugin.class).getProfessionManager().getAllProfessions(hero));
    }

    public static Profession getProfessionFromArgs(Hero hero, String input, Collection<Profession> choices) throws CommandException {

        input = input.toLowerCase();
        List<Profession> professions = new ArrayList<>();
        for (Profession profession : choices) {
            if (profession.getName().contains(input) || profession.getProperties().getFriendlyName().toLowerCase().contains(input)) {
                professions.add(profession);
            }
        }

        if (professions.size() < 1) {
            throw new CommandException("Es gibt keine Spezialisierung mit dem Namen: " + input);
        }

        if (professions.size() > 1) {
            for (Profession profession : professions) {
                if (profession.getFriendlyName().equalsIgnoreCase(input) || profession.getName().equalsIgnoreCase(input)) {
                    return profession;
                }
            }
            throw new CommandException(
                    "Es gibt mehrere Spezialisierungen mit dem Namen " + input + ":" + StringUtil.joinString(professions, ", ", 0));
        }

        return professions.get(0);
    }

    public static Collection<String> renderProfessionInformation(Profession profession) {

        List<String> strings = formatBody(profession);
        strings.add(0, formatHeader(profession));
        return strings;
    }

    @SuppressWarnings("unchecked")
    public static List<String> formatBody(Profession profession) {

        List<String> body = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.GRAY).append(ChatColor.ITALIC).append(profession.getProperties().getDescription());
        body.add(sb.toString());

        // live information
        sb = new StringBuilder();
        sb.append(ChatColor.YELLOW).append("Leben: ").append(ChatColor.AQUA).append(profession.getHero().getHealth());
        sb.append(ChatColor.YELLOW).append("/").append(ChatColor.AQUA).append(profession.getHero().getMaxHealth());
        body.add(sb.toString());

        // level information
        sb = new StringBuilder();
        sb.append(ChatColor.YELLOW).append("Level: ").append(ChatColor.AQUA).append(profession.getAttachedLevel().getLevel())
                .append(ChatColor.YELLOW).append("/").append(ChatColor.AQUA).append(profession.getAttachedLevel().getMaxLevel());
        sb.append(ChatColor.YELLOW).append("  |   EXP: ").append(ChatColor.AQUA).append(profession.getAttachedLevel().getExp())
                .append(ChatColor.YELLOW).append("/").append(ChatColor.AQUA).append(profession.getAttachedLevel().getMaxExp());
        body.add(sb.toString());

        if (profession.getResources().size() > 0) {
            sb = new StringBuilder();
            sb.append(ChatColor.YELLOW).append("Resourcen: \n");
            for (Resource resource : profession.getResources()) {
                sb.append(ChatColor.YELLOW).append("  - ");
                sb.append(ChatColor.YELLOW).append(resource.getFriendlyName()).append(": ");
                sb.append(ChatColor.AQUA).append(resource.getCurrent()).append(ChatColor.YELLOW).append("/");
                sb.append(ChatColor.AQUA).append(resource.getMax()).append(ChatColor.YELLOW);
                sb.append("\n");
            }
            body.add(sb.toString());
        }

        if (profession.getRequirements().size() > 0) {
            sb = new StringBuilder();
            sb.append(ChatColor.YELLOW).append("Vorraussetzungen: \n");
            for (Requirement<Player> requirement : profession.getRequirements()) {
                if (requirement instanceof Reasonable) {
                    sb.append(ChatColor.YELLOW).append("  - ");
                    sb.append((requirement.test(profession.getHero().getPlayer()) ? ChatColor.GREEN : ChatColor.RED));
                    sb.append(((Reasonable<Player>) requirement).getReason(profession.getHero().getPlayer()));
                    sb.append("\n");
                }
            }
            body.add(sb.toString());
        }

        sb = new StringBuilder();
        sb.append(ChatColor.GRAY).append(ChatColor.ITALIC);
        sb.append("Für eine Liste alle Skills gebe /skills ").append(profession.getFriendlyName()).append(" ein.");
        body.add(sb.toString());

        return body;
    }

    public static String formatHeader(Profession profession) {

        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.YELLOW).append("------- [");
        sb.append(ChatColor.AQUA).append(profession.getAttachedLevel().getLevel());
        sb.append(ChatColor.YELLOW).append("] ").append(profession.isActive() ? ChatColor.GREEN : ChatColor.RED);
        sb.append(profession.getFriendlyName());
        sb.append(ChatColor.YELLOW).append(" -------");
        return sb.toString();
    }

    public static BaseComponent[] getProfessionTooltip(Profession profession, boolean isTooltip) {

        List<BaseComponent> messages = new ArrayList<>();
        messages.addAll(Arrays.asList(new ComponentBuilder("------- [").color(ChatColor.YELLOW)
                .append(profession.getTotalLevel() + "").color(ChatColor.AQUA)
                .append("]").color(ChatColor.YELLOW).append(" ")
                .append(profession.getFriendlyName()).color(profession.isActive() ? ChatColor.GREEN : ChatColor.GRAY)
                .append(profession.getPath() != null ? " (" + profession.getPath().getFriendlyName() + ")" : "").color(ChatColor.DARK_PURPLE)
                .append(" -------").color(ChatColor.YELLOW).create()));

        messages.addAll(Arrays.asList(new ComponentBuilder("Level: ").color(ChatColor.YELLOW)
                .append(profession.getAttachedLevel().getLevel() + "").color(ChatColor.AQUA)
                .append("/").color(ChatColor.YELLOW)
                .append(profession.getAttachedLevel().getMaxLevel() + "").color(ChatColor.AQUA)
                .append("   |   ").color(ChatColor.GREEN).append("EXP: ").color(ChatColor.YELLOW)
                .append(profession.getAttachedLevel().getExp() + "").color(ChatColor.AQUA)
                .append("/").color(ChatColor.YELLOW)
                .append(profession.getAttachedLevel().getMaxExp() + "").color(ChatColor.AQUA).create())
        );
        List<Skill> skills = profession.getSkills().stream()
                .filter(Skill::isEnabled)
                .filter(skill -> !skill.isHidden())
                .sorted().collect(Collectors.toList());
        if (!skills.isEmpty()) {
            long firstColumnSize = Math.round(skills.size() / 2.0);
            int secondStartIndex = 0;
            if (firstColumnSize >= skills.size()) {
                firstColumnSize = skills.size();
            } else {
                secondStartIndex = (int) (firstColumnSize + 1);
            }
            for (int i = 0; i < firstColumnSize; i++) {
                ComponentBuilder msg = getSkillInfoInTooltip(skills.get(i), new ComponentBuilder(""), isTooltip);
                if (secondStartIndex > 0 && secondStartIndex < skills.size()) {
                    msg = getSkillInfoInTooltip(skills.get(secondStartIndex), msg.append("   |   ").color(ChatColor.DARK_PURPLE), isTooltip);
                    secondStartIndex++;
                }
                messages.addAll(Arrays.asList(msg.create()));
            }
        }
        return messages.toArray(new BaseComponent[0]);
    }

    private static ComponentBuilder getSkillInfoInTooltip(Skill skill, ComponentBuilder message, boolean isTooltip) {

        return message.append("[").color(skill.isHidden() ? ChatColor.GRAY : ChatColor.YELLOW)
                .append(skill.getRequiredLevel() + "").color(skill.isUnlocked() ? ChatColor.GREEN : ChatColor.DARK_RED)
                .append("]").color(skill.isHidden() ? ChatColor.GRAY : ChatColor.YELLOW)
                .append(" ").append(skill.getFriendlyName())
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, isTooltip ? new BaseComponent[0] : SkillUtil.getSkillTooltip(skill, true)))
                .color(skill.isUnlocked() ? (skill.isHidden() ? ChatColor.DARK_GRAY : ChatColor.GREEN)
                        : (skill.isHidden() ? ChatColor.GRAY : ChatColor.DARK_RED));
    }

    public static double getProfessionChangeCost(Profession profession) {

        SkillsPlugin.LocalConfiguration commonConfig = RaidCraft.getComponent(SkillsPlugin.class).getCommonConfig();
        return commonConfig.profession_change_cost +
                (commonConfig.profession_change_level_modifier * profession.getAttachedLevel().getLevel());
    }
}
