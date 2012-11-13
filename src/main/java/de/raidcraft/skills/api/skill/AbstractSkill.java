package de.raidcraft.skills.api.skill;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.SkillsComponent;
import de.raidcraft.skills.tables.SkillsTable;

/**
 * @author Silthus
 */
public abstract class AbstractSkill implements Skill {

    private final int id;
    private String name;
    private String description;
    private String[] usage;
    private Type type;
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
        this.type = data.type;
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
    public Type getType() {

        return type;
    }

    @Override
    public boolean hasUsePermission(RCPlayer player) {

        return hasPermission(player,
                "rcskills.admin",
                "rcskills.use.*",
                "rcskills.use." + getId(),
                "rcskills.use." + getName().replace(" ", "_").toLowerCase());
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

    protected boolean hasPermission(RCPlayer player, String... permissions) {

        if (RaidCraft.getComponent(SkillsComponent.class).getLocalConfiguration().allow_op && player.isOp()) return true;
        for (String perm : permissions) {
            if (player.hasPermission(perm)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public double getCost() {

        return cost;
    }
}
