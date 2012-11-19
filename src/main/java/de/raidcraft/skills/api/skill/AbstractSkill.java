package de.raidcraft.skills.api.skill;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.SkillsComponent;
import de.raidcraft.skills.api.persistance.SkillData;
import de.raidcraft.skills.tables.skills.SkillsTable;

import java.util.Collection;

/**
 * @author Silthus
 */
public abstract class AbstractSkill implements Skill {

    private final int id;
    private String name;
    private String description;
    private String[] usage;
    private Collection<Skill> strongParents;
    private Collection<Skill> weakParents;

    public AbstractSkill(int id) {

        this.id = id;
        load(Database.getTable(SkillsTable.class).getSkillData(id));
    }

    protected void load(SkillData data) {

        this.name = data.getName();
        this.description = data.getDescription();
        this.usage = data.getUsage();
        this.strongParents = data.getStrongParents();
        this.weakParents = data.getWeakParents();
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
    public boolean hasUsePermission(RCPlayer player) {

        return hasPermission(player,
                "rcskills.admin",
                "rcskills.use.*",
                "rcskills.use." + getId(),
                "rcskills.use." + getName().replace(" ", "_").toLowerCase());
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
    public Collection<Skill> getStrongParents() {

        return strongParents;
    }

    @Override
    public Collection<Skill> getWeakParents() {

        return weakParents;
    }

    @Override
    public String toString() {

        return "[S" + getId() + "-" + getClass().getName() + "]" + getName();
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Skill && ((Skill) obj).getId() == getId();
    }
}
