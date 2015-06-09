package de.raidcraft.skills.api.ui;

import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.Effect;
import org.bukkit.ChatColor;

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
    public String getScoreName() {

        ChatColor color = ChatColor.DARK_GRAY;
        if (getEffect().isOfType(EffectType.HELPFUL)) {
            color = ChatColor.GREEN;
        } else if (getEffect().isOfType(EffectType.HARMFUL)) {
            color = ChatColor.RED;
        }
        String name = color + "";
        if (getEffect().getStacks() > 0) {
            name += getEffect().getStacks() + "x ";
        }
        name += getEffect().getFriendlyName();
        if (name.length() > 15) {
            name = name.substring(0, 15);
        }
        return name;
    }

    public Effect getEffect() {

        return effect;
    }
}
