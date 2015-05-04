package de.raidcraft.skills.util;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.util.StringUtil;
import de.raidcraft.api.action.requirement.Reasonable;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public final class SkillUtil {

    private SkillUtil() {

    }

    public static Skill getSkillFromArgs(Hero hero, String input) throws CommandException {

        List<Skill> foundSkills = new ArrayList<>();
        input = StringUtils.formatName(input);
        // lets try to query for an id input first
        int id = -1;
        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException ignored) {
        }
        for (Skill skill : hero.getSkills()) {
            if (id > 0) {
                if (skill.getId() == id) {
                    foundSkills.add(skill);
                    break;
                }
            } else if (skill.getName().startsWith(input)
                    || StringUtils.formatName(skill.getFriendlyName()).startsWith(input)) {
                foundSkills.add(skill);
            }
        }

        if (foundSkills.size() < 1) {
            throw new CommandException("Du kennst keinen Skill mit dem Namen: " + input);
        }

        if (foundSkills.size() > 1) {
            // check if a skills matches exactly
            for (Skill skill : foundSkills) {
                if (skill.getName().equals(input) || StringUtils.formatName(skill.getFriendlyName()).equals(input)) {
                    return skill;
                }
            }
            throw new CommandException(
                    "Es gibt mehrere Skills mit dem Namen: " + input + " - " + StringUtil.joinString(foundSkills, ", ", 0));
        }

        return foundSkills.get(0);
    }

    public static String formatHeader(Skill skill) {

        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.YELLOW).append("------- [").append(skill.getProfession().isActive() ? ChatColor.GREEN : ChatColor.RED);
        sb.append(skill.getProfession().getProperties().getTag());
        sb.append(ChatColor.YELLOW).append("] ").append(skill.isUnlocked() ? ChatColor.AQUA : ChatColor.RED).append(skill.getFriendlyName());
        sb.append(ChatColor.YELLOW).append("[").append(ChatColor.GRAY).append("ID:").append(skill.getId()).append(ChatColor.YELLOW).append("]");
        sb.append(ChatColor.YELLOW).append(" -------");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public static List<String> formatBody(Skill skill) {

        List<String> body = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.GRAY).append(ChatColor.ITALIC).append(skill.getDescription());
        body.add(sb.toString());

        if (skill.isLevelable()) {
            sb = new StringBuilder();
            sb.append(ChatColor.YELLOW).append("Level: ").append(ChatColor.AQUA).append(((Levelable) skill).getAttachedLevel().getLevel())
                    .append(ChatColor.YELLOW).append("/").append(ChatColor.AQUA).append(((Levelable) skill).getAttachedLevel().getMaxLevel());
            sb.append(ChatColor.YELLOW).append("  |   EXP: ").append(ChatColor.AQUA).append(((Levelable) skill).getAttachedLevel().getExp())
                    .append(ChatColor.YELLOW).append("/").append(ChatColor.AQUA).append(((Levelable) skill).getAttachedLevel().getMaxExp());
            body.add(sb.toString());
        }

        for (Resource resource : skill.getHolder().getResources()) {
            double resourceCost = ((int) (skill.getTotalResourceCost(resource.getName()) * 100)) / 100.0;

            if (resourceCost == 0) {
                continue;
            }

            sb = new StringBuilder();
            sb.append(ChatColor.YELLOW).append("  - ");
            sb.append(ChatColor.AQUA);

            if (resourceCost < 0) {
                sb.append(ChatColor.GREEN).append("+");
            } else {
                sb.append(ChatColor.RED).append("-");
            }
            sb.append(resourceCost);

            sb.append(ChatColor.YELLOW).append(" ").append(resource.getFriendlyName());
            body.add(sb.toString());
        }

        if (skill.getRequirements().size() > 0) {
            sb = new StringBuilder();
            sb.append(ChatColor.YELLOW).append("Vorraussetzungen: \n");
            for (Requirement<Player> requirement : skill.getRequirements()) {
                if (requirement instanceof Reasonable) {
                    sb.append(ChatColor.YELLOW).append("  - ");
                    sb.append((requirement.test(skill.getHolder().getPlayer()) ? ChatColor.GREEN : ChatColor.RED));
                    sb.append(((Reasonable<Player>) requirement).getReason(skill.getHolder().getPlayer()));
                    sb.append("\n");
                }
            }
            body.add(sb.toString());
        }

        if (skill.getUsage().length > 0) {
            sb = new StringBuilder();
            sb.append(ChatColor.YELLOW).append("Zusatzinformationen: \n");
            for (String str : skill.getUsage()) {
                sb.append(ChatColor.YELLOW).append(str).append("\n");
            }
            body.add(sb.toString());
        }

        return body;
    }
}
