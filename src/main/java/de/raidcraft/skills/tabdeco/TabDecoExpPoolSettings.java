package de.raidcraft.skills.tabdeco;

import TCB.TabDeco.API.TabDecoSetting;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class TabDecoExpPoolSettings extends TabDecoSetting {

    private final SkillsPlugin plugin;

    public TabDecoExpPoolSettings(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Override
    public String getSlotText(Player player, String inputText, String settingName) {

        Hero hero = plugin.getCharacterManager().getHero(player);
        return String.valueOf(hero.getExpPool().getExp());
    }
}