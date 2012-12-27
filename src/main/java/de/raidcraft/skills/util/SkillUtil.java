package de.raidcraft.skills.util;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.util.StringUtil;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public final class SkillUtil {

    private SkillUtil() {}

    public static Skill getSkillFromArgs(Hero hero, String input) throws CommandException {

        List<Skill> foundSkills = new ArrayList<>();
        input = StringUtils.formatName(input);
        for (Skill skill : hero.getSkills()) {
            if (skill.getName().contains(input)
                    || StringUtils.formatName(skill.getFriendlyName()).contains(input)) {
                foundSkills.add(skill);
            }
        }

        if (foundSkills.size() < 1) {
            throw new CommandException("Du kennst keinen Skill mit dem Namen: " + input);
        }

        if (foundSkills.size() > 1) {
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
        sb.append(ChatColor.YELLOW).append(" -------");
        return sb.toString();
    }

    public static String[] formatBody(Skill skill) {

        String[] body = new String[3];
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.YELLOW).append(skill.getProfession().getProperties().isPrimary() ? "Klassen Level: " : "Beruf Level: ");
        sb.append(ChatColor.AQUA).append(skill.getProfession().getLevel().getLevel());
        if (skill instanceof Levelable) {
            sb.append(ChatColor.YELLOW).append("  |   Skill Level: ").append(ChatColor.AQUA).append(((Levelable) skill).getLevel().getLevel());
        }
        body[0] = sb.toString();

        sb = new StringBuilder();
        sb.append(ChatColor.YELLOW).append(skill.getProfession().getProperties().isPrimary() ? "Klassen Min. Level: " : "Beruf Min. Level: ");
        sb.append(ChatColor.AQUA).append(skill.getProperties().getRequiredLevel());
        body[1] = sb.toString();

        sb = new StringBuilder();
        if (skill.getTotalManaCost() > 0) {
            sb.append(ChatColor.YELLOW).append("MP: ").append(ChatColor.AQUA).append(skill.getTotalManaCost());
        }
        if (skill.getTotalStaminaCost() > 0) {
            sb.append(ChatColor.YELLOW).append("  |   SP: ").append(ChatColor.AQUA).append(skill.getTotalStaminaCost());
        }
        if (skill.getTotalHealthCost() > 0) {
            sb.append(ChatColor.YELLOW).append("  |   HP: ").append(ChatColor.AQUA).append(skill.getTotalHealthCost());
        }
        body[2] = sb.toString();

        sb = new StringBuilder();

        return body;
    }
}
