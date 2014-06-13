package de.raidcraft.skills.skills;

import de.raidcraft.RaidCraft;
import de.raidcraft.permissions.PermissionsPlugin;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.AbstractSkill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.tables.THeroSkill;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
@SkillInformation(
        name = "permission",
        description = "Represents a generic permissions skill.",
        triggerCombat = false
)
public class PermissionSkill extends AbstractSkill {

    // maps the worlds to their permissions
    private Map<String, Set<String>> worldPermissions = new HashMap<>();
    private Set<String> globalPermissions = new HashSet<>();
    private boolean timed = false;

    public PermissionSkill(Hero hero, SkillProperties data, Profession profession, THeroSkill database) {

        super(hero, data, profession, database);
    }

    @Override
    public void load(ConfigurationSection data) {

        for (World world : Bukkit.getWorlds()) {
            worldPermissions.put(world.getName(), new HashSet<>(data.getStringList(world.getName())));
        }
        globalPermissions.addAll(data.getStringList("global"));
        this.timed = data.getBoolean("timed", false);
    }

    public Map<String, Set<String>> getWorldPermissions() {

        return worldPermissions;
    }

    public Set<String> getGlobalPermissions() {

        return globalPermissions;
    }

    @Override
    public void apply() {

        if (getHolder().isOnline()) {
            RaidCraft.getComponent(PermissionsPlugin.class).getGroupManager().addPlayerToGroup(getHolder().getName(), getName());
        }
        if (timed) {
            try {
                addEffect(TimedPermissionSkill.class);
            } catch (CombatException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
            }
        }
    }

    @Override
    public void remove() {

        if (getHolder().isOnline()) {
            RaidCraft.getComponent(PermissionsPlugin.class).getGroupManager().removePlayerFromGroup(getHolder().getName(), getName());
        }
    }
}
