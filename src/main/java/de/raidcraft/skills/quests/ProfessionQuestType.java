package de.raidcraft.skills.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.api.quests.QuestType;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
@QuestType.Name("profession")
public class ProfessionQuestType implements QuestType {

    @Method(name = "level", type = Type.REQUIREMENT)
    public static boolean isLevel(Player player, ConfigurationSection data) throws QuestException {

        try {
            SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);
            Hero hero = plugin.getCharacterManager().getHero(player);
            Profession profession = plugin.getProfessionManager().getProfession(hero, data.getString("profession"));
            return profession.getAttachedLevel().getLevel() == data.getInt("level");
        } catch (UnknownSkillException | UnknownProfessionException e) {
            throw new QuestException(e.getMessage());
        }
    }

    @Method(name = "minlevel", type = Type.REQUIREMENT)
    public static boolean isMinlevel(Player player, ConfigurationSection data) throws QuestException {

        try {
            SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);
            Hero hero = plugin.getCharacterManager().getHero(player);
            Profession profession = plugin.getProfessionManager().getProfession(hero, data.getString("profession"));
            return profession.getAttachedLevel().getLevel() >= data.getInt("level");
        } catch (UnknownSkillException | UnknownProfessionException e) {
            throw new QuestException(e.getMessage());
        }
    }

    @Method(name = "addxp", type = Type.ACTION)
    public static void addExp(Player player, ConfigurationSection data) throws QuestException {

        try {
            SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);
            Hero hero = plugin.getCharacterManager().getHero(player);
            Profession profession = plugin.getProfessionManager().getProfession(hero, data.getString("profession"));
            profession.getAttachedLevel().addExp(data.getInt("exp"));
        } catch (UnknownSkillException | UnknownProfessionException e) {
            throw new QuestException(e.getMessage());
        }
    }

    @Method(name = "addlevel", type = Type.ACTION)
    public static void addLevel(Player player, ConfigurationSection data) throws QuestException {

        try {
            SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);
            Hero hero = plugin.getCharacterManager().getHero(player);
            Profession profession = plugin.getProfessionManager().getProfession(hero, data.getString("profession"));
            profession.getAttachedLevel().addLevel(data.getInt("level"));
        } catch (UnknownSkillException | UnknownProfessionException e) {
            throw new QuestException(e.getMessage());
        }
    }
}
