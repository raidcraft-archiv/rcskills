package de.raidcraft.skills.tabdeco;

import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.tabdeco.api.TabDecoSetting;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class TabDecoArmorSettings extends TabDecoSetting {

    private final SkillsPlugin plugin;

    public TabDecoArmorSettings(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Override
    public String getSlotText(Player player, String inputText, String settingName) {

        Hero hero = plugin.getCharacterManager().getHero(player);
        return String.valueOf(hero.getTotalArmorValue());
    }
}
