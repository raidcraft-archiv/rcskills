package de.raidcraft.skills.config;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.persistance.Equipment;
import de.raidcraft.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public class EquipmentConfig extends ConfigurationBase<SkillsPlugin> {

    private final Map<String, Equipment> equipments = new HashMap<>();
    private final Map<String, Set<Equipment>> groups = new HashMap<>();

    public EquipmentConfig(SkillsPlugin plugin) {

        super(plugin, "equipment.yml");
    }

    @Override
    public void load() {

        super.load();
        ConfigurationSection section = getSafeConfigSection("equipment");
        // lets load the default mappings first
        for (String key : section.getKeys(false)) {
            section = section.getConfigurationSection(key);
            Material item = ItemUtils.getItem(section.getString("id"));
            if (item != null) {
                Equipment equipment = new Equipment(item, section.getInt("data"), section);
                equipments.put(key, equipment);
            } else {
                getPlugin().getLogger().warning("Unknown Item in equipment config " + key + ": " + section.getString("id"));
            }
        }
        // now lets load the groups
        section = getSafeConfigSection("groups");
        for (String key : section.getKeys(false)) {
            if (!groups.containsKey(key)) {
                groups.put(key, new LinkedHashSet<Equipment>());
            }
            for (String name : section.getStringList(key)) {
                groups.get(key).add(getEquipment(name));
            }
        }
    }

    public boolean hasEquipment(String name) {

        return equipments.containsKey(name);
    }

    public Equipment getEquipment(String name) {

        return equipments.get(name);
    }

    public boolean hasGroup(String name) {

        return groups.containsKey(name);
    }

    public Set<Equipment> getGroup(String name) {

        return groups.get(name);
    }
}
