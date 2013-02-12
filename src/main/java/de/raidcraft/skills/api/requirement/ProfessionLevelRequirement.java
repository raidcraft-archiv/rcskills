package de.raidcraft.skills.api.requirement;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public class ProfessionLevelRequirement extends LevelRequirement<Profession> {

    public ProfessionLevelRequirement(Profession type, int requiredLevel) {

        super(type, requiredLevel);
    }

    public ProfessionLevelRequirement(Profession type) {

        super(type);
    }

    @Override
    public String getLongReason(Hero hero) {

        return ChatColor.RED + "Du musst erst deine " + getType().getPath().getFriendlyName() + " Spezialisierung " +
                ChatColor.AQUA + getType() + ChatColor.RED + " auf " + ChatColor.AQUA + "Level "
                + getRequiredLevel() + ChatColor.RED + " bringen.";
    }

    @Override
    public String getShortReason(Hero hero) {

        return getType().getPath().getFriendlyName() + " Spezialisierung " + getType() + " auf Level " + getRequiredLevel();
    }
}
