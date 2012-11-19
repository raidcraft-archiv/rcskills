package de.raidcraft.skills.api.skill;

import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.Obtainable;
import de.raidcraft.skills.api.persistance.SkillData;
import de.raidcraft.skills.api.profession.Profession;

import java.util.Collection;

/**
 * @author Silthus
 */
public abstract class AbstractObtainableSkill extends AbstractSkill implements Obtainable {

    private Type type;
    private double cost;
    private int neededLevel;
    private Collection<Profession> professions;
    private boolean allProfessionsRequired;

    public AbstractObtainableSkill(int id) {

        super(id);
    }

    @Override
    protected void load(SkillData data) {

        super.load(data);
        this.type = data.type;
        this.cost = data.cost;
        this.neededLevel = data.neededLevel;
        this.professions = data.professions;
        this.allProfessionsRequired = data.allProfessionsRequired;
    }

    @Override
    public Type getType() {

        return type;
    }

    @Override
    public double getCost() {

        return cost;
    }

    @Override
    public int getNeededLevel() {

        return neededLevel;
    }

    @Override
    public Collection<Profession> getNeededProfessions() {

        return professions;
    }

    @Override
    public boolean areAllProfessionsRequired() {

        return allProfessionsRequired;
    }

    @Override
    public boolean hasBuyPermission(RCPlayer player) {

        return hasPermission(player,
                "rcskills.admin",
                "rcskills.buy.*",
                "rcskills.buy." + getId(),
                "rcskills.buy." + getName().replace(" ", "_").toLowerCase());
    }

    @Override
    public boolean hasGainPermission(RCPlayer player) {

        return hasPermission(player,
                "rcskills.admin",
                "rcskills.gain.*",
                "rcskills.gain." + getId(),
                "rcskills.gain." + getName().replace(" ", "_").toLowerCase());
    }
}
