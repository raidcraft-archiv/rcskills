package de.raidcraft.skills;

import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.AbstractLevelable;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.Obtainable;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a profession instantiated for one {@link SkilledPlayer}.
 * Each single {@link RCPlayer} can have multiple {@link Profession}s at a time. The
 * {@link SkilledPlayer} will hold a reference to all {@link PlayerProfession}s obtained by the player.
 * And the {@link PlayerProfession} will hold a single reference to the corresponding {@link Profession}.
 *
 * This class also keeps track of the {@link Skill}s that the player can buy or gain. All {@link Obtainable.Type} gained
 * or bought skills are stored in the {@link SkilledPlayer}.
 *
 * @author Silthus
 */
public class PlayerProfession extends AbstractLevelable {

    private final RCPlayer player;
    private final Profession profession;
    private final Map<Obtainable.Type, Collection<Skill>> obtainableSkills = new HashMap<>();
    // holds the time the player gained this profession
    private Timestamp timestamp;

    public PlayerProfession(RCPlayer player, Profession profession) {

        this.player = player;
        this.profession = profession;
        load();
    }

    private void load() {

        // TODO: implement
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

    public Collection<Skill> getBuyableSkills() {

        return obtainableSkills.get(Obtainable.Type.BUYABLE);
    }

    public Collection<Skill> getGainableSkills() {

        return obtainableSkills.get(Obtainable.Type.GAINABLE);
    }
}
