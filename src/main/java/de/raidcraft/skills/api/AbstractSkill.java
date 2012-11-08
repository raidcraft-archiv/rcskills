package de.raidcraft.skills.api;

import de.raidcraft.componentutils.database.Database;
import de.raidcraft.skills.tables.SkillsTable;

/**
 * @author Silthus
 */
public abstract class AbstractSkill implements Skill {

    private final int id;
    private String name;
    private String description;
    private String[] usage;
    private double cost;

    public AbstractSkill(int id) {

        this.id = id;
        load();
    }

    private void load() {

        SkillsTable.Data data = Database.getTable(SkillsTable.class).getSkillData(id);
        this.name = data.name;
        this.description = data.description;
        this.usage = data.usage;
        this.cost = data.cost;
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
    public String[] getUsage() {

        return usage;
    }

    @Override
    public double getCost() {

        return cost;
    }
}
