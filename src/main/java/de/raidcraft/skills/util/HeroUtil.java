package de.raidcraft.skills.util;

import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public final class HeroUtil {

    private HeroUtil() {}

    public static String createManaBar(double mana, double maxMana) {

        StringBuilder manaBar = new StringBuilder(String.valueOf(ChatColor.RED) + "[" + ChatColor.BLUE);
        int percent = (int)((mana / maxMana) * 100.0);
        int progress = percent / 2;
        for (int i = 0; i < progress; i++) {
            manaBar.append('|');
        }
        manaBar.append(ChatColor.DARK_RED);
        for (int i = 0; i < 50 - progress; i++) {
            manaBar.append('|');
        }
        manaBar.append(ChatColor.RED).append(']');
        return String.valueOf(manaBar) + " - " + ChatColor.BLUE + percent + "%";
    }
}
