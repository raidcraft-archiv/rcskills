package de.raidcraft.skills.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
@QuestType.Name("hero")
public class HeroQuestType implements QuestType {

    @Method(name = "addxp", type = Type.ACTION)
    public static void addExp(Player player, ConfigurationSection data) throws QuestException {

        SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);
        Hero hero = plugin.getCharacterManager().getHero(player);
        hero.getExpPool().addExp(data.getInt("exp"));
    }
}
