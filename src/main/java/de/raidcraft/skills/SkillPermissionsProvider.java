package de.raidcraft.skills;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.permissions.PermissionsPlugin;
import de.raidcraft.permissions.groups.Group;
import de.raidcraft.permissions.groups.SimpleGroup;
import de.raidcraft.permissions.provider.PermissionsProvider;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.skills.PermissionSkill;
import de.raidcraft.skills.tables.THeroSkill;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public final class SkillPermissionsProvider implements PermissionsProvider<SkillsPlugin> {

    private final SkillsPlugin plugin;
    private final List<Group> groups = new ArrayList<>();
    private Group defaultGroup;

    protected SkillPermissionsProvider(SkillsPlugin plugin) {

        this.plugin = plugin;
        PermissionsPlugin rcPermissions = RaidCraft.getComponent(PermissionsPlugin.class);
        if (rcPermissions != null) {
            rcPermissions.registerProvider(this);
        } else {
            plugin.getLogger().warning("RCPermissions was not found! Not using our cool skill group feature :)");
            return;
        }
        load();
    }

    public void reload() {

        groups.clear();
        defaultGroup = null;
        load();
        // reload the permissions
        RaidCraft.getComponent(PermissionsPlugin.class).reload();
    }

    private void load() {

        String defaultGroup = plugin.getCommonConfig().default_permission_group;
        // every permission skill is handled as a group
        for (SkillFactory skillFactory : plugin.getSkillManager().getSkillFactoriesFor(PermissionSkill.class)) {
            try {
                Skill skill = skillFactory.createDummy();
                if (skill instanceof PermissionSkill) {
                    Set<String> globalPermissions = ((PermissionSkill) skill).getGlobalPermissions();
                    SimpleGroup simpleGroup = new SimpleGroup(this, skill.getName(),
                            ((PermissionSkill) skill).getWorldPermissions(),
                            globalPermissions.toArray(new String[globalPermissions.size()]));
                    groups.add(simpleGroup);
                    if (simpleGroup.getName().equalsIgnoreCase(defaultGroup)) {
                        this.defaultGroup = simpleGroup;
                    }
                }
            } catch (UnknownSkillException e) {
                plugin.getLogger().warning(e.getMessage());
            }
        }
        if (this.defaultGroup == null) {
            plugin.getLogger().warning("The default group defined in the config does not exist!");
        }
    }

    @Override
    public SkillsPlugin getPlugin() {

        return plugin;
    }

    @Override
    public Group getDefaultGroup() {

        return defaultGroup;
    }

    @Override
    public List<Group> getGroups() {

        return groups;
    }

    @Override
    public Set<String> getPlayerGroups(String player) {

        Set<String> groups = new HashSet<>();
        try {
            Hero hero = plugin.getCharacterManager().getHero(player);
            List<THeroSkill> skills = RaidCraft.getDatabase(SkillsPlugin.class).find(THeroSkill.class).where().eq("hero_id", hero.getId()).eq("unlocked", true).findList();
            for (THeroSkill skill : skills) {
                if (skill == null) {
                    continue;
                }
                if (plugin.getSkillManager().getFactory(skill.getName()).getSkillClass() == PermissionSkill.class) {
                    groups.add(skill.getName());
                }
            }
        } catch (UnknownPlayerException e) {
            plugin.getLogger().warning(e.getMessage());
        }
        return groups;
    }
}
