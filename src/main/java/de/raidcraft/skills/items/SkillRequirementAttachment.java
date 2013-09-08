package de.raidcraft.skills.items;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.api.items.attachments.RequiredItemAttachment;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class SkillRequirementAttachment implements RequiredItemAttachment {

    private final SkillsPlugin plugin;
    private String skillName;
    private int skillLevel;

    public SkillRequirementAttachment() {

        this.plugin = RaidCraft.getComponent(SkillsPlugin.class);
    }

    @Override
    public boolean isRequirementMet(Player player) {

        try {
            Hero hero = plugin.getCharacterManager().getHero(player);
            if (hero.hasSkill(skillName)) {
                return false;
            }
            Skill skill = hero.getSkill(skillName);
            return !(skillLevel > 0 && skill instanceof Levelable)
                    || ((Levelable) skill).getAttachedLevel().getLevel() >= skillLevel;
        } catch (UnknownSkillException ignored) {
        }
        return false;
    }

    @Override
    public String getItemText(Player player) {

        try {
            Skill skill = plugin.getCharacterManager().getHero(player).getSkill(skillName);
            String msg = skill.getFriendlyName();
            if (skillLevel > 0 && skill instanceof Levelable) {
                msg += " Level " + skillLevel;
            }
            return msg;
        } catch (UnknownSkillException ignored) {
        }
        return null;
    }

    @Override
    public String getErrorMessage(Player player) {

        try {
            Skill skill = plugin.getCharacterManager().getHero(player).getSkill(skillName);
            String msg = "Benötigt " + skill.getFriendlyName();
            if (skillLevel > 0 && skill instanceof Levelable) {
                msg += " auf Level " + skillLevel;
            }
            msg += ".";
            return msg;
        } catch (UnknownSkillException e) {
            return e.getMessage();
        }
    }

    @Override
    public void applyAttachment(CustomItemStack item, Player player, ConfigurationSection args) throws CustomItemException {

        this.skillName = args.getString("skill");
        this.skillLevel = args.getInt("level");
    }

    @Override
    public void removeAttachment(CustomItemStack item, Player player, ConfigurationSection args) throws CustomItemException {


    }
}
