package de.raidcraft.skills.tabdeco;

import TCB.TabDeco.API.TabDecoSetting;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.economy.Economy;
import de.raidcraft.skills.SkillsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class TabDecoEconomySetting extends TabDecoSetting {

    private final SkillsPlugin plugin;

    public TabDecoEconomySetting(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Override
    public String getSlotText(Player player, String inputText, String settingName) {

        Economy economy = RaidCraft.getEconomy();
        if (economy != null) {

            if (settingName.equalsIgnoreCase("rcMoneyGold")) {
                return String.valueOf((int) economy.getBalance(player.getName()) / 100);
            } else if (settingName.equalsIgnoreCase("rcMoneySilver")) {
                return String.valueOf((int) economy.getBalance(player.getName()) % 100);
            } else if (settingName.equalsIgnoreCase("rcMoneyCopper")) {
                return String.valueOf((int) ((economy.getBalance(player.getName()) * 100.0) % 100));
            }
        }
        // return random unique string
        return ChatColor.GREEN + "" + ChatColor.AQUA + ChatColor.ITALIC + ChatColor.BOLD + ChatColor.AQUA;
    }
}
