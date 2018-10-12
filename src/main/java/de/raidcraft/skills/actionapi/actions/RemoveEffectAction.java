package de.raidcraft.skills.actionapi.actions;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@Data
public class RemoveEffectAction implements Action<Player> {

    private final SkillsPlugin plugin;

    @Information(
            value = "effect.remove",
            desc = "Removes an effect applied to the player.",
            conf = {
                    "effect: name of the effect to remove"
            }
    )
    @Override
    public void accept(Player player, ConfigurationSection config) {

        Hero hero = getPlugin().getCharacterManager().getHero(player);
        if (hero == null) return;
        hero.getEffect(config.getString("effect"))
                .ifPresent(effect -> {
                    try {
                        hero.removeEffect(effect);
                    } catch (CombatException e) {
                        player.sendMessage(ChatColor.RED + e.getMessage());
                        getPlugin().warning(e.getMessage());
                    }
                });
    }
}
