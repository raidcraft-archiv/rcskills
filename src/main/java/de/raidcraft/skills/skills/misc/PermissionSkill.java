package de.raidcraft.skills.skills.misc;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.AbstractSkill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.tables.THeroSkill;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;

/**
 * @author Silthus
 */
@SkillInformation(
        name = "permission-skill",
        desc = "Represents a generic permissions skill.",
        types = {EffectType.UNBINDABLE},
        triggerCombat = false
)
public class PermissionSkill extends AbstractSkill {

    private Collection<String> groups;
    private Collection<String> permissions;
    private Collection<String> worlds;

    protected PermissionSkill(Hero hero, SkillProperties data, Profession profession, THeroSkill database) {

        super(hero, data, profession, database);
    }


    @Override
    public void load(ConfigurationSection data) {

        groups = data.getStringList("groups");
        permissions = data.getStringList("permissions");
        worlds = data.getStringList("worlds");
    }

    @Override
    public void apply() {

        Permission pex = RaidCraft.getPermissions();
        for (String world : worlds) {
            for (String perm : permissions) {
                pex.playerAdd(world, getHero().getName(), perm);
            }
            for (String grp : groups) {
                pex.playerAddGroup(world, getHero().getName(), grp);
            }
        }
    }

    @Override
    public void remove() {

        Permission pex = RaidCraft.getPermissions();
        for (String world : worlds) {
            for (String perm : permissions) {
                pex.playerRemove(world, getHero().getName(), perm);
            }
            for (String grp : groups) {
                pex.playerRemoveGroup(world, getHero().getName(), grp);
            }
        }
    }
}
