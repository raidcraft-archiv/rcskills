package de.raidcraft.skills.api.requirement;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.profession.Profession;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public class ProfessionLevelRequirement extends LevelRequirement<Profession> {

    public ProfessionLevelRequirement(Level<Profession> type, int requiredLevel) {

        super(type, requiredLevel);
    }

    public ProfessionLevelRequirement(Level<Profession> type) {

        super(type);
    }

    @Override
    public String getReason(Hero hero) {

        return ChatColor.RED + "Du musst erst " +
                (getLevelObject().getProperties().isPrimary() ? "die Klasse" : "den Beruf") +
                ChatColor.AQUA + getLevelObject() + ChatColor.RED + " auf Level "
                + ChatColor.AQUA + getRequiredLevel() + ChatColor.AQUA + " bringen.";
    }
}