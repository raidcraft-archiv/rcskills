package de.raidcraft.skills;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.permissions.Group;
import de.raidcraft.api.permissions.GroupManager;
import de.raidcraft.api.permissions.RCPermissionsProvider;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.skills.PermissionSkill;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public final class SkillPermissionsProvider implements RCPermissionsProvider<SkillsPlugin> {

    private final SkillsPlugin plugin;
    private final List<Group> groups = new ArrayList<>();
    private Group defaultGroup;

    protected SkillPermissionsProvider(SkillsPlugin plugin) {

        this.plugin = plugin;
        RaidCraft.registerPermissionsProvider(this);
        load();
    }

    private void load() {

        GroupManager groupManager = RaidCraft.getPermissionGroupManager();
        if (groupManager == null) {
            plugin.getLogger().warning("Not registering RCSkill Skills as Permission Groups. No GroupManager found!");
            return;
        }

        String defaultGroup = plugin.getCommonConfig().default_permission_group;
        // every permission skill is handled as a group
        for (SkillFactory skillFactory : plugin.getSkillManager().getSkillFactoriesFor(PermissionSkill.class)) {
            try {
                Skill skill = skillFactory.createDummy();
                if (skill instanceof PermissionSkill) {
                    Set<String> globalPermissions = ((PermissionSkill) skill).getGlobalPermissions();
                    Group group = groupManager.createGroup(this, skill.getName(),
                            ((PermissionSkill) skill).getWorldPermissions(),
                            globalPermissions.toArray(new String[globalPermissions.size()]));
                    groups.add(group);
                    if (group.getName().equalsIgnoreCase(defaultGroup)) {
                        this.defaultGroup = group;
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

    public void reload() {

        groups.clear();
        defaultGroup = null;
        load();
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
    public Set<String> getPlayerGroups(UUID player) {

        Set<String> groups = new HashSet<>();
        Hero hero = plugin.getCharacterManager().getHero(player);
        if(hero == null) {
            try {
                throw new UnknownPlayerException("getHero: Player is null");
            } catch (UnknownPlayerException e) {
                e.printStackTrace();
            }
            return null;
        }
        groups.addAll(hero.getSkills().stream()
                .filter(skill -> skill.isActive()
                        && skill.isUnlocked()
                        && skill.isOfType(EffectType.PERMISSION))
                .map(Skill::getName)
                .collect(Collectors.toList()));
        return groups;
    }
}
