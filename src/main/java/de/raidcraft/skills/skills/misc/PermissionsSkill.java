package de.raidcraft.skills.skills.misc;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillData;
import de.raidcraft.skills.api.skill.AbstractSkill;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.util.DataMap;

import java.util.Collection;

/**
 * @author Silthus
 */
@SkillInformation(
        name = "permission-skill",
        desc = "Represents a generic permissions skill.",
        defaults = {"groups:[foobar,barfoo]", "permissions:[foo.bar,bar.foo]"},
        types = {Skill.Type.UNBINDABLE}
)
public class PermissionsSkill extends AbstractSkill {

    private Collection<String> groups;
    private Collection<String> permissions;

    public PermissionsSkill(Hero hero, SkillData data) {

        super(hero, data);
    }

    @Override
    public void load(DataMap data) {

        groups = data.getStringList("groups");
        permissions = data.getStringList("permissions");
    }

    public Collection<String> getGroups() {

        return groups;
    }

    public Collection<String> getPermissions() {

        return permissions;
    }
}
