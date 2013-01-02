package de.raidcraft.skills.api.requirement;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public class SkillRequirement extends AbstractRequirement<Skill> {


    public SkillRequirement(Skill type) {

        super(type);
    }

    @Override
    public boolean isMet(Hero hero) {

        return hero.hasSkill(getType()) && getType().isUnlocked();
    }

    @Override
    public String getReason(Hero hero) {

        return ChatColor.RED + "Du benötigst den Skill " + ChatColor.AQUA + getType() + ChatColor.RED + ".";
    }
}
