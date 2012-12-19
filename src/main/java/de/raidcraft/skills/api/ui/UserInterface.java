package de.raidcraft.skills.api.ui;

import de.raidcraft.skills.api.hero.Hero;

/**
 * @author Silthus
 */
public interface UserInterface {

    public Hero getHero();

    public boolean isEnabled();

    public void setEnabled(boolean enabled);

    public void refresh();
}
