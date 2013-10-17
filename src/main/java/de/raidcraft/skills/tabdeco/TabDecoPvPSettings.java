package de.raidcraft.skills.tabdeco;

import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.util.HeroUtil;
import de.raidcraft.tabdeco.api.TabDecoSetting;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class TabDecoPvPSettings extends TabDecoSetting {

    private final SkillsPlugin plugin;

    public TabDecoPvPSettings(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Override
    public String getSlotText(Player player, String inputText, String settingName) {

        Hero hero = plugin.getCharacterManager().getHero(player);
        return HeroUtil.getPvPTag(hero);
    }
}
