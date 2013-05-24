package de.raidcraft.skills.config;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.path.Path;
import de.raidcraft.skills.api.path.ProfessionPath;
import de.raidcraft.skills.api.profession.Profession;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public class PathConfig extends ConfigurationBase<SkillsPlugin> {

    public PathConfig(SkillsPlugin plugin) {

        super(plugin, "paths.yml");
    }

    public Set<Path<Profession>> getPaths() {

        Set<Path<Profession>> paths = new HashSet<>();
        for (String key : getKeys(false)) {
            paths.add(new ProfessionPath(this, key));
        }
        return paths;
    }

    public List<String> getParents(String path) {

        return getStringList(path + ".parents");
    }

    public String getFriendlyName(String path) {

        return getString(path + ".name", getName());
    }

    public int getPriority(String path) {

        return getInt(path + ".priority", 1);
    }

    public boolean isSelectedInCombat(String path) {

        return getBoolean(path + ".select-in-combat", true);
    }

    public boolean isSelectedOutOfCombat(String path) {

        return getBoolean(path + ".select-out-of-combat", true);
    }
}
