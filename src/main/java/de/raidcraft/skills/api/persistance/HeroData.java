package de.raidcraft.skills.api.persistance;

import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.profession.Profession;

/**
 * @author Silthus
 */
public abstract class HeroData {

    protected Profession selectedProfession;
    protected RCPlayer player;

    public Profession getSelectedProfession() {

        return selectedProfession;
    }

    public RCPlayer getPlayer() {

        return player;
    }
}
