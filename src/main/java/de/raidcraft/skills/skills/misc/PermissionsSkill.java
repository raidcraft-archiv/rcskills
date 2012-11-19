package de.raidcraft.skills.skills.misc;

import de.raidcraft.skills.api.persistance.SkillData;
import de.raidcraft.skills.api.skill.AbstractSkill;
import de.raidcraft.skills.api.skill.SkillInformation;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Silthus
 */
@SkillInformation(
        name = "permission-skill",
        desc = "Represents a generic permissions skill."
)
public class PermissionsSkill extends AbstractSkill {

    private Collection<String> groups;
    private Collection<String> permissions;

    public PermissionsSkill(int id, SkillData data) {

        super(id, data);
        if (data == null) {
            groups = new HashSet<>();
            permissions = new HashSet<>();
            return;
        }
        this.groups = data.getStringList("groups");
        this.permissions = data.getStringList("permissions");
    }

    public Collection<String> getGroups() {

        return groups;
    }

    public Collection<String> getPermissions() {

        return permissions;
    }
}
