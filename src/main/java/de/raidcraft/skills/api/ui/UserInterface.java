package de.raidcraft.skills.api.ui;

import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.OfflinePlayer;

/**
 * @author Silthus
 */
public interface UserInterface {

    public Hero getHero();

    public void removeSidebarScore(OfflinePlayer name);

    public void updateSidebarScore(OfflinePlayer name, int score);

    public void addEffect(Effect effect, int duration);

    public void renewEffect(Effect effect, int duration);

    public void removeEffect(Effect effect);

    public void refresh();
}
