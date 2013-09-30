package de.raidcraft.skills.items;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.api.items.attachments.RequiredItemAttachment;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class SkillRequirementAttachment implements RequiredItemAttachment {

    private final SkillsPlugin plugin;
    private Skill skill;
    private String skillName;
    private int requiredLevel;

    public SkillRequirementAttachment() {

        this.plugin = RaidCraft.getComponent(SkillsPlugin.class);
    }

    @Override
    public void loadAttachment(ConfigurationSection data) {

        this.skillName = data.getString("skill");
        this.requiredLevel = data.getInt("level", 1);
    }

    @Override
    public String getName() {

        return "skill";
    }

    @Override
    public boolean isRequirementMet(Player player) {

        return !skill.getHolder().hasSkill(skill)
                && (!(requiredLevel > 0
                && skill instanceof Levelable)
                || ((Levelable) skill).getAttachedLevel().getLevel() >= requiredLevel);
    }

    @Override
    public String getItemText() {

        String msg = skill.getFriendlyName();
        if (requiredLevel > 0 && skill instanceof Levelable) {
            msg += " Level " + requiredLevel;
        }
        return msg;
    }

    @Override
    public String getErrorMessage() {

        String msg = "Benötigt " + skill.getFriendlyName();
        if (requiredLevel > 0 && skill instanceof Levelable) {
            msg += " auf Level " + requiredLevel;
        }
        msg += ".";
        return msg;
    }

    @Override
    public void applyAttachment(Player player) throws CustomItemException {

        try {
            skill = plugin.getCharacterManager().getHero(player).getSkill(skillName);
        } catch (UnknownSkillException e) {
            throw new CustomItemException(e.getMessage());
        }
    }

    @Override
    public void removeAttachment(Player player) throws CustomItemException {


    }
}
