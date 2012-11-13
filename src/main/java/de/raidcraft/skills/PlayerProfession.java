package de.raidcraft.skills;

import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.AbstractLevelable;
import de.raidcraft.skills.api.profession.Profession;

import java.sql.Timestamp;

/**
 * Represents a profession instantiated for one {@link SkilledPlayer}.
 * Each single {@link RCPlayer} can have multiple {@link Profession}s at a time. The
 * {@link SkilledPlayer} will hold a reference to all {@link PlayerProfession}s obtained by the player.
 * And the {@link PlayerProfession} will hold a single reference to the corresponding {@link Profession}.
 *
 * @author Silthus
 */
public class PlayerProfession extends AbstractLevelable {

    private final RCPlayer player;
    private final Profession profession;
    // holds the time the player gained this profession
    private Timestamp timestamp;

    public PlayerProfession(RCPlayer player, Profession profession) {

        this.player = player;
        this.profession = profession;
        load();
    }

    private void load() {


    }

    @Override
    protected void loadLevel() {
        //TODO: implement
    }

    public RCPlayer getPlayer() {

        return player;
    }

    public Profession getProfession() {

        return profession;
    }

    @Override
    public void saveLevelProgress() {
        //TODO: implement
    }
}
