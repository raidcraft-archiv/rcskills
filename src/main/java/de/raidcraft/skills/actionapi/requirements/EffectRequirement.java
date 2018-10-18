package de.raidcraft.skills.actionapi.requirements;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.skills.CharacterManager;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class EffectRequirement implements Requirement<Player> {

    @Information(
            value = "effect",
            desc = "Checks if the player has an active effect with the given name.",
            conf = {
                    "effect: name of the effect"
            }
    )
    @Override
    public boolean test(Player player, ConfigurationSection config) {

        Hero hero = RaidCraft.getComponent(CharacterManager.class).getHero(player);
        if (hero == null) return false;
        return hero.getEffect(config.getString("effect")).isPresent();
    }
}
