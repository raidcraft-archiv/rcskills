package de.raidcraft.skills.api.ui;

import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

/**
 * @author Silthus
 */
public class RefreshingEffectDisplay extends RefreshingDisplay {

    private final Effect effect;

    public RefreshingEffectDisplay(Effect effect, UserInterface userInterface, int duration) {

        super(userInterface, duration);
        this.effect = effect;
    }

    @Override
    public OfflinePlayer getScoreName() {

        ChatColor color = ChatColor.DARK_GRAY;
        if (getEffect().isOfType(EffectType.HELPFUL)) {
            color = ChatColor.GREEN;
        } else if (getEffect().isOfType(EffectType.HARMFUL)) {
            color = ChatColor.RED;
        }
        String name = color + getEffect().getFriendlyName();
        if (name.length() > 15) {
            name = name.substring(0, 15);
        }
        // TODO: catch nullpointer
        return Bukkit.getOfflinePlayer(UUIDUtil.convertPlayer(name));
    }

    public Effect getEffect() {

        return effect;
    }
}
