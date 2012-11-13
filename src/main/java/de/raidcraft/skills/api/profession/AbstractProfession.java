package de.raidcraft.skills.api.profession;

import de.raidcraft.api.database.Database;
import de.raidcraft.skills.api.AbstractLevelable;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.tables.professions.ProfessionsTable;

import java.util.Collection;

/**
 * @author Silthus
 */
public abstract class AbstractProfession extends AbstractLevelable implements Profession {

    private final int id;
    private String name;
    private String description;
    private Collection<Skill> skills;

    protected AbstractProfession(int id) throws UnknownProfessionException {

        this.id = id;
        load(Database.getTable(ProfessionsTable.class).getProfessionData(id));
    }

    private void load(ProfessionsTable.Data data) {

        this.name = data.name;
        this.description = data.description;
        this.skills = data.skills;
        // TODO: maybe load some infos about the skill requirements here
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getDescription() {

        return description;
    }

    @Override
    public Collection<Skill> getSkills() {

        return skills;
    }

    @Override
    public boolean canPlayerObtainSkill(Skill skill) {

        //TODO: check special skill -> profession requirements like level of the profession
        return false;
    }
}
