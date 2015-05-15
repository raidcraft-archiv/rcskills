package de.raidcraft.skills.api.ui;

import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.hero.Hero;

/**
 * @author Silthus
 */
public interface UserInterface {

    Hero getHero();

    void removeSidebarScore(String name);

    void updateSidebarScore(String name, int score);

    void addEffect(Effect effect, int duration);

    void renewEffect(Effect effect, int duration);

    void removeEffect(Effect effect);

    void refresh();
}
