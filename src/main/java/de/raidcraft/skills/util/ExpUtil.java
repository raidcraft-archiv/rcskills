package de.raidcraft.skills.util;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.AttachedLevel;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.util.EntityUtil;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public class ExpUtil {

    public static int getZD(int lvl) {

        if (lvl <= 7) {
            return 5;
        }
        if (lvl <= 9) {
            return 6;
        }
        if (lvl <= 11) {
            return 7;
        }
        if (lvl <= 15) {
            return 8;
        }
        if (lvl <= 19) {
            return 9;
        }
        if (lvl <= 29) {
            return 11;
        }
        if (lvl <= 39) {
            return 12;
        }
        if (lvl <= 44) {
            return 13;
        }
        if (lvl <= 49) {
            return 14;
        }
        if (lvl <= 54) {
            return 15;
        }
        if (lvl <= 59) {
            return 16;
        } else {
            return 17; // Approx.
        }
    }

    public static double getMobXP(int playerlvl, int moblvl) {

        if (moblvl >= playerlvl) {
            double temp = ((playerlvl * 5) + 45) * (1 + (0.05 * (moblvl - playerlvl)));
            double tempcap = ((playerlvl * 5) + 45) * 1.2;
            if (temp > tempcap) {
                return Math.floor(tempcap);
            } else {
                return Math.floor(temp);
            }
        } else {
            if (EntityUtil.getConColor(playerlvl, moblvl) == ChatColor.GRAY) {
                return 0;
            } else {
                return Math.floor((playerlvl * 5) + 45) * (1 - (playerlvl - moblvl) / getZD(playerlvl));
            }
        }
    }

    public static double getEliteMobXP(int playerlvl, int moblvl) {

        return getMobXP(playerlvl, moblvl) * 2;
    }

    // Rested Bonus:
    // Restedness is double XP, but if we only have part restedness we must split the XP:

    public static double getMobXPFull(int playerlvl, int moblvl, boolean elite, int rest) {
        // rest = how much XP is left before restedness wears off:
        double temp = 0;
        if (elite) {
            temp = getEliteMobXP(playerlvl, moblvl);
        } else {
            temp = getMobXP(playerlvl, moblvl);
        }
        // Now to apply restedness.  temp = actual XP.
        // If restedness is 0...
        if (rest == 0) {
            return temp;
        } else {
            if (rest >= temp) {
                return temp * 2;
            } else {
                //Restedness is partially covering the XP gained.
                // XP = rest + (AXP - (rest / 2))
                return rest + (temp - (rest / 2));
            }
        }
    }

    // Party Mob XP:
    public static double getPartyMobXPFull(int playerlvl, int highestlvl, int sumlvls, int moblvl, boolean elite, int rest) {

        double temp = getMobXPFull(highestlvl, moblvl, elite, 0);
        // temp = XP from soloing via highest lvl...
        temp = temp * playerlvl / sumlvls;
        if (rest == 0) {
            return temp;
        } else {
            if (rest >= temp) {
                return temp * 2;
            } else {
                //Restedness is partially covering the XP gained.
                // XP = rest + (AXP - (rest / 2))
                return rest + (temp - (rest / 2));
            }
        }
    }

    public static int getTotalExp(Hero hero) {

        int exp = hero.getExpPool().getExp();
        for (Profession profession : hero.getProfessions()) {
            AttachedLevel<Profession> level = profession.getAttachedLevel();
            exp += (level.getTotalNeededExpForLevel(level.getLevel()) - level.getExpToNextLevel());
        }
        for (Skill skill : hero.getSkills()) {
            if (skill instanceof Levelable) {
                AttachedLevel level = ((Levelable) skill).getAttachedLevel();
                exp += (level.getTotalNeededExpForLevel(level.getLevel()) - level.getExpToNextLevel());
            }
        }
        return exp;
    }
}
