package de.raidcraft.skills.actionapi.requirements;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.requirement.ReasonableRequirement;
import de.raidcraft.skills.CharacterManager;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
public class LevelRequirement implements ReasonableRequirement<Player> {

    @Override
    @Information(
            value = "hero.level",
            desc = "Checks if the level of the hero/profession/skill is greater,equals or lower than the given level.",
            conf = {
                    "level: 1",
                    "modifier: [gt/<ge>/eq/le/lt]",
                    "type: <hero/skill/profession>",
                    "[skill]",
                    "[profession]"
            }
    )
    public boolean test(Player player, ConfigurationSection config) {

        Hero hero = RaidCraft.getComponent(CharacterManager.class).getHero(player);
        String type = config.getString("type", "hero");
        int currentLevel = 1;
        switch (type) {
            case "skill":
                try {
                    Skill skill;
                    if (config.isSet("profession")) {
                        Profession profession = hero.getProfession(config.getString("profession"));
                        skill = profession.getSkill(config.getString("skill"));
                    } else {
                        skill = hero.getSkill(config.getString("skill"));
                    }
                    if (skill instanceof Levelable) {
                        currentLevel = ((Levelable) skill).getAttachedLevel().getLevel();
                    } else {
                        return false;
                    }
                } catch (UnknownSkillException | UnknownProfessionException e) {
                    e.printStackTrace();
                }
                break;
            case "profession":
                try {
                    Profession profession = hero.getProfession(config.getString("profession"));
                    if (config.getBoolean("total-level", false)) {
                        currentLevel = profession.getTotalLevel();
                    } else {
                        currentLevel = profession.getAttachedLevel().getLevel();
                    }
                } catch (UnknownSkillException | UnknownProfessionException e) {
                    e.printStackTrace();
                }
                break;
            case "hero":
            default:
                currentLevel = hero.getPlayerLevel();
                break;
        }

        String modifier = config.getString("modifier", "ge");
        int level = config.getInt("level", 1);
        switch (modifier) {
            case "gt":
                return currentLevel > level;
            case "ge":
                return currentLevel >= level;
            case "le":
                return currentLevel <= level;
            case "lt":
                return currentLevel < level;
            case "eq":
            default:
                return currentLevel == level;
        }
    }

    @Override
    public String getReason(Player player, ConfigurationSection config) {

        Hero hero = RaidCraft.getComponent(CharacterManager.class).getHero(player);
        String type = config.getString("type", "hero");
        int requiredLevel = config.getInt("level", 1);
        switch (type) {
            case "skill":
                try {
                    Skill skill;
                    if (config.isSet("profession")) {
                        Profession profession = hero.getProfession(config.getString("profession"));
                        skill = profession.getSkill(config.getString("skill"));
                    } else {
                        skill = hero.getSkill(config.getString("skill"));
                    }
                    if (skill instanceof Levelable) {
                        return skill.getFriendlyName() + " auf Level " + requiredLevel + " benötigt.";
                    } else {
                        return "Der Skill " + skill.getFriendlyName() + " ist nicht Levelbar.";
                    }
                } catch (UnknownSkillException | UnknownProfessionException e) {
                    return "Der Skill " + config.getString("skill") + " exisistiert nicht!";
                }
            case "profession":
                try {
                    return hero.getProfession(config.getString("profession")).getFriendlyName() + " Spezialisierung auf Level " + requiredLevel + " benötigt.";
                } catch (UnknownSkillException | UnknownProfessionException e) {
                    return "Die Spezialisierung " + config.getString("profession") + " existiert nicht.";
                }
            case "hero":
            default:
                return "Level " + requiredLevel + " benötigt.";
        }
    }
}
