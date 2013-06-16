package de.raidcraft.skills.tabdeco;

import TCB.TabDeco.API.TabDecoSetting;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.economy.Economy;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.util.CustomItemUtil;
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
    public String getSlotText(Player player, String s, String s2) {

        Economy economy = RaidCraft.getEconomy();
        if (economy != null) {
            return CustomItemUtil.getSellPriceString(economy.getBalance(player.getName()), null);
        }
        // return random unique string
        return ChatColor.GREEN + "" + ChatColor.AQUA + ChatColor.ITALIC + ChatColor.BOLD + ChatColor.AQUA;
    }
}
