package de.raidcraft.skills.actionapi.requirements;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.skills.CharacterManager;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ProfessionRequirement implements Requirement<Player> {

    @Information(
            value = "hero.class",
            desc = "Checks if the player has the given class or any if none specified.",
            conf = {
                    "class: class to check or empty for any class"
            }
    )
    @Override
    public boolean test(Player player, ConfigurationSection config) {

        Hero hero = RaidCraft.getComponent(CharacterManager.class).getHero(player);
        if (hero == null) return false;
        Profession virtualProfession = hero.getVirtualProfession();
        if (config.isSet("class")) {
            return hero.getProfessions().stream().anyMatch(prof -> !prof.equals(virtualProfession));
        }
        return hero.hasProfession(config.getString("class"));
    }
}
