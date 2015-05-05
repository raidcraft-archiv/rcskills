package de.raidcraft.skills.util;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.util.StringUtil;
import de.raidcraft.api.requirement.Requirement;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.skill.Skill;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
            for (Requirement<Hero> requirement : skill.getRequirements()) {
                sb.append(ChatColor.YELLOW).append("  - ");
                sb.append((requirement.isMet(skill.getHolder()) ? ChatColor.GREEN : ChatColor.RED));
                sb.append(requirement.getShortReason());
                sb.append("\n");
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

    public static List<FancyMessage> getSkillTooltip(Skill skill) {

        List<FancyMessage> messages = new ArrayList<>();
        messages.add(new FancyMessage("-------").color(ChatColor.YELLOW)
                .then(" [").color(ChatColor.BLACK)
                .then(skill.getRequiredLevel() + "").color(ChatColor.AQUA)
                .then("] ").color(ChatColor.BLACK)
                .then(skill.getFriendlyName()).color(skill.isUnlocked() ? ChatColor.GREEN : ChatColor.DARK_RED)
                .then(" (").color(ChatColor.YELLOW)
                .then(skill.getProfession().getProperties().getTag()).color(ChatColor.GOLD)
                .formattedTooltip(ProfessionUtil.getProfessionTooltip(skill.getProfession()))
                .then(")").color(ChatColor.YELLOW)
                .then(" -------").color(ChatColor.YELLOW));

        if (skill instanceof Levelable) {
            messages.add(new FancyMessage("Level: ").color(ChatColor.YELLOW)
                            .then(((Levelable) skill).getAttachedLevel().getLevel() + "").color(ChatColor.AQUA)
                            .then("/").color(ChatColor.YELLOW)
                            .then(((Levelable) skill).getAttachedLevel().getMaxLevel() + "").color(ChatColor.AQUA)
                            .then("\t|\t").color(ChatColor.GREEN).then("EXP: ").color(ChatColor.YELLOW)
                            .then(((Levelable) skill).getAttachedLevel().getExp() + "").color(ChatColor.AQUA)
                            .then("/").color(ChatColor.YELLOW)
                            .then(((Levelable) skill).getAttachedLevel().getMaxExp() + "").color(ChatColor.AQUA)
            );
        }

        Set<Resource> resources = skill.getHolder().getResources();
        if (!resources.isEmpty()) {
            messages.add(new FancyMessage("Skill Kosten:").color(ChatColor.YELLOW));
        }
        for (Resource resource : resources) {
            double resourceCost = ((int) (skill.getTotalResourceCost(resource.getName()) * 100)) / 100.0;

            if (resourceCost == 0) {
                continue;
            }

            FancyMessage msg = new FancyMessage("  - ").color(ChatColor.YELLOW);
            if (resourceCost < 0) {
                msg.then("+" + resourceCost).color(ChatColor.GREEN);
            } else {
                msg.then("-" + resourceCost).color(ChatColor.DARK_RED);
            }
            msg.then(" ").then(resource.getFriendlyName()).color(ChatColor.YELLOW);
            messages.add(msg);
        }
//
//        if (skill.getRequirements().size() > 0) {
//            messages.add(new FancyMessage("Vorraussetzungen:").color(ChatColor.YELLOW));
//            for (Requirement<Hero> requirement : skill.getRequirements()) {
//                sb.append(ChatColor.YELLOW).append("  - ");
//                sb.append((requirement.isMet(skill.getHolder()) ? ChatColor.GREEN : ChatColor.RED));
//                sb.append(requirement.getShortReason());
//                sb.append("\n");
//            }
//            body.add(sb.toString());
//        }

        if (skill.getUsage().length > 0) {
            messages.add(new FancyMessage("Zusatzinformationen:").color(ChatColor.YELLOW));
            for (String str : skill.getUsage()) {
                messages.add(new FancyMessage(str).color(ChatColor.YELLOW));
            }
        }

        return messages;
    }
}
