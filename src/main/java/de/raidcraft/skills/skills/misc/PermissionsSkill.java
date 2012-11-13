package de.raidcraft.skills.skills.misc;

import de.raidcraft.api.database.Database;
import de.raidcraft.skills.api.skill.AbstractSkill;
import de.raidcraft.skills.tables.PermissionSkillsTable;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Silthus
 */
public class PermissionsSkill extends AbstractSkill {

    private Collection<String> groups;
    private Collection<String> permissions;

    public PermissionsSkill(int id) {

        super(id);
        load();
    }

    private void load() {

        PermissionSkillsTable.Data data = Database.getTable(PermissionSkillsTable.class).getPermissionsData(getId());
        if (data == null) {
            groups = new HashSet<>();
            permissions = new HashSet<>();
            return;
        }
        this.groups = data.groups;
        this.permissions = data.permissions;
    }

    public Collection<String> getGroups() {

        return groups;
    }

    public Collection<String> getPermissions() {

        return permissions;
    }
}
