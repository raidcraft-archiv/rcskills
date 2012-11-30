package de.raidcraft.skills.skills.misc;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.AbstractSkill;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.util.DataMap;
import net.milkbowl.vault.permission.Permission;

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
public class PermissionSkill extends AbstractSkill {

    private Collection<String> groups;
    private Collection<String> permissions;
    private Collection<String> worlds;

    protected PermissionSkill(Hero hero, SkillProperties data, Profession profession, THeroSkill database) {

        super(hero, data, database, profession);
    }


    @Override
    public void load(DataMap data) {

        groups = data.getStringList("groups");
        permissions = data.getStringList("permissions");
        worlds = data.getStringList("worlds");
    }

    @Override
    public void apply(Hero hero) {

        Permission pex = RaidCraft.getPermissions();
        for (String world : worlds) {
            for (String perm : permissions) {
                pex.playerAdd(world, hero.getUserName(), perm);
            }
            for (String grp : groups) {
                pex.playerAddGroup(world, hero.getUserName(), grp);
            }
        }
    }

    @Override
    public void remove(Hero hero) {

        Permission pex = RaidCraft.getPermissions();
        for (String world : worlds) {
            for (String perm : permissions) {
                pex.playerRemove(world, hero.getUserName(), perm);
            }
            for (String grp : groups) {
                pex.playerRemoveGroup(world, hero.getUserName(), grp);
            }
        }
    }
}
