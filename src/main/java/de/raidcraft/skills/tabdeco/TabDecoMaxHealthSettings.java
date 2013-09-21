package de.raidcraft.skills.tabdeco;

import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.tabdeco.api.TabDecoSetting;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class TabDecoMaxHealthSettings extends TabDecoSetting {

    private final SkillsPlugin plugin;

    public TabDecoMaxHealthSettings(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Override
    public String getSlotText(Player player, String inputText, String settingName) {

        return String.valueOf(player.getMaxHealth());
    }
}
